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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.purush.app.blutoothassistant.ui.theme.BlutoothAssistantTheme

enum class AppScreen { MAIN, FULL_KEYBOARD }

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlutoothAssistantTheme {
                val navController = rememberNavController()

                Scaffold(topBar = {
                    AppNavBar(
                        navController,
                        modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues())
                    )
                }) {
                    AppNavHost(navController = navController, modifier = Modifier.padding(it))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavBar(navController: NavHostController, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    )
    {
        IconButton(onClick = {
            navController.navigateSingleTop(Home.route)
        }) {
            Icon(
                Icons.Filled.Home,
                contentDescription = "Home"
            )
        }
        IconButton(onClick = { navController.navigateSingleTop(Keyboard.route) }) {
            Icon(
                Icons.Filled.Keyboard,
                contentDescription = "Keyboard"
            )
        }
        IconButton(onClick = { navController.navigateSingleTop(Mouse.route) }) {
            Icon(
                Icons.Filled.Mouse,
                contentDescription = "Mouse"
            )
        }
    }
}

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
                    android.content.Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                        .apply {
                            putExtra(
                                android.bluetooth.BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
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
        androidx.compose.material3.OutlinedTextField(
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
            modifier = Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState())
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
            modifier = Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState())
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
            modifier = Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState())
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


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = viewModel()
    NavHost(navController, startDestination = appDestinations.first().route, modifier = modifier) {
        composable(route = Home.route) {
            HomeScreen(
                viewModel,
                onNavigateToKeyboard = { navController.navigateSingleTop(Keyboard.route) })
        }

        composable(route = Keyboard.route) {
            KeyboardScreen(
                onSendSpecialKey = { keyCode, modifier ->
                    viewModel.sendSpecialKey(keyCode, modifier)
                },
                onBack = { navController.navigateSingleTop(Home.route) }
            )
        }

        composable(route = Mouse.route) {
            MouseScreen(
                onScroll = { viewModel.scroll(it) },
                moveMouse = { dx, dy ->
                    viewModel.moveMouse(dx, dy)
                },
                onZoom = { viewModel.zoom(it) },
                onLeftClick = { viewModel.mouseClick(true) },
                onRightClick = { viewModel.mouseClick(false) })
        }
    }
}

fun NavHostController.navigateSingleTop(route: String) {
    this.navigate(route) {
        popUpTo(this@navigateSingleTop.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
