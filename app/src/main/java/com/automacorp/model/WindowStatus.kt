package com.automacorp.model

enum class WindowStatus {
    OPENED,
    CLOSED;

    companion object {
        fun fromDouble(value: Double): WindowStatus = when (value) {
            1.0 -> OPENED
            0.0 -> CLOSED
            else -> throw IllegalArgumentException("Unknown windowStatus value: $value")
        }
    }
}