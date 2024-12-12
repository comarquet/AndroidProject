package com.automacorp.model

data class SensorCommandDto(
    val name: String,
    val value: Double,
    val sensorType: SensorType
)