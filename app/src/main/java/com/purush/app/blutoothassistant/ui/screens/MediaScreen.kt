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
import com.purush.app.blutoothassistant.MainViewModel
import com.purush.app.blutoothassistant.ui.theme.BlutoothAssistantTheme

@Composable
fun MediaScreen(
    onMediaCommand: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Media Controls",
            color = Color.White,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Volume Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MediaButton(
                Icons.Default.VolumeDown,
                "Vol Down"
            ) { onMediaCommand(MainViewModel.MediaCommands.VOL_DOWN) }
            MediaButton(
                Icons.Default.VolumeOff,
                "Mute"
            ) { onMediaCommand(MainViewModel.MediaCommands.MUTE) }
            MediaButton(
                Icons.Default.VolumeUp,
                "Vol Up"
            ) { onMediaCommand(MainViewModel.MediaCommands.VOL_UP) }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Playback Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MediaButton(
                Icons.Default.SkipPrevious,
                "Prev"
            ) { onMediaCommand(MainViewModel.MediaCommands.PREVIOUS) }

            LargeMediaButton(
                Icons.Default.PlayArrow,
                "Play/Pause"
            ) { onMediaCommand(MainViewModel.MediaCommands.PLAY_PAUSE) }

            MediaButton(
                Icons.Default.SkipNext,
                "Next"
            ) { onMediaCommand(MainViewModel.MediaCommands.NEXT) }
        }

        Spacer(modifier = Modifier.height(32.dp))

        MediaButton(Icons.Default.Stop, "Stop") { onMediaCommand(MainViewModel.MediaCommands.STOP) }
    }
}

@Composable
fun MediaButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF333333))
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun LargeMediaButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MediaScreenPreview() {
    BlutoothAssistantTheme {
        MediaScreen { }
    }
}
