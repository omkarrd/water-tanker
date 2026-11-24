package com.watertanker.api.repository

import com.watertanker.api.entity.Order
import com.watertanker.shared.enums.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface OrderRepository : JpaRepository<Order, Long> {

    fun findByOrderNumber(orderNumber: String): Order?

    fun findByCustomerId(customerId: Long, pageable: Pageable): Page<Order>

    fun findByTankerId(tankerId: Long, pageable: Pageable): Page<Order>

    fun findByDriverId(driverId: Long, pageable: Pageable): Page<Order>

    fun findByStatus(status: OrderStatus, pageable: Pageable): Page<Order>

    @Query("SELECT o FROM Order o WHERE o.status IN :statuses ORDER BY o.scheduledAt ASC")
    fun findByStatusIn(@Param("statuses") statuses: List<OrderStatus>): List<Order>

    @Query("SELECT o FROM Order o WHERE o.driverId = :driverId AND o.status IN :activeStatuses")
    fun findActiveByDriverId(@Param("driverId") driverId: Long, @Param("activeStatuses") activeStatuses: List<OrderStatus>): List<Order>

    @Query("SELECT COUNT(o) FROM Order o WHERE o.tankerId = :tankerId AND o.status IN :activeStatuses")
    fun countActiveByTankerId(@Param("tankerId") tankerId: Long, @Param("activeStatuses") activeStatuses: List<OrderStatus>): Long

    fun existsByOrderNumber(orderNumber: String): Boolean
}