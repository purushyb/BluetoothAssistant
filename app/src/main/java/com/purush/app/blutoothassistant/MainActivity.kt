package com.purush.app.blutoothassistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.purush.app.blutoothassistant.ui.theme.BlutoothAssistantTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlutoothAssistantTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BluetoothHidScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BluetoothHidScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isConnected by viewModel.isConnected.collectAsState()
    val isServiceRunning by viewModel.isServiceRunning.collectAsState()
    var hasPermissions by remember { mutableStateOf(false) }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasPermissions = result.values.all { it }
        if (!hasPermissions) {
            Toast.makeText(context, "Permissions required", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val granted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (granted) {
            hasPermissions = true
        } else {
            launcher.launch(permissions.toTypedArray())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Bluetooth HID Assistant",
            style = MaterialTheme.typography.headlineMedium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isServiceRunning) "Service: ON" else "Service: OFF")
            Switch(
                checked = isServiceRunning,
                onCheckedChange = { checked ->
                    if (hasPermissions) {
                        if (checked) viewModel.startBluetoothService()
                        else viewModel.stopBluetoothService()
                    } else {
                        launcher.launch(permissions.toTypedArray())
                    }
                }
            )
        }

        Button(
            onClick = {
                val discoverableIntent = android.content.Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                    putExtra(android.bluetooth.BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                }
                context.startActivity(discoverableIntent)
            },
            enabled = hasPermissions
        ) {
            Text("Make Discoverable (Pairing)")
        }

        Text(
            text = if (isConnected) "Status: Connected" else "Status: Disconnected",
            color = if (isConnected) Color.Green else Color.Red
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Keyboard", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.sendChar('a') }) { Text("A") }
            Button(onClick = { viewModel.sendChar('b') }) { Text("B") }
            Button(onClick = { viewModel.sendChar(' ') }) { Text("Space") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.sendEnter() }) { Text("Enter") }
            Button(onClick = { viewModel.sendBackspace() }) { Text("Bksp") }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Trackpad", style = MaterialTheme.typography.titleMedium)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        // Scale sensitivity if needed
                        viewModel.moveMouse(dragAmount.x.toInt(), dragAmount.y.toInt())
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { viewModel.mouseClick(true) },
                        onDoubleTap = { viewModel.mouseClick(false) } // Right click on double tap for demo
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text("Drag to move mouse\nTap to click\nDouble tap to R-click")
        }
    }
}