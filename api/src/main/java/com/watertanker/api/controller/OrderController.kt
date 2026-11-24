package com.watertanker.api.controller

import com.watertanker.api.service.OrderService
import com.watertanker.shared.dto.OrderDto
import com.watertanker.shared.enums.OrderStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Water delivery order management")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    @Operation(summary = "Create a new order")
    fun create(@Valid @RequestBody dto: OrderDto): ResponseEntity<OrderDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(dto))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    fun findById(@PathVariable id: Long): OrderDto {
        return orderService.findById(id)
    }

    @GetMapping
    @Operation(summary = "List all orders with pagination")
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "scheduledAt") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String
    ): Page<OrderDto> {
        val sort = if (sortDir.equals("desc", ignoreCase = true))
            Sort.by(sortBy).descending() else Sort.by(sortBy).ascending()
        val pageable = PageRequest.of(page, size, sort)
        return orderService.findAll(pageable)
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "List orders by customer")
    fun findByCustomerId(
        @PathVariable customerId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): Page<OrderDto> {
        val pageable = PageRequest.of(page, size, Sort.by("scheduledAt").descending())
        return orderService.findByCustomerId(customerId, pageable)
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List orders by status")
    fun findByStatus(
        @PathVariable status: OrderStatus,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): Page<OrderDto> {
        val pageable = PageRequest.of(page, size, Sort.by("scheduledAt").ascending())
        return orderService.findByStatus(status, pageable)
    }

    @GetMapping("/driver/{driverId}/active")
    @Operation(summary = "Get active orders for a driver")
    fun findActiveByDriverId(@PathVariable driverId: Long): List<OrderDto> {
        return orderService.findActiveByDriverId(driverId)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update order")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: OrderDto): OrderDto {
        return orderService.update(id, dto)
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestParam status: OrderStatus
    ): OrderDto {
        return orderService.updateStatus(id, status)
    }

    @PatchMapping("/{id}/assign-tanker")
    @Operation(summary = "Assign tanker to order")
    fun assignTanker(@PathVariable id: Long, @RequestParam tankerId: Long): OrderDto {
        return orderService.assignTanker(id, tankerId)
    }

    @PatchMapping("/{id}/assign-driver")
    @Operation(summary = "Assign driver to order")
    fun assignDriver(@PathVariable id: Long, @RequestParam driverId: Long): OrderDto {
        return orderService.assignDriver(id, driverId)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete order")
    fun delete(@PathVariable id: Long) {
        orderService.delete(id)
    }
}