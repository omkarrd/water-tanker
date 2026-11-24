package com.watertanker.api.repository

import com.watertanker.api.entity.Driver
import com.watertanker.shared.enums.DriverStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DriverRepository : JpaRepository<Driver, Long> {

    fun findByEmployeeId(employeeId: String): Driver?

    fun findByEmail(email: String): Driver?

    fun findByLicenseNumber(licenseNumber: String): Driver?

    fun findByStatus(status: DriverStatus, pageable: Pageable): Page<Driver>

    fun findByAssignedTankerId(assignedTankerId: Long): Driver?

    @Query("SELECT d FROM Driver d WHERE d.status = :status AND d.currentLatitude IS NOT NULL AND d.currentLongitude IS NOT NULL")
    fun findAvailableWithLocation(@Param("status") status: DriverStatus): List<Driver>

    fun existsByEmployeeId(employeeId: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun existsByLicenseNumber(licenseNumber: String): Boolean
}