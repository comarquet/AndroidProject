package com.automacorp.service;

import androidx.core.text.isDigitsOnly
import com.automacorp.model.RoomDto
import com.automacorp.model.WindowDto
import com.automacorp.model.WindowStatus

object RoomService {
    val ROOM_KIND: List<String> = listOf("Room", "Meeting", "Laboratory", "Office", "Boardroom")
    val ROOM_NUMBER: List<Char> = ('A'..'Z').toList()
    val WINDOW_KIND: List<String> = listOf("Sliding", "Bay", "Casement", "Hung", "Fixed")

    fun generateWindow(id: Long, roomId: Long, roomName: String): WindowDto {
        return WindowDto(
            id = id,
            name = "${ WINDOW_KIND.random()} Window $id",
            roomName = roomName,
            roomId = roomId,
            windowStatus = WindowStatus.entries.toTypedArray().random()
        )
    }

    fun generateRoom(id: Long): RoomDto {
        val roomName = "${ROOM_NUMBER.random()}$id ${ROOM_KIND.random()}"
        val windows = (1..(1..6).random()).map { generateWindow(it.toLong(), id, roomName) }
        return RoomDto(
            id = id,
            name = roomName,
            currentTemperature = (15..30).random().toDouble(),
            targetTemperature = (15..22).random().toDouble(),
            windows = windows
        )
    }

    // Create 50 rooms
    val ROOMS = (1..50).map { generateRoom(it.toLong()) }.toMutableList()

    fun findAll(): List<RoomDto> {
        return ROOMS.sortedBy { it.name }
    }

    fun findById(id: Long): RoomDto? {
        return ROOMS.find { it.id == id }
    }

    fun findByName(name: String): RoomDto? {
        return ROOMS.find { it.name.equals(name, ignoreCase = true) }
    }

    fun updateRoom(id: Long, room: RoomDto): RoomDto? {
        val index = ROOMS.indexOfFirst { it.id == id }
        val updatedRoom = findById(id)?.copy(
            name = room.name,
            targetTemperature = room.targetTemperature,
            currentTemperature = room.currentTemperature
        ) ?: throw IllegalArgumentException()
        return ROOMS.set(index, updatedRoom)
    }

    fun findByNameOrId(nameOrId: String?): RoomDto? {
        if (nameOrId != null) {
            return if (nameOrId.isDigitsOnly()) {
                findById(nameOrId.toLong())
            } else {
                findByName(nameOrId)
            }
        }
        return null
    }
}
