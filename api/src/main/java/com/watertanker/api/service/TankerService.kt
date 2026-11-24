package com.watertanker.api.service

import com.watertanker.api.entity.Tanker
import com.watertanker.api.repository.TankerRepository
import com.watertanker.shared.dto.TankerDto
import com.watertanker.shared.enums.TankerStatus
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TankerService(
    private val tankerRepository: TankerRepository
) {

    fun create(@Valid dto: TankerDto): TankerDto {
        if (tankerRepository.existsByRegistrationNumber(dto.registrationNumber)) {
            throw IllegalArgumentException("Registration number already exists: ${dto.registrationNumber}")
        }

        val tanker = Tanker(
            registrationNumber = dto.registrationNumber,
            driverName = dto.driverName,
            driverPhone = dto.driverPhone,
            capacityLiters = dto.capacityLiters,
            status = dto.status,
            currentLatitude = dto.currentLocation?.latitude,
            currentLongitude = dto.currentLocation?.longitude,
            currentAddress = dto.currentLocation?.address
        )

        val saved = tankerRepository.save(tanker)
        return toDto(saved)
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): TankerDto {
        val tanker = tankerRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Tanker not found: $id") }
        return toDto(tanker)
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<TankerDto> {
        return tankerRepository.findAll(pageable).map(this::toDto)
    }

    @Transactional(readOnly = true)
    fun findByStatus(status: TankerStatus, pageable: Pageable): Page<TankerDto> {
        return tankerRepository.findByStatus(status, pageable).map(this::toDto)
    }

    fun update(id: Long, @Valid dto: TankerDto): TankerDto {
        val tanker = tankerRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Tanker not found: $id") }

        if (dto.registrationNumber != tanker.registrationNumber &&
            tankerRepository.existsByRegistrationNumber(dto.registrationNumber)) {
            throw IllegalArgumentException("Registration number already exists: ${dto.registrationNumber}")
        }

        val updated = tanker.copy(
            registrationNumber = dto.registrationNumber,
            driverName = dto.driverName,
            driverPhone = dto.driverPhone,
            capacityLiters = dto.capacityLiters,
            status = dto.status,
            currentLatitude = dto.currentLocation?.latitude,
            currentLongitude = dto.currentLocation?.longitude,
            currentAddress = dto.currentLocation?.address
        )

        val saved = tankerRepository.save(updated)
        return toDto(saved)
    }

    fun updateLocation(id: Long, latitude: Double, longitude: Double, address: String?): TankerDto {
        val tanker = tankerRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Tanker not found: $id") }

        val updated = tanker.copy(
            currentLatitude = latitude,
            currentLongitude = longitude,
            currentAddress = address
        )

        val saved = tankerRepository.save(updated)
        return toDto(saved)
    }

    fun updateStatus(id: Long, status: TankerStatus): TankerDto {
        val tanker = tankerRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Tanker not found: $id") }

        val updated = tanker.copy(status = status)
        val saved = tankerRepository.save(updated)
        return toDto(saved)
    }

    fun delete(id: Long) {
        if (!tankerRepository.existsById(id)) {
            throw EntityNotFoundException("Tanker not found: $id")
        }
        tankerRepository.deleteById(id)
    }

    private fun toDto(tanker: Tanker): TankerDto = TankerDto(
        id = tanker.id,
        registrationNumber = tanker.registrationNumber,
        driverName = tanker.driverName,
        driverPhone = tanker.driverPhone,
        capacityLiters = tanker.capacityLiters,
        status = tanker.status,
        currentLocation = tanker.toLocationDto(),
        createdAt = tanker.createdAt,
        updatedAt = tanker.updatedAt
    )
}