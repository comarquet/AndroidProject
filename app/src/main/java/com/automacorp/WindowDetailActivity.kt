package com.automacorp

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.automacorp.ui.theme.AutomacorpTheme
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.automacorp.model.WindowDto
import com.automacorp.views.WindowsViewModel

class WindowDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: WindowsViewModel by viewModels()
        val param = intent.getLongExtra(WindowListActivity.WINDOW_ID, -1L)
        if (param != -1L) {
            viewModel.findWindowFromList(param)
            Log.d("RoomDetailActivity", "Room loaded: ${viewModel.window}")
        }

        val onWindowSave: () -> Unit = {
            if (viewModel.window != null) {
                val windowDto: WindowDto = viewModel.window as WindowDto
                viewModel.updateWindow(windowDto.id, windowDto)
                Toast.makeText(baseContext, "Window ${windowDto.name} was updated", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        val onWindowDelete: () -> Unit = {
            if (viewModel.window != null) {
                val windowDto: WindowDto = viewModel.window as WindowDto
                viewModel.deleteWindow(windowDto.id) {
                    runOnUiThread {
                        Toast.makeText(baseContext, "Room ${windowDto.name} was deleted", Toast.LENGTH_LONG).show()
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
                            WindowUpdateButton(onWindowSave)
                            Spacer(modifier = Modifier.height(16.dp))
                            WindowDeleteButton(onWindowDelete)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (viewModel.window != null) {
                        WindowListDetail(viewModel, Modifier.padding(innerPadding))
                    }
                }
            }
        }

    }
}

@Composable
fun WindowDeleteButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        text = { Text(text = stringResource(R.string.act_window_delete)) },
        icon = {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.act_window_delete)
            )
        }
    )
}

@Composable
fun WindowUpdateButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = {
            Icon(
                Icons.Filled.Done,
                contentDescription = stringResource(R.string.act_window_save),
            )
        },
        text = { Text(text = stringResource(R.string.act_window_save)) }
    )
}

@Composable
fun WindowListDetail(model: WindowsViewModel, modifier: Modifier = Modifier) {
    val window = model.window
    if (window == null) {
        Text("Window details are unavailable", modifier = Modifier.padding(16.dp))
        return
    }

    // Initialize windowState from the current window status
    var windowState by remember { mutableStateOf(window.windowStatus == 1.0) }

    Column(modifier = modifier.padding(16.dp)) {
        // Window Name
        Text(
            text = "Window Name",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = window.name,
            onValueChange = { newName ->
                // Update the WindowDto in the ViewModel
                model.window = window.copy(name = newName)
            },
            label = { Text(text = "Window Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show current window state
        Text(
            text = if (windowState) "Status: Opened" else "Status: Closed",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Open Button
        Button(
            onClick = {
                windowState = true
                // Update the window's status in the ViewModel
                model.window = window.copy(windowStatus = 1.0)
            },
            enabled = !windowState,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Open")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Close Button
        Button(
            onClick = {
                windowState = false
                // Update the window's status in the ViewModel
                model.window = window.copy(windowStatus = 0.0)
            },
            enabled = windowState,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Close")
        }
    }
}
