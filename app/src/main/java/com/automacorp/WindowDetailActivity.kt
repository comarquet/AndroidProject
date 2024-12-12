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
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.model.WindowDto
import kotlin.math.round

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
    Column(modifier = modifier.padding(16.dp)) {
        // Window Name
        Text(
            text = stringResource(R.string.act_window_name),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = model.window?.name ?: "",
            onValueChange = { newName ->
                model.window = model.window?.copy(name = newName)
            },
            label = { Text(text = stringResource(R.string.act_window_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val context = LocalContext.current

        Button(
            onClick = {
                model.window?.id?.let { id ->
                    model.openWindow(id) {
                        Toast.makeText(context, "Window ${model.window?.name} was opened", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.act_window_open))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                model.window?.id?.let { id ->
                    model.closeWindow(id) {
                        Toast.makeText(context, "Window ${model.window?.name} was closed", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.act_window_close))
        }
    }
}
