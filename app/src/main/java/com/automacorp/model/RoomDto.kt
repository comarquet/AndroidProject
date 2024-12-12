package com.automacorp.model

import com.squareup.moshi.Json

data class RoomDto(
    val Id: Long,
    val floor: Int?,
    var name: String,
    val currentTemperature: Double?,
    val targetTemperature: Double?,
    val windows: List<WindowCommandDto>,
    val heaters: List<HeaterDto> = emptyList(),
    @Json(name = "BuildingID") val buildingId: Long?
)
