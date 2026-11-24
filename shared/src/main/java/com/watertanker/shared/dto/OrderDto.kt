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
@Schema(description = "Water delivery order")
data class OrderDto(
    @JsonProperty("id")
    @Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    val id: Long? = null,

    @field:NotBlank @field:NotNull
    @JsonProperty("order_number")
    @Schema(example = "WT-2024-001234", accessMode = Schema.AccessMode.READ_ONLY)
    val orderNumber: String? = null,

    @field:NotNull
    @JsonProperty("customer_id")
    @Schema(example = "1")
    val customerId: Long,

    @field:NotNull
    @JsonProperty("tanker_id")
    @Schema(example = "1")
    val tankerId: Long? = null,

    @field:NotNull
    @JsonProperty("driver_id")
    @Schema(example = "1")
    val driverId: Long? = null,

    @field:NotNull
    @JsonProperty("source_location")
    val sourceLocation: LocationDto,

    @field:NotNull
    @JsonProperty("destination_location")
    val destinationLocation: LocationDto,

    @field:Positive @field:NotNull
    @JsonProperty("quantity_liters")
    @Schema(example = "5000")
    val quantityLiters: Int,

    @field:Min(0)
    @JsonProperty("price")
    @Schema(example = "2500.00")
    val price: Double = 0.0,

    @field:NotNull
    @JsonProperty("status")
    val status: com.watertanker.shared.enums.OrderStatus = com.watertanker.shared.enums.OrderStatus.PENDING,

    @JsonProperty("scheduled_at")
    @Schema(example = "2024-01-15T10:00:00Z")
    val scheduledAt: java.time.Instant? = null,

    @JsonProperty("started_at")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    val startedAt: java.time.Instant? = null,

    @JsonProperty("completed_at")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    val completedAt: java.time.Instant? = null,

    @JsonProperty("created_at")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    val createdAt: java.time.Instant? = null,

    @JsonProperty("updated_at")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    val updatedAt: java.time.Instant? = null
)