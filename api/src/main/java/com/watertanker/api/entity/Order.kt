package com.watertanker.api.entity

import com.watertanker.shared.enums.OrderStatus
import jakarta.persistence.*
import lombok.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "orders", indexes = [
    Index(name = "idx_order_status", columnList = "status"),
    Index(name = "idx_order_customer", columnList = "customer_id"),
    Index(name = "idx_order_tanker", columnList = "tanker_id"),
    Index(name = "idx_order_driver", columnList = "driver_id"),
    Index(name = "idx_order_scheduled", columnList = "scheduled_at"),
    Index(name = "idx_order_number", columnList = "order_number", unique = true)
])
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener::class)
class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "order_number", nullable = false, unique = true, length = 30)
    val orderNumber: String,

    @Column(name = "customer_id", nullable = false)
    val customerId: Long,

    @Column(name = "tanker_id")
    val tankerId: Long? = null,

    @Column(name = "driver_id")
    val driverId: Long? = null,

    @Column(name = "source_latitude", nullable = false, precision = 10, scale = 8)
    val sourceLatitude: Double,

    @Column(name = "source_longitude", nullable = false, precision = 11, scale = 8)
    val sourceLongitude: Double,

    @Column(name = "source_address", length = 255)
    val sourceAddress: String? = null,

    @Column(name = "destination_latitude", nullable = false, precision = 10, scale = 8)
    val destinationLatitude: Double,

    @Column(name = "destination_longitude", nullable = false, precision = 11, scale = 8)
    val destinationLongitude: Double,

    @Column(name = "destination_address", length = 255)
    val destinationAddress: String? = null,

    @Column(name = "quantity_liters", nullable = false)
    val quantityLiters: Int,

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    val price: Double = 0.0,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    val status: OrderStatus = OrderStatus.PENDING,

    @Column(name = "scheduled_at")
    val scheduledAt: Instant? = null,

    @Column(name = "started_at")
    val startedAt: Instant? = null,

    @Column(name = "completed_at")
    val completedAt: Instant? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant? = null
) {
    fun toSourceLocationDto() = com.watertanker.shared.dto.LocationDto(sourceLatitude, sourceLongitude, sourceAddress)
    fun toDestinationLocationDto() = com.watertanker.shared.dto.LocationDto(destinationLatitude, destinationLongitude, destinationAddress)
}