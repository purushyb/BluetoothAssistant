package com.purush.app.blutoothassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.purush.app.blutoothassistant.ui.theme.BlutoothAssistantTheme

@Composable
fun RemoteScreen(
    onRemoteKey: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Navigation Pad
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            RemoteButton(
                modifier = Modifier.padding(16.dp),
                Icons.Default.KeyboardArrowUp,
                "Up"
            ) { onRemoteKey(82) } // Up
            Row {
                RemoteButton(
                    icon = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Left"
                ) { onRemoteKey(80) } // Left
                Spacer(modifier = Modifier.width(16.dp))
                // OK Button
                FilledIconButton(
                    onClick = { onRemoteKey(40) }, // Enter
                    modifier = Modifier.size(80.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("OK", color = Color.White, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                RemoteButton(
                    icon = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Right"
                ) { onRemoteKey(79) } // Right
            }
            RemoteButton(
                modifier = Modifier.padding(16.dp),
                icon = Icons.Default.KeyboardArrowDown,
                contentDescription = "Down"
            ) { onRemoteKey(81) } // Down
        }

        // Functional Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RemoteFunctionalButton(
                Icons.Default.ArrowBack,
                "Back"
            ) { onRemoteKey(41) } // Esc as Back
            RemoteFunctionalButton(Icons.Default.Home, "Home") { onRemoteKey(74) } // Home
            RemoteFunctionalButton(
                Icons.Default.Menu,
                "Menu"
            ) { onRemoteKey(101) } // App Menu / Menu Key (Application)
        }
    }
}

@Composable
fun RemoteButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(72.dp),
        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF333333))
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun RemoteFunctionalButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(64.dp),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF252525))
        ) {
            Icon(icon, contentDescription = contentDescription, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(contentDescription, color = Color.White, fontSize = 12.sp)
    }
}


@Preview
@Composable
fun RemoteScreenPreview() {
    BlutoothAssistantTheme() { RemoteScreen { } }
}