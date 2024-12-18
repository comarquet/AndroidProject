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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.views.RoomViewModel
import kotlin.math.round

class RoomDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: RoomViewModel by viewModels()
        val param = intent.getLongExtra(RoomListActivity.ROOM_PARAM, -1L)
        if (param != -1L) {
            viewModel.findRoomFromList(param)
        }

        val onRoomSave: () -> Unit = {
            if (viewModel.room != null) {
                val roomDto: RoomDto = viewModel.room as RoomDto
                viewModel.updateRoom(roomDto.Id, roomDto)
                Toast.makeText(baseContext, "Room ${roomDto.name} was updated", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        val onRoomDelete: () -> Unit = {
            if (viewModel.room != null) {
                val roomDto: RoomDto = viewModel.room as RoomDto
                viewModel.deleteRoom(roomDto.Id) {
                    runOnUiThread {
                        Toast.makeText(baseContext, "Room ${roomDto.name} was deleted", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
        }

        val navigateBack: () -> Unit = {
            finish()
        }

        setContent {
            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Room", navigateBack) },
                    floatingActionButton = {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            RoomUpdateButton(onRoomSave)
                            Spacer(modifier = Modifier.height(16.dp))
                            RoomDeleteButton(onRoomDelete)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (viewModel.room != null) {
                        RoomListDetail(viewModel, Modifier.padding(innerPadding))
                    }
                }
            }
        }

    }
}

@Composable
fun RoomDeleteButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        text = { Text(text = stringResource(R.string.act_room_delete)) },
        icon = {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.act_room_delete)
            )
        }
    )
}

@Composable
fun RoomListDetail(model: RoomViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        // Room Name
        Text(
            text = stringResource(R.string.act_room_name),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = model.room?.name ?: "",
            onValueChange = { newName ->
                model.room = model.room?.copy(name = newName)
            },
            label = { Text(text = stringResource(R.string.act_room_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Current Temperature Label and Value
        Text(
            text = stringResource(R.string.act_room_current_temperature),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )
        Text(
            text = "${model.room?.currentTemperature?.let { String.format("%.1f", it) } ?: "N/A"}°C",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Target Temperature Input Field
        Text(
            text = stringResource(R.string.act_room_target_temperature),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Slider(
            value = model.room?.targetTemperature?.toFloat() ?: 18.0f,
            onValueChange = { newValue ->
                model.room = model.room?.copy(targetTemperature = newValue.toDouble())
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 0,
            valueRange = 10f..28f
        )

        // Display the Target Temperature
        Text(
            text = (round((model.room?.targetTemperature ?: 18.0) * 10) / 10).toString(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        val context = LocalContext.current

        // Navigate to Window List Activity
        Button(
            onClick = {
                val roomId = model.room?.Id
                if (roomId != null) {
                    val intent = Intent(context, WindowListActivity::class.java)
                    intent.putExtra(WindowListActivity.ROOM_ID, roomId)
                    context.startActivity(intent)
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.app_go_windows))
        }
    }
}
