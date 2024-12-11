package com.automacorp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.automacorp.model.WindowDto
import com.automacorp.ui.theme.AutomacorpTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.automacorp.ui.theme.PurpleGrey80

class WindowListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val roomId = intent.getLongExtra("ROOM_ID", -1L)
        val viewModel: RoomViewModel by viewModels()


        val navigateBack: () -> Unit = {
            finish()
        }

        setContent {
            val windowsState by viewModel.windowsState.collectAsState()

            LaunchedEffect(Unit) {
                if (roomId != -1L) {
                    viewModel.findWindowsByRoom(roomId)
                }
            }

            AutomacorpTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AutomacorpTopAppBar("Windows", navigateBack) }
                ) { innerPadding ->
                    if (windowsState.isEmpty()) {
                        Text(
                            text = "No windows found",
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        WindowList(
                            windows = windowsState,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WindowList(windows: List<WindowDto>, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(windows, key = { it.id }) { window ->
            WindowItem(window = window, modifier = Modifier.clickable {
                val intent = Intent(context, WindowDetailActivity::class.java)
                intent.putExtra("WINDOW_ID", window.id)
                context.startActivity(intent)
            })
        }
    }
}


@Composable
fun WindowItem(window: WindowDto, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, PurpleGrey80),
        modifier = modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = window.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Status: ${window.windowStatus}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "ID: ${window.id}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

