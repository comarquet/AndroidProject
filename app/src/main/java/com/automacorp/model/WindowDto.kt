package com.automacorp.model

data class WindowDto(
    val id: Long,
    var name: String,
    val roomId: Long,
    var windowStatus: Double // Represents the status of the window (e.g., 0.0 for "Closed", 1.0 for "Opened")
)