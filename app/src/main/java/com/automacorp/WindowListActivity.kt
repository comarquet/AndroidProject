package com.automacorp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import kotlinx.coroutines.flow.asStateFlow

class WindowListActivity : ComponentActivity() {
    companion object {
        const val ROOM_ID = "com.automacorp.room.windows"
        const val WINDOW_ID = "com.automacorp.window"
    }

    private val viewModel: WindowsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val roomId = intent.getLongExtra(ROOM_ID, -1L)
        if (roomId == -1L) {
            Toast.makeText(this, "Invalid Room ID", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val navigateBack: () -> Unit = {
            finish()
        }

        val openWindow: (id: Long) -> Unit = { id ->
            val intent = Intent(this, WindowDetailActivity::class.java).apply {
                putExtra(WINDOW_ID, id)
            }
            startActivity(intent)
        }

        setContent {
            val windowsState by viewModel.windowsState.asStateFlow().collectAsState()
            if (windowsState.error != null) {
                WindowsList(emptyList(), navigateBack, openWindow)
                Toast
                    .makeText(applicationContext, "Error on windows loading ${windowsState.error}", Toast.LENGTH_LONG)
                    .show()
            } else {
                WindowsList(windowsState.windows, navigateBack, openWindow)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val roomId = intent.getLongExtra(ROOM_ID, -1L)
        if (roomId != -1L) {
            viewModel.findWindowsByRoom(roomId) // Refresh windows when returning to this activity
        }
    }
}


@Composable
fun WindowsList(
    windows: List<WindowDto>,
    navigateBack: () -> Unit,
    openWindow: (id: Long) -> Unit
) {
    AutomacorpTheme {
        Scaffold(
            topBar = { AutomacorpTopAppBar("Windows", navigateBack) }
        ) { innerPadding ->
            if (windows.isEmpty()) {
                Text(
                    text = "No windows found",
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(innerPadding),
                ) {
                    itemsIndexed(windows) { index, window ->
                        WindowItem(
                            window = window,
                            modifier = Modifier.clickable {
                                openWindow(window.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WindowItem(window: WindowDto, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, PurpleGrey80),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = window.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Status: ${if (window.windowStatus == 1.0) "Opened" else "Closed"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


