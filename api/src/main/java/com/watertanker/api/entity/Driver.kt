package com.watertanker.api.entity

import com.watertanker.shared.enums.DriverStatus
import jakarta.persistence.*
import lombok.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "drivers", indexes = [
    Index(name = "idx_driver_status", columnList = "status"),
    Index(name = "idx_driver_employee", columnList = "employee_id", unique = true),
    Index(name = "idx_driver_email", columnList = "email", unique = true),
    Index(name = "idx_driver_license", columnList = "license_number", unique = true)
])
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener::class)
class Driver(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "employee_id", nullable = false, unique = true, length = 20)
    val employeeId: String,

    @Column(name = "full_name", nullable = false, length = 100)
    val fullName: String,

    @Column(name = "phone", nullable = false, length = 20)
    val phone: String,

    @Column(name = "email", nullable = false, unique = true, length = 100)
    val email: String,

    @Column(name = "license_number", nullable = false, unique = true, length = 30)
    val licenseNumber: String,

    @Column(name = "license_expiry", nullable = false)
    val licenseExpiry: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    val status: DriverStatus = DriverStatus.AVAILABLE,

    @Column(name = "current_latitude", precision = 10, scale = 8)
    val currentLatitude: Double? = null,

    @Column(name = "current_longitude", precision = 11, scale = 8)
    val currentLongitude: Double? = null,

    @Column(name = "current_address", length = 255)
    val currentAddress: String? = null,

    @Column(name = "assigned_tanker_id")
    val assignedTankerId: Long? = null,

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