package com.watertanker.shared.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class DriverStatus(@JsonValue val value: String) {
    AVAILABLE("available"),
    ON_DUTY("on_duty"),
    DRIVING("driving"),
    ON_BREAK("on_break"),
    OFFLINE("offline"),
    SUSPENDED("suspended");

    @JsonCreator
    companion object {
        @JvmStatic
        fun fromValue(value: String): DriverStatus = values().first { it.value == value }
    }
}