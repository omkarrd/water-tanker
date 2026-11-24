package com.watertanker.api.entity

import com.watertanker.shared.enums.TankerStatus
import jakarta.persistence.*
import lombok.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "tankers", indexes = [
    Index(name = "idx_tanker_status", columnList = "status"),
    Index(name = "idx_tanker_registration", columnList = "registration_number", unique = true)
])
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener::class)
class Tanker(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "registration_number", nullable = false, unique = true, length = 20)
    val registrationNumber: String,

    @Column(name = "driver_name", nullable = false, length = 100)
    val driverName: String,

    @Column(name = "driver_phone", nullable = false, length = 20)
    val driverPhone: String,

    @Column(name = "capacity_liters", nullable = false)
    val capacityLiters: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    val status: TankerStatus = TankerStatus.AVAILABLE,

    @Column(name = "current_latitude", precision = 10, scale = 8)
    val currentLatitude: Double? = null,

    @Column(name = "current_longitude", precision = 11, scale = 8)
    val currentLongitude: Double? = null,

    @Column(name = "current_address", length = 255)
    val currentAddress: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant? = null
) {
    fun toLocationDto() = if (currentLatitude != null && currentLongitude != null) {
        com.watertanker.shared.dto.LocationDto(currentLatitude!!, currentLongitude!!, currentAddress)
    } else null
}