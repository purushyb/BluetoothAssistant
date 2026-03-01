package com.purush.app.blutoothassistant.ui.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.purush.app.blutoothassistant.MainViewModel
import com.purush.app.blutoothassistant.ui.theme.BlutoothAssistantTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    isConnected: Boolean,
    connectedDeviceName: String?,
    isServiceRunning: Boolean,
    startService: () -> Unit,
    stopService: () -> Unit,
    onDisconnect: () -> Unit,
    onResume: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermissions by remember { mutableStateOf(false) }

    LaunchedEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
    }

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
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = if (isServiceRunning) "Service: ON" else "Service: OFF")
            Switch(
                checked = isServiceRunning,
                onCheckedChange = { checked ->
                    if (hasPermissions) {
                        if (checked) startService()
                        else stopService()
                    } else {
                        launcher.launch(permissions.toTypedArray())
                    }
                }
            )
        }

        AnimatedVisibility (isConnected) {
            ListItem(
                leadingContent = { Icon(Icons.Filled.Bluetooth, null) },
                headlineContent = { Text(connectedDeviceName ?: "Unknown Device") },
                supportingContent = { Text("Connected") },
                trailingContent = {
                    Button(onClick = onDisconnect) {
                        Text("Disconnect")
                    }
                }
            )
        }

        if(!isConnected){
            Button(
                onClick = {
                    val discoverableIntent =
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                            .apply {
                                putExtra(
                                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                                    300
                                )
                            }
                    context.startActivity(discoverableIntent)
                },
                enabled = hasPermissions
            ) {
                Text("Make Discoverable (Pairing)")
            }

            Text(
                text = "Status: Disconnected",
                color = Color.Red
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BlutoothAssistantTheme() {
        HomeScreen(
            isConnected = true,
            connectedDeviceName = "My Mac",
            isServiceRunning = true,
            startService = {},
            stopService = {},
            onDisconnect = {},
            onResume = {})
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenDisconnectedPreview() {
    BlutoothAssistantTheme() {
        HomeScreen(
            isConnected = false,
            connectedDeviceName = "My Mac",
            isServiceRunning = true,
            startService = {},
            stopService = {},
            onDisconnect = {},
            onResume = {})
    }
}
