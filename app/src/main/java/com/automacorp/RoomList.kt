package com.automacorp

import com.automacorp.model.RoomDto

class RoomList(
    val rooms: List<RoomDto> = emptyList(),
    val error: String? = null
)