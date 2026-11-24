package com.watertanker.api.controller

import com.watertanker.api.service.DriverService
import com.watertanker.shared.dto.DriverDto
import com.watertanker.shared.enums.DriverStatus
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
@RequestMapping("/api/drivers")
@Tag(name = "Drivers", description = "Driver management")
class DriverController(
    private val driverService: DriverService
) {

    @PostMapping
    @Operation(summary = "Create a new driver")
    fun create(@Valid @RequestBody dto: DriverDto): ResponseEntity<DriverDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.create(dto))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID")
    fun findById(@PathVariable id: Long): DriverDto {
        return driverService.findById(id)
    }

    @GetMapping
    @Operation(summary = "List all drivers with pagination")
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "id") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String
    ): Page<DriverDto> {
        val sort = if (sortDir.equals("desc", ignoreCase = true))
            Sort.by(sortBy).descending() else Sort.by(sortBy).ascending()
        val pageable = PageRequest.of(page, size, sort)
        return driverService.findAll(pageable)
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List drivers by status")
    fun findByStatus(
        @PathVariable status: DriverStatus,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): Page<DriverDto> {
        val pageable = PageRequest.of(page, size)
        return driverService.findByStatus(status, pageable)
    }

    @GetMapping("/available")
    @Operation(summary = "List available drivers with location")
    fun findAvailable(): List<DriverDto> {
        return driverService.findAvailableWithLocation()
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update driver")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: DriverDto): DriverDto {
        return driverService.update(id, dto)
    }

    @PatchMapping("/{id}/location")
    @Operation(summary = "Update driver location")
    fun updateLocation(
        @PathVariable id: Long,
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam(required = false) address: String?
    ): DriverDto {
        return driverService.updateLocation(id, latitude, longitude, address)
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update driver status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestParam status: DriverStatus
    ): DriverDto {
        return driverService.updateStatus(id, status)
    }

    @PatchMapping("/{id}/assign-tanker")
    @Operation(summary = "Assign tanker to driver")
    fun assignTanker(@PathVariable id: Long, @RequestParam tankerId: Long): DriverDto {
        return driverService.assignTanker(id, tankerId)
    }

    @PatchMapping("/{id}/unassign-tanker")
    @Operation(summary = "Unassign tanker from driver")
    fun unassignTanker(@PathVariable id: Long): DriverDto {
        return driverService.unassignTanker(id)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete driver")
    fun delete(@PathVariable id: Long) {
        driverService.delete(id)
    }
}