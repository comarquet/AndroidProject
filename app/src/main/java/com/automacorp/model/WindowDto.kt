package com.automacorp.model

data class WindowDto(
    val id: Long,
    var name: String,
    val windowStatus: Double,
    val roomId: Long
)
