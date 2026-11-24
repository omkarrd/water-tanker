package com.watertanker.api.controller

import com.watertanker.api.service.TankerService
import com.watertanker.shared.dto.TankerDto
import com.watertanker.shared.enums.TankerStatus
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
@RequestMapping("/api/tankers")
@Tag(name = "Tankers", description = "Water tanker management")
class TankerController(
    private val tankerService: TankerService
) {

    @PostMapping
    @Operation(summary = "Create a new tanker")
    fun create(@Valid @RequestBody dto: TankerDto): ResponseEntity<TankerDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(tankerService.create(dto))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tanker by ID")
    fun findById(@PathVariable id: Long): TankerDto {
        return tankerService.findById(id)
    }

    @GetMapping
    @Operation(summary = "List all tankers with pagination")
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "id") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String
    ): Page<TankerDto> {
        val sort = if (sortDir.equals("desc", ignoreCase = true))
            Sort.by(sortBy).descending() else Sort.by(sortBy).ascending()
        val pageable = PageRequest.of(page, size, sort)
        return tankerService.findAll(pageable)
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List tankers by status")
    fun findByStatus(
        @PathVariable status: TankerStatus,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): Page<TankerDto> {
        val pageable = PageRequest.of(page, size)
        return tankerService.findByStatus(status, pageable)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tanker")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: TankerDto): TankerDto {
        return tankerService.update(id, dto)
    }

    @PatchMapping("/{id}/location")
    @Operation(summary = "Update tanker location")
    fun updateLocation(
        @PathVariable id: Long,
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam(required = false) address: String?
    ): TankerDto {
        return tankerService.updateLocation(id, latitude, longitude, address)
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update tanker status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestParam status: TankerStatus
    ): TankerDto {
        return tankerService.updateStatus(id, status)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete tanker")
    fun delete(@PathVariable id: Long) {
        tankerService.delete(id)
    }
}