package com.automacorp.model

data class WindowCommandDto(
    val name: String,
    val roomId: Long,
    val windowStatus: Double // Replace `sensor` with `windowStatus`
)