package com.watertanker.shared.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Geographic location")
data class LocationDto(
    @field:NotNull @field:DecimalMin("-90.0") @field:DecimalMax("90.0")
    @JsonProperty("lat") @Schema(example = "12.9716")
    val latitude: Double,

    @field:NotNull @field:DecimalMin("-180.0") @field:DecimalMax("180.0")
    @JsonProperty("lng") @Schema(example = "77.5946")
    val longitude: Double,

    @JsonProperty("address")
    @Schema(example = "Koramangala, Bangalore")
    val address: String? = null
)