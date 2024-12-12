package com.automacorp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automacorp.model.RoomCommandDto
import com.automacorp.model.RoomDto
import com.automacorp.model.SensorCommandDto
import com.automacorp.model.SensorType
import com.automacorp.model.WindowCommandDto
import com.automacorp.model.WindowDto
import com.automacorp.service.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WindowsViewModel : ViewModel() {
    var window by mutableStateOf<WindowDto?>(null)
    val windowsState = MutableStateFlow(WindowList())

    fun findWindowsByRoom(roomId: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.windowsApiService.findWindowsByRoomId(roomId).execute() }
                .onSuccess { response ->
                    val windows = response.body() ?: emptyList()
                    windowsState.value = WindowList(windows)
                }
                .onFailure { exception ->
                    windowsState.value = WindowList(emptyList(), exception.message)
                }
        }
    }

    fun findWindowFromList(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.windowsApiService.findById(id).execute() }
                .onSuccess {
                    if (it.isSuccessful && it.body() != null) {
                        window = it.body()
                    } else {
                        window = null
                    }
                }
                .onFailure {
                    it.printStackTrace()
                    Log.e("RoomViewModel", "API call failed: ${it.message}")
                    window = null
                }
        }
    }

    fun updateWindow(id: Long, windowDto: WindowDto) {
        val command = WindowCommandDto(
            name = windowDto.name,
            roomId = windowDto.roomId,
            windowStatus = windowDto.windowStatus // Directly use the `windowStatus` value
        )

        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching {
                ApiServices.windowsApiService.updateWindow(id, command).execute()
            }
                .onSuccess { response ->
                    response.body()?.let { updatedWindow ->
                        println("Window updated: $updatedWindow")
                    }
                }
                .onFailure { exception ->
                    Log.e("WindowUpdate", "Exception: ${exception.message}")
                }
        }
    }

    fun deleteWindow(id: Long, onComplete: () -> Unit) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.windowsApiService.deleteWindow(id).execute() }
                .onSuccess {
                    onComplete()
                }
                .onFailure {
                    it.printStackTrace()
                    onComplete()
                }
        }
    }

    fun openWindow(id: Long, onComplete: () -> Unit) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching {
                ApiServices.windowsApiService.openWindow(id).execute()
            }.onSuccess {
                onComplete()
            }.onFailure {
                it.printStackTrace()
                onComplete()
            }
        }
    }

    fun closeWindow(id: Long, onComplete: () -> Unit) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching {
                ApiServices.windowsApiService.closeWindow(id).execute()
            }.onSuccess {
                onComplete()
            }.onFailure {
                it.printStackTrace()
                onComplete()
            }
        }
    }
}
