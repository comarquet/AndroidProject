package com.automacorp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automacorp.model.RoomCommandDto
import com.automacorp.model.RoomDto
import com.automacorp.model.WindowDto
import com.automacorp.service.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {
    var room by mutableStateOf<RoomDto?>(null)
    val roomsState = MutableStateFlow(RoomList())
    val windowsState = MutableStateFlow<List<WindowDto>>(emptyList())
    val selectedWindowState = MutableStateFlow<WindowDto?>(null)

    fun findAll() {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.findAll().execute() }
                .onSuccess {
                    val rooms = it.body() ?: emptyList()
                    roomsState.value = RoomList(rooms)
                }
                .onFailure {
                    it.printStackTrace()
                    roomsState.value = RoomList(emptyList(), it.stackTraceToString())
                }
        }
    }

    fun findRoomFromList(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.findById(id).execute() }
                .onSuccess {
                    room = it.body()
                }
                .onFailure {
                    it.printStackTrace()
                    room = null
                }
        }
    }

    fun updateRoom(id: Long, roomDto: RoomDto) {
        val command = RoomCommandDto(
            name = roomDto.name,
            targetTemperature = roomDto.targetTemperature?.let { Math.round(it * 10) / 10.0 },
            currentTemperature = roomDto.currentTemperature,
            floor = 1,
            buildingId = -10
        )

        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.updateRoom(id, command).execute() }
                .onSuccess { response ->
                    room = response.body()
                }
                .onFailure { exception ->
                    if (exception is retrofit2.HttpException) {
                        println("HTTP Exception: ${exception.code()}, ${exception.response()?.errorBody()?.string()}")
                    } else {
                        println("Exception: ${exception.message}")
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
            buildingId = -10
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

    fun findWindowsByRoom(roomId: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.findWindowsByRoom(roomId).execute() }
                .onSuccess { response ->
                    windowsState.value = response.body() ?: emptyList()
                }
                .onFailure {
                    it.printStackTrace()
                    windowsState.value = emptyList()
                }
        }
    }

    fun findWindowById(windowId: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.findWindowById(windowId).execute() }
                .onSuccess { response ->
                    selectedWindowState.value = response.body()
                }
                .onFailure {
                    it.printStackTrace()
                    selectedWindowState.value = null
                }
        }
    }

    fun deleteWindow(windowId: Long, onComplete: () -> Unit) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.deleteWindow(windowId).execute() }
                .onSuccess {
                    onComplete()
                }
                .onFailure {
                    it.printStackTrace()
                    onComplete()
                }
        }
    }

}
