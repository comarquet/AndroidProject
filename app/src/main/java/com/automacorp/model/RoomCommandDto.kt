package com.automacorp.model

data class RoomCommandDto(
    val name: String?,
    val currentTemperature: Double?,
    val targetTemperature: Double?,
    val floor: Int?,
    val buildingId: Long?,
    val windows: List<WindowDto> = emptyList()
)
