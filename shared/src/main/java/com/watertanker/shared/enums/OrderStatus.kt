package com.watertanker.shared.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class OrderStatus(@JsonValue val value: String) {
    PENDING("pending"),
    CONFIRMED("confirmed"),
    ASSIGNED("assigned"),
    DISPATCHED("dispatched"),
    IN_PROGRESS("in_progress"),
    DELIVERED("delivered"),
    CANCELLED("cancelled"),
    FAILED("failed");

    @JsonCreator
    companion object {
        @JvmStatic
        fun fromValue(value: String): OrderStatus = values().first { it.value == value }
    }
}