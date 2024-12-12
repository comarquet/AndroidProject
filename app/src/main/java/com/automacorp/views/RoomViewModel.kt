package com.automacorp.views

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automacorp.RoomList
import com.automacorp.model.RoomCommandDto
import com.automacorp.model.RoomDto
import com.automacorp.service.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {
    var room by mutableStateOf<RoomDto?>(null)
    val roomsState = MutableStateFlow(RoomList())

    fun findAll() {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.findAll().execute() }
                .onSuccess { response ->
                    val rooms = response.body() ?: emptyList()
                    roomsState.value = RoomList(rooms)
                }
                .onFailure { exception ->
                    exception.printStackTrace()
                    roomsState.value = RoomList(emptyList(), exception.stackTraceToString())
                }
        }
    }


    fun findRoomFromList(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.findById(id).execute() }
                .onSuccess {
                    if (it.isSuccessful && it.body() != null) {
                        room = it.body()
                    } else {
                        room = null
                    }
                }
                .onFailure {
                    it.printStackTrace()
                    Log.e("RoomViewModel", "API call failed: ${it.message}")
                    room = null
                }
        }
    }


    fun updateRoom(id: Long, roomDto: RoomDto) {
        val command = RoomCommandDto(
            name = roomDto.name,
            targetTemperature = roomDto.targetTemperature?.let { Math.round(it * 10) / 10.0 },
            currentTemperature = roomDto.currentTemperature,
            floor = roomDto.floor,
            buildingId = roomDto.buildingId
        )

        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.updateRoom(id, command).execute() }
                .onSuccess { response ->
                    room = response.body()
                }
                .onFailure { exception ->
                    if (exception is retrofit2.HttpException) {
                    } else {
                        Log.e("RoomUpdate", "Exception: ${exception.message}")
                    }
                    room = null
                }
        }
    }

    fun createRoom(roomDto: RoomDto, onComplete: (RoomDto) -> Unit) {
        val command = RoomCommandDto(
            name = roomDto.name,
            targetTemperature = roomDto.targetTemperature?.let { Math.round(it * 10) / 10.0 },
            currentTemperature = roomDto.currentTemperature,
            floor = 1,
            buildingId = -10,
            windows = roomDto.windows
        )
        println("Request Body: $command")
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.createRoom(command).execute() }
                .onSuccess { response ->
                    val createdRoom = response.body()
                    if (createdRoom != null) {
                        room = createdRoom
                        println("Response: $createdRoom")
                        onComplete(createdRoom)
                    } else {
                        println("Failed to create room: Response body is null")
                    }
                }
                .onFailure {
                    it.printStackTrace()
                }
        }
    }


    fun deleteRoom(id: Long, onComplete: () -> Unit) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.deleteRoom(id).execute() }
                .onSuccess {
                    onComplete()
                }
                .onFailure {
                    it.printStackTrace()
                    onComplete() // Ensure we return to the UI even on failure
                }
        }
    }
}
