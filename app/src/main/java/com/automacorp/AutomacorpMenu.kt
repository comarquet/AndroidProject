package com.automacorp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.startActivity
import com.automacorp.ui.theme.AutomacorpTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AutomacorpTopAppBar(title: String? = null, returnAction: () -> Unit = {}) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    )
    // Define the actions displayed on the right side of the app bar
    val actions: @Composable RowScope.() -> Unit = {
        val context = LocalContext.current
        IconButton(onClick = {
            val intent = Intent(context, RoomListActivity::class.java)
            context.startActivity(intent)
        }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_action_rooms),
                contentDescription = stringResource(R.string.app_go_room_description)
            )
        }
        IconButton(onClick = {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:cormarquet@gmail.com"))
            startActivity(context, intent, null)
        }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_action_mail),
                contentDescription = stringResource(R.string.app_go_mail_description)
            )
        }
        IconButton(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/comarquet"))
            startActivity(context, intent, null)
        }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_action_github),
                contentDescription = stringResource(R.string.app_go_github_description)
            )
        }
    }
    // Display the app bar with the title if present and actions
    if(title == null) {
        TopAppBar(
            title = { Text("") },
            colors = colors,
            actions = actions
        )
    } else {
        MediumTopAppBar(
            title = { Text(title) },
            colors = colors,
            // The title will be displayed in other screen than the main screen.
            // In this case we need to add a return action
            navigationIcon = {
                IconButton(onClick = returnAction) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.app_go_back_description)
                    )
                }
            },
            actions = actions
        )
    }
}