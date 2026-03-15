package com.purush.app.blutoothassistant

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.purush.app.blutoothassistant.ui.screens.AppNavBar
import com.purush.app.blutoothassistant.ui.screens.HomeScreen
import com.purush.app.blutoothassistant.ui.screens.KeyboardScreen
import com.purush.app.blutoothassistant.ui.screens.MediaScreen
import com.purush.app.blutoothassistant.ui.screens.MouseScreen
import com.purush.app.blutoothassistant.ui.screens.RemoteScreen
import com.purush.app.blutoothassistant.ui.theme.BlutoothAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlutoothAssistantTheme {
                val navController = rememberNavController()
                val backStack by navController.currentBackStackEntryAsState()
                val currentScreen = backStack?.destination

                Scaffold(topBar = {
                    AppNavBar(
                        modifier = Modifier.statusBarsPadding(),
                        allScreens = AppDestination.entries,
                        onTabSelected = { screen ->
                            navController.navigateSingleTop(screen.route)
                        },
                        currentScreen = AppDestination.entries.find { it.route == currentScreen?.route }
                            ?: AppDestination.Home
                    )
                }) {
                    AppNavHost(navController = navController, modifier = Modifier.padding(it))
                }
            }
        }
    }
}


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = viewModel()
    NavHost(navController, startDestination = "Home", modifier = modifier) {
        composable(route = AppDestination.Home.route) {
            val isConnected by viewModel.isConnected.collectAsState()
            val connectedDeviceName by viewModel.connectedDeviceName.collectAsState()
            val isServiceRunning by viewModel.isServiceRunning.collectAsState()
            HomeScreen(
                isConnected = isConnected,
                connectedDeviceName = connectedDeviceName,
                isServiceRunning = isServiceRunning,
                startService = {
                    Log.i("BluetoothMessage", "Connecting service")
                    viewModel.startBluetoothService() },
                stopService = { viewModel.stopBluetoothService() },
                onDisconnect = { viewModel.disconnect() },
                onResume = { viewModel.onResume() })
        }

        composable(route = AppDestination.Keyboard.route) {
            KeyboardScreen(
                onSendSpecialKey = { keyCode, modifier ->
                    viewModel.sendSpecialKey(keyCode, modifier)
                },
                onBack = { navController.navigateSingleTop(AppDestination.Home.route) }
            )
        }

        composable(route = AppDestination.Mouse.route) {
            MouseScreen(
                onScroll = { viewModel.scroll(it) },
                moveMouse = { dx, dy ->
                    viewModel.moveMouse(dx, dy)
                },
                onZoom = { viewModel.zoom(it) },
                onLeftClick = { viewModel.mouseClick(true) },
                onRightClick = { viewModel.mouseClick(false) })
        }

        composable(route = AppDestination.Media.route) {
            MediaScreen(
                onMediaCommand = { command ->
                    viewModel.sendMediaCommand(command)
                }
            )
        }

        composable(route = AppDestination.Remote.route) {
            RemoteScreen(
                onRemoteKey = { keycode ->
                    viewModel.sendRemoteKey(keycode)
                }
            )
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


@Preview(showBackground = true)
@Composable
fun AppNavBarPreview() {
    BlutoothAssistantTheme {
        AppNavBar(
            allScreens = AppDestination.entries,
            onTabSelected = {},
            currentScreen = AppDestination.Home
        )
    }
}