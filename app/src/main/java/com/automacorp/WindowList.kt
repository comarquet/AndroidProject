package com.automacorp

import com.automacorp.model.WindowDto

class WindowList(
    val windows: List<WindowDto> = emptyList(),
    val error: String? = null
)