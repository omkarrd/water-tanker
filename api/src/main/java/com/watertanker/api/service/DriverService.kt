package com.watertanker.api.service

import com.watertanker.api.entity.Driver
import com.watertanker.api.repository.DriverRepository
import com.watertanker.api.repository.TankerRepository
import com.watertanker.shared.dto.DriverDto
import com.watertanker.shared.enums.DriverStatus
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DriverService(
    private val driverRepository: DriverRepository,
    private val tankerRepository: TankerRepository
) {

    fun create(@Valid dto: DriverDto): DriverDto {
        if (driverRepository.existsByEmployeeId(dto.employeeId)) {
            throw IllegalArgumentException("Employee ID already exists: ${dto.employeeId}")
        }
        if (driverRepository.existsByEmail(dto.email)) {
            throw IllegalArgumentException("Email already exists: ${dto.email}")
        }
        if (driverRepository.existsByLicenseNumber(dto.licenseNumber)) {
            throw IllegalArgumentException("License number already exists: ${dto.licenseNumber}")
        }

        val driver = Driver(
            employeeId = dto.employeeId,
            fullName = dto.fullName,
            phone = dto.phone,
            email = dto.email,
            licenseNumber = dto.licenseNumber,
            licenseExpiry = dto.licenseExpiry,
            status = dto.status,
            currentLatitude = dto.currentLocation?.latitude,
            currentLongitude = dto.currentLocation?.longitude,
            currentAddress = dto.currentLocation?.address,
            assignedTankerId = dto.assignedTankerId
        )

        val saved = driverRepository.save(driver)
        return toDto(saved)
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): DriverDto {
        val driver = driverRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Driver not found: $id") }
        return toDto(driver)
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<DriverDto> {
        return driverRepository.findAll(pageable).map(this::toDto)
    }

    @Transactional(readOnly = true)
    fun findByStatus(status: DriverStatus, pageable: Pageable): Page<DriverDto> {
        return driverRepository.findByStatus(status, pageable).map(this::toDto)
    }

    @Transactional(readOnly = true)
    fun findAvailableWithLocation(): List<DriverDto> {
        return driverRepository.findAvailableWithLocation(DriverStatus.AVAILABLE).map(this::toDto)
    }

    fun update(id: Long, @Valid dto: DriverDto): DriverDto {
        val driver = driverRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Driver not found: $id") }

        if (dto.employeeId != driver.employeeId && driverRepository.existsByEmployeeId(dto.employeeId)) {
            throw IllegalArgumentException("Employee ID already exists: ${dto.employeeId}")
        }
        if (dto.email != driver.email && driverRepository.existsByEmail(dto.email)) {
            throw IllegalArgumentException("Email already exists: ${dto.email}")
        }
        if (dto.licenseNumber != driver.licenseNumber && driverRepository.existsByLicenseNumber(dto.licenseNumber)) {
            throw IllegalArgumentException("License number already exists: ${dto.licenseNumber}")
        }

        val updated = driver.copy(
            employeeId = dto.employeeId,
            fullName = dto.fullName,
            phone = dto.phone,
            email = dto.email,
            licenseNumber = dto.licenseNumber,
            licenseExpiry = dto.licenseExpiry,
            status = dto.status,
            currentLatitude = dto.currentLocation?.latitude,
            currentLongitude = dto.currentLocation?.longitude,
            currentAddress = dto.currentLocation?.address,
            assignedTankerId = dto.assignedTankerId
        )

        val saved = driverRepository.save(updated)
        return toDto(saved)
    }

    fun updateLocation(id: Long, latitude: Double, longitude: Double, address: String?): DriverDto {
        val driver = driverRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Driver not found: $id") }

        val updated = driver.copy(
            currentLatitude = latitude,
            currentLongitude = longitude,
            currentAddress = address
        )

        val saved = driverRepository.save(updated)
        return toDto(saved)
    }

    fun updateStatus(id: Long, status: DriverStatus): DriverDto {
        val driver = driverRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Driver not found: $id") }

        val updated = driver.copy(status = status)
        val saved = driverRepository.save(updated)
        return toDto(saved)
    }

    fun assignTanker(driverId: Long, tankerId: Long): DriverDto {
        val driver = driverRepository.findById(driverId)
            .orElseThrow { EntityNotFoundException("Driver not found: $driverId") }
        val tanker = tankerRepository.findById(tankerId)
            .orElseThrow { EntityNotFoundException("Tanker not found: $tankerId") }

        if (tanker.status != com.watertanker.shared.enums.TankerStatus.AVAILABLE &&
            tanker.status != com.watertanker.shared.enums.TankerStatus.ASSIGNED) {
            throw IllegalArgumentException("Tanker not available for assignment")
        }

        val updatedDriver = driver.copy(assignedTankerId = tankerId)
        val updatedTanker = tanker.copy(status = com.watertanker.shared.enums.TankerStatus.ASSIGNED)

        driverRepository.save(updatedDriver)
        tankerRepository.save(updatedTanker)

        return toDto(updatedDriver)
    }

    fun unassignTanker(driverId: Long): DriverDto {
        val driver = driverRepository.findById(driverId)
            .orElseThrow { EntityNotFoundException("Driver not found: $driverId") }

        if (driver.assignedTankerId != null) {
            tankerRepository.findById(driver.assignedTankerId!!).ifPresent { t ->
                tankerRepository.save(t.copy(status = com.watertanker.shared.enums.TankerStatus.AVAILABLE))
            }
        }

        val updated = driver.copy(assignedTankerId = null)
        val saved = driverRepository.save(updated)
        return toDto(saved)
    }

    fun delete(id: Long) {
        val driver = driverRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Driver not found: $id") }

        if (driver.assignedTankerId != null) {
            tankerRepository.findById(driver.assignedTankerId!!).ifPresent { t ->
                tankerRepository.save(t.copy(status = com.watertanker.shared.enums.TankerStatus.AVAILABLE))
            }
        }

        driverRepository.deleteById(id)
    }

    private fun toDto(driver: Driver): DriverDto = DriverDto(
        id = driver.id,
        employeeId = driver.employeeId,
        fullName = driver.fullName,
        phone = driver.phone,
        email = driver.email,
        licenseNumber = driver.licenseNumber,
        licenseExpiry = driver.licenseExpiry,
        status = driver.status,
        currentLocation = driver.toLocationDto(),
        assignedTankerId = driver.assignedTankerId,
        createdAt = driver.createdAt,
        updatedAt = driver.updatedAt
    )
}