package com.automacorp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.automacorp.model.WindowDto
import com.automacorp.ui.theme.AutomacorpTheme

class WindowDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: RoomViewModel by viewModels()
        val windowId = intent.getLongExtra("WINDOW_ID", -1L)

        if (windowId != -1L) {
            viewModel.findWindowById(windowId)
        }

        val onWindowDelete: () -> Unit = {
            viewModel.deleteWindow(windowId) {
                runOnUiThread {
                    Toast.makeText(
                        baseContext,
                        "Window was deleted",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }

        val navigateBack: () -> Unit = {
            finish()
        }

        setContent {
            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Window Detail", navigateBack) },
                    floatingActionButton = {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            WindowDeleteButton(onWindowDelete)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val windowState by viewModel.selectedWindowState.collectAsState()

                    if (windowState != null) {
                        WindowDetailContent(windowState!!, Modifier.padding(innerPadding))
                    } else {
                        Text("Window not found")
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
fun WindowDetailContent(window: WindowDto, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Window Name: ${window.name}",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Status: ${window.windowStatus}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "ID: ${window.id}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
