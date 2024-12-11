package com.automacorp

import android.content.Intent
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.automacorp.ui.theme.AutomacorpTheme
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import kotlin.math.round

class RoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: RoomViewModel by viewModels()
        val param = intent.getStringExtra(MainActivity.ROOM_PARAM)

        // Initialize an empty room for the user to fill in
        viewModel.room = RoomDto(
            id = 0L, // Placeholder until created
            name = param.toString(),
            currentTemperature = (15..30).random().toDouble(),
            targetTemperature = (15..22).random().toDouble(),
            windows = emptyList()
        )

        val onRoomSave: () -> Unit = {
            if (viewModel.room != null) {
                val roomDto: RoomDto = viewModel.room as RoomDto
                println("RoomDto before save: $roomDto")

                // Always create the room since the user is setting parameters
                viewModel.createRoom(roomDto) { createdRoom ->
                    runOnUiThread {
                        Toast.makeText(
                            baseContext,
                            "Room ${createdRoom.name} was created with ID: ${createdRoom.id}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    finish()
                }
            }
        }

        val navigateBack: () -> Unit = {
            startActivity(Intent(baseContext, MainActivity::class.java))
        }

        setContent {
            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Room", navigateBack) },
                    floatingActionButton = { RoomUpdateButton(onRoomSave) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    RoomDetail(viewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun RoomDetail(model: RoomViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        // Room Name
        Text(
            text = stringResource(R.string.act_room_name),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = model.room?.name ?: "",
            onValueChange = { model.room?.name = it },
            label = { Text(text = stringResource(R.string.act_room_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Current Temperature Label and Value
        Text(
            text = stringResource(R.string.act_room_current_temperature),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = model.room?.currentTemperature?.toString() ?: "",
            onValueChange = { value ->
                model.room = model.room?.copy(currentTemperature = value.toDoubleOrNull())
            },
            label = { Text(text = stringResource(R.string.act_room_current_temperature)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Target Temperature Input Field
        Text(
            text = stringResource(R.string.act_room_target_temperature),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Slider(
            value = model.room?.targetTemperature?.toFloat() ?: 18.0f,
            onValueChange = {
                val roundedTemp = Math.round(it * 10) / 10.0
                model.room = model.room?.copy(targetTemperature = roundedTemp)
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 0,
            valueRange = 10f..28f
        )
        Text(text = (round((model.room?.targetTemperature ?: 18.0) * 10) / 10).toString())
    }
}

@Composable
fun RoomUpdateButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = {
            Icon(
                Icons.Filled.Done,
                contentDescription = stringResource(R.string.act_room_save),
            )
        },
        text = { Text(text = stringResource(R.string.act_room_save)) }
    )
}
