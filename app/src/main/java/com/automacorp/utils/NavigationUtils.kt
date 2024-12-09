package com.automacorp.utils

import android.content.Context
import android.content.Intent
import com.automacorp.MainActivity
import com.automacorp.RoomActivity

object NavigationUtils {
    fun openRoom(context: Context, roomId: Long, source: String) {
        val intent = Intent(context, RoomActivity::class.java).apply {
            putExtra(MainActivity.ROOM_PARAM, roomId.toString())
            putExtra("SOURCE_ACTIVITY", source) // Ajoute l'activité source
        }
        context.startActivity(intent)
    }
}
