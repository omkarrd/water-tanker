package com.watertanker.shared.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class TankerStatus(@JsonValue val value: String) {
    AVAILABLE("available"),
    ASSIGNED("assigned"),
    IN_TRANSIT("in_transit"),
    LOADING("loading"),
    UNLOADING("unloading"),
    MAINTENANCE("maintenance"),
    OFFLINE("offline");

    @JsonCreator
    companion object {
        @JvmStatic
        fun fromValue(value: String): TankerStatus = values().first { it.value == value }
    }
}