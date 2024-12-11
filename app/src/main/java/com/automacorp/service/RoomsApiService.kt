package com.automacorp.service

import com.automacorp.model.RoomCommandDto
import com.automacorp.model.RoomDto
import com.automacorp.model.WindowDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoomsApiService {

    @GET("rooms")
    fun findAll(): Call<List<RoomDto>>

    @GET("rooms/{id}")
    fun findById(@Path("id") id: Long): Call<RoomDto>

    @PUT("rooms/{id}")
    fun updateRoom(
        @Path("id") id: Long,
        @Body room: RoomCommandDto
    ): Call<RoomDto>

    @POST("rooms")
    fun createRoom(@Body room: RoomCommandDto): Call<RoomDto>

    @DELETE("rooms/{id}")
    fun deleteRoom(@Path("id") id: Long): Call<Void>

    @GET("windows/{id}")
    fun findWindowById(@Path("id") id: Long): Call<WindowDto>

    @DELETE("windows/{id}")
    fun deleteWindow(@Path("id") id: Long): Call<Void>

    @GET("windows/{id}")
    fun findWindowsByRoom(@Path("id") roomId: Long): Call<List<WindowDto>>
}
