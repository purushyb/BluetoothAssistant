package com.purush.app.blutoothassistant.ui.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.purush.app.blutoothassistant.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToKeyboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val isConnected by viewModel.isConnected.collectAsState()
    val isServiceRunning by viewModel.isServiceRunning.collectAsState()
    var hasPermissions by remember { mutableStateOf(false) }

    LaunchedEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onResume()
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
            text = if (isConnected) "Status: Connected" else "Status: Disconnected",
            color = if (isConnected) Color.Green else Color.Red
        )

        Button(onClick = onNavigateToKeyboard, modifier = Modifier.fillMaxWidth()) {
            Text("Open Full Keyboard")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Keyboard & Media", style = MaterialTheme.typography.titleMedium)

        var textInput by remember { mutableStateOf("") }
        OutlinedTextField(
            value = textInput,
            onValueChange = { newValue ->
                if (newValue.length > textInput.length) {
                    val char = newValue.last()
                    viewModel.sendChar(char)
                } else if (newValue.length < textInput.length) {
                    viewModel.sendBackspace()
                }
                textInput = newValue
            },
            label = { Text("Type here (Special symbols & Shift supported)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            Button(onClick = { viewModel.sendEnter() }) { Text("Enter") }
            Button(onClick = { viewModel.sendBackspace() }) { Text("Bksp") }
            Button(onClick = { viewModel.sendSpecialKey(41) }) { Text("Esc") } // Esc = 41
            Button(onClick = { viewModel.sendSpecialKey(43) }) { Text("Tab") } // Tab = 43
            Button(onClick = { viewModel.sendSpecialKey(58) }) { Text("F1") } // F1 = 58
            Button(onClick = { viewModel.sendSpecialKey(59) }) { Text("F2") } // F2 = 59
            Button(onClick = { viewModel.sendSpecialKey(60) }) { Text("F3") } // F3 = 60
            Button(onClick = { viewModel.sendSpecialKey(61) }) { Text("F4") }
            Button(onClick = { viewModel.sendSpecialKey(62) }) { Text("F5") }
            Button(onClick = { viewModel.sendSpecialKey(63) }) { Text("F6") }
            Button(onClick = { viewModel.sendSpecialKey(64) }) { Text("F7") }
            Button(onClick = { viewModel.sendSpecialKey(65) }) { Text("F8") }
            Button(onClick = { viewModel.sendSpecialKey(66) }) { Text("F9") }
            Button(onClick = { viewModel.sendSpecialKey(67) }) { Text("F10") }
            Button(onClick = { viewModel.sendSpecialKey(68) }) { Text("F11") }
            Button(onClick = { viewModel.sendSpecialKey(69) }) { Text("F12") }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            Button(onClick = { viewModel.sendSpecialKey(82) }) { Text("↑") }
            Button(onClick = { viewModel.sendSpecialKey(81) }) { Text("↓") }
            Button(onClick = { viewModel.sendSpecialKey(80) }) { Text("←") }
            Button(onClick = { viewModel.sendSpecialKey(79) }) { Text("→") }
            Button(onClick = { viewModel.sendSpecialKey(74) }) { Text("Home") }
            Button(onClick = { viewModel.sendSpecialKey(77) }) { Text("End") }
            Button(onClick = { viewModel.sendSpecialKey(75) }) { Text("PgUp") }
            Button(onClick = { viewModel.sendSpecialKey(78) }) { Text("PgDn") }
            Button(onClick = { viewModel.sendSpecialKey(76) }) { Text("Del") }
            Button(onClick = { viewModel.sendSpecialKey(73) }) { Text("Ins") }
        }

        Text(text = "Media Controls", style = MaterialTheme.typography.titleSmall)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            Button(onClick = { viewModel.sendMediaControl(0x08) }) { Text("Play/Pause") }
            Button(onClick = { viewModel.sendMediaControl(0x10) }) { Text("Mute") }
            Button(onClick = { viewModel.sendMediaControl(0x40) }) { Text("Vol -") }
            Button(onClick = { viewModel.sendMediaControl(0x20) }) { Text("Vol +") }
            Button(onClick = { viewModel.sendMediaControl(0x02) }) { Text("Prev") }
            Button(onClick = { viewModel.sendMediaControl(0x01) }) { Text("Next") }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Trackpad", style = MaterialTheme.typography.titleMedium)

    }
}
