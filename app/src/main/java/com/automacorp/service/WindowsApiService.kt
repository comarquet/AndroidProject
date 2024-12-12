package com.automacorp.service

import com.automacorp.model.RoomCommandDto
import com.automacorp.model.RoomDto
import com.automacorp.model.WindowCommandDto
import com.automacorp.model.WindowDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface WindowsApiService {
    @GET("rooms/{roomId}/windows")
    fun findWindowsByRoomId(@Path("roomId") roomId: Long): Call<List<WindowDto>>

    @GET("windows/{id}")
    fun findById(@Path("id") id: Long): Call<WindowDto>

    @PUT("windows/{id}")
    fun updateWindow(@Path("id") id: Long, @Body room: WindowCommandDto): Call<WindowDto>

    @PUT("windows/{id}/openWindow")
    fun openWindow(@Path("id") id: Long): Call<WindowDto>

    @PUT("windows/{id}/closeWindow")
    fun closeWindow(@Path("id") id: Long): Call<WindowDto>

    @DELETE("windows/{id}")
    fun deleteWindow(@Path("id") id: Long): Call<Void>
}