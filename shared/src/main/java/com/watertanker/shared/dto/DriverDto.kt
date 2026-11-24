package com.watertanker.shared.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Driver")
data class DriverDto(
    @JsonProperty("id")
    @Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    val id: Long? = null,

    @field:NotBlank @field:NotNull
    @JsonProperty("employee_id")
    @Schema(example = "EMP-001")
    val employeeId: String,

    @field:NotBlank @field:NotNull
    @JsonProperty("full_name")
    @Schema(example = "Rajesh Kumar")
    val fullName: String,

    @field:NotBlank @field:NotNull @field:Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    @JsonProperty("phone")
    @Schema(example = "+91-9876543210")
    val phone: String,

    @field:Email @field:NotBlank
    @JsonProperty("email")
    @Schema(example = "rajesh@example.com")
    val email: String,

    @field:NotNull
    @JsonProperty("license_number")
    @Schema(example = "KA01-2020-0012345")
    val licenseNumber: String,

    @field:NotNull
    @JsonProperty("license_expiry")
    @Schema(example = "2028-12-31")
    val licenseExpiry: java.time.LocalDate,

    @field:NotNull
    @JsonProperty("status")
    val status: com.watertanker.shared.enums.DriverStatus = com.watertanker.shared.enums.DriverStatus.AVAILABLE,

    @JsonProperty("current_location")
    val currentLocation: LocationDto? = null,

    @JsonProperty("assigned_tanker_id")
    @Schema(example = "1")
    val assignedTankerId: Long? = null,

    @JsonProperty("created_at")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    val createdAt: java.time.Instant? = null,

    @JsonProperty("updated_at")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    val updatedAt: java.time.Instant? = null
)