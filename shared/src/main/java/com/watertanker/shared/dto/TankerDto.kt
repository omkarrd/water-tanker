package com.watertanker.shared.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Water tanker")
data class TankerDto(
    @JsonProperty("id")
    @Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    val id: Long? = null,

    @field:NotBlank @field:NotNull
    @JsonProperty("registration_number")
    @Schema(example = "KA-01-AB-1234")
    val registrationNumber: String,

    @field:NotBlank @field:NotNull
    @JsonProperty("driver_name")
    @Schema(example = "Rajesh Kumar")
    val driverName: String,

    @field:NotBlank @field:NotNull
    @JsonProperty("driver_phone")
    @Schema(example = "+91-9876543210")
    val driverPhone: String,

    @field:Positive @field:NotNull
    @JsonProperty("capacity_liters")
    @Schema(example = "10000")
    val capacityLiters: Int,

    @field:NotNull
    @JsonProperty("status")
    val status: com.watertanker.shared.enums.TankerStatus,

    @JsonProperty("current_location")
    val currentLocation: LocationDto? = null,

    @JsonProperty("created_at")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    val createdAt: java.time.Instant? = null,

    @JsonProperty("updated_at")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    val updatedAt: java.time.Instant? = null
)