package com.watertanker.api.service

import com.watertanker.api.entity.Order
import com.watertanker.api.repository.OrderRepository
import com.watertanker.api.repository.TankerRepository
import com.watertanker.api.repository.DriverRepository
import com.watertanker.shared.dto.OrderDto
import com.watertanker.shared.enums.OrderStatus
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository,
    private val tankerRepository: TankerRepository,
    private val driverRepository: DriverRepository
) {

    companion object {
        private const val ACTIVE_STATUSES = listOf(
            OrderStatus.CONFIRMED,
            OrderStatus.ASSIGNED,
            OrderStatus.DISPATCHED,
            OrderStatus.IN_PROGRESS
        )
    }

    fun create(@Valid dto: OrderDto): OrderDto {
        // Validate tanker exists and is available
        if (dto.tankerId != null) {
            val tanker = tankerRepository.findById(dto.tankerId!!)
                .orElseThrow { EntityNotFoundException("Tanker not found: ${dto.tankerId}") }
            if (tanker.status != com.watertanker.shared.enums.TankerStatus.AVAILABLE) {
                throw IllegalArgumentException("Tanker not available: ${tanker.status}")
            }
        }

        // Validate driver exists and is available
        if (dto.driverId != null) {
            val driver = driverRepository.findById(dto.driverId!!)
                .orElseThrow { EntityNotFoundException("Driver not found: ${dto.driverId}") }
            if (driver.status != com.watertanker.shared.enums.DriverStatus.AVAILABLE) {
                throw IllegalArgumentException("Driver not available: ${driver.status}")
            }
        }

        val orderNumber = dto.orderNumber ?: generateOrderNumber()

        if (orderRepository.existsByOrderNumber(orderNumber)) {
            throw IllegalArgumentException("Order number already exists: $orderNumber")
        }

        val order = Order(
            orderNumber = orderNumber,
            customerId = dto.customerId,
            tankerId = dto.tankerId,
            driverId = dto.driverId,
            sourceLatitude = dto.sourceLocation.latitude,
            sourceLongitude = dto.sourceLocation.longitude,
            sourceAddress = dto.sourceLocation.address,
            destinationLatitude = dto.destinationLocation.latitude,
            destinationLongitude = dto.destinationLocation.longitude,
            destinationAddress = dto.destinationLocation.address,
            quantityLiters = dto.quantityLiters,
            price = dto.price,
            status = dto.status,
            scheduledAt = dto.scheduledAt
        )

        val saved = orderRepository.save(order)

        // Update tanker/driver status if assigned
        if (dto.tankerId != null) {
            tankerRepository.findById(dto.tankerId!!).ifPresent { t ->
                tankerRepository.save(t.copy(status = com.watertanker.shared.enums.TankerStatus.ASSIGNED))
            }
        }
        if (dto.driverId != null) {
            driverRepository.findById(dto.driverId!!).ifPresent { d ->
                driverRepository.save(d.copy(status = com.watertanker.shared.enums.DriverStatus.ON_DUTY))
            }
        }

        return toDto(saved)
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): OrderDto {
        val order = orderRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Order not found: $id") }
        return toDto(order)
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<OrderDto> {
        return orderRepository.findAll(pageable).map(this::toDto)
    }

    @Transactional(readOnly = true)
    fun findByCustomerId(customerId: Long, pageable: Pageable): Page<OrderDto> {
        return orderRepository.findByCustomerId(customerId, pageable).map(this::toDto)
    }

    @Transactional(readOnly = true)
    fun findByStatus(status: OrderStatus, pageable: Pageable): Page<OrderDto> {
        return orderRepository.findByStatus(status, pageable).map(this::toDto)
    }

    @Transactional(readOnly = true)
    fun findActiveByDriverId(driverId: Long): List<OrderDto> {
        return orderRepository.findActiveByDriverId(driverId, ACTIVE_STATUSES).map(this::toDto)
    }

    fun update(id: Long, @Valid dto: OrderDto): OrderDto {
        val order = orderRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Order not found: $id") }

        val updated = order.copy(
            tankerId = dto.tankerId,
            driverId = dto.driverId,
            sourceLatitude = dto.sourceLocation.latitude,
            sourceLongitude = dto.sourceLocation.longitude,
            sourceAddress = dto.sourceLocation.address,
            destinationLatitude = dto.destinationLocation.latitude,
            destinationLongitude = dto.destinationLocation.longitude,
            destinationAddress = dto.destinationLocation.address,
            quantityLiters = dto.quantityLiters,
            price = dto.price,
            status = dto.status,
            scheduledAt = dto.scheduledAt
        )

        val saved = orderRepository.save(updated)
        return toDto(saved)
    }

    fun updateStatus(id: Long, status: OrderStatus): OrderDto {
        val order = orderRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Order not found: $id") }

        val now = Instant.now()
        val updated = when (status) {
            OrderStatus.IN_PROGRESS -> order.copy(status = status, startedAt = now)
            OrderStatus.DELIVERED -> order.copy(status = status, completedAt = now)
            else -> order.copy(status = status)
        }

        val saved = orderRepository.save(updated)

        // Release tanker/driver when order completes
        if (status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED) {
            if (saved.tankerId != null) {
                tankerRepository.findById(saved.tankerId!!).ifPresent { t ->
                    tankerRepository.save(t.copy(status = com.watertanker.shared.enums.TankerStatus.AVAILABLE))
                }
            }
            if (saved.driverId != null) {
                driverRepository.findById(saved.driverId!!).ifPresent { d ->
                    driverRepository.save(d.copy(status = com.watertanker.shared.enums.DriverStatus.AVAILABLE))
                }
            }
        }

        return toDto(saved)
    }

    fun assignTanker(id: Long, tankerId: Long): OrderDto {
        val order = orderRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Order not found: $id") }
        val tanker = tankerRepository.findById(tankerId)
            .orElseThrow { EntityNotFoundException("Tanker not found: $tankerId") }

        if (tanker.status != com.watertanker.shared.enums.TankerStatus.AVAILABLE) {
            throw IllegalArgumentException("Tanker not available")
        }

        val updatedOrder = order.copy(tankerId = tankerId, status = OrderStatus.ASSIGNED)
        val updatedTanker = tanker.copy(status = com.watertanker.shared.enums.TankerStatus.ASSIGNED)

        orderRepository.save(updatedOrder)
        tankerRepository.save(updatedTanker)

        return toDto(updatedOrder)
    }

    fun assignDriver(id: Long, driverId: Long): OrderDto {
        val order = orderRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Order not found: $id") }
        val driver = driverRepository.findById(driverId)
            .orElseThrow { EntityNotFoundException("Driver not found: $driverId") }

        if (driver.status != com.watertanker.shared.enums.DriverStatus.AVAILABLE) {
            throw IllegalArgumentException("Driver not available")
        }

        val updatedOrder = order.copy(driverId = driverId, status = OrderStatus.DISPATCHED)
        val updatedDriver = driver.copy(status = com.watertanker.shared.enums.DriverStatus.ON_DUTY)

        orderRepository.save(updatedOrder)
        driverRepository.save(updatedDriver)

        return toDto(updatedOrder)
    }

    fun delete(id: Long) {
        if (!orderRepository.existsById(id)) {
            throw EntityNotFoundException("Order not found: $id")
        }
        orderRepository.deleteById(id)
    }

    private fun toDto(order: Order): OrderDto = OrderDto(
        id = order.id,
        orderNumber = order.orderNumber,
        customerId = order.customerId,
        tankerId = order.tankerId,
        driverId = order.driverId,
        sourceLocation = order.toSourceLocationDto(),
        destinationLocation = order.toDestinationLocationDto(),
        quantityLiters = order.quantityLiters,
        price = order.price,
        status = order.status,
        scheduledAt = order.scheduledAt,
        startedAt = order.startedAt,
        completedAt = order.completedAt,
        createdAt = order.createdAt,
        updatedAt = order.updatedAt
    )

    private fun generateOrderNumber(): String {
        return "WT-${java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))}-${UUID.randomUUID().toString().substring(0, 6).uppercase()}"
    }
}