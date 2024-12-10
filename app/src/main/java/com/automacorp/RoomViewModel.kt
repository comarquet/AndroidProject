package com.automacorp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automacorp.model.RoomDto
import com.automacorp.service.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RoomViewModel: ViewModel() {
    var room by mutableStateOf <RoomDto?>(null)
    val roomsState = MutableStateFlow(RoomList())

    fun findAll() {
        viewModelScope.launch(context = Dispatchers.IO) { // (1)
            runCatching { ApiServices.roomsApiService.findAll().execute() }
                .onSuccess {
                    val rooms = it.body() ?: emptyList()
                    roomsState.value = RoomList(rooms) // (2)
                }
                .onFailure {
                    it.printStackTrace()
                    roomsState.value = RoomList(emptyList(), it.stackTraceToString() ) // (3)
                }
        }
    }
}