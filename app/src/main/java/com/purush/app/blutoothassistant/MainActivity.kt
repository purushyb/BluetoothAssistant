package com.purush.app.blutoothassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.purush.app.blutoothassistant.ui.screens.MouseScreen
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
                        modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues()),
                        allScreens = appDestinations,
                        onTabSelected = { screen ->
                            navController.navigateSingleTop(screen.route)
                        },
                        currentScreen = appDestinations.find { it.route == currentScreen?.route }
                            ?: Home
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
    NavHost(navController, startDestination = Home.route, modifier = modifier) {
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


@Preview(showBackground = true)
@Composable
fun AppNavBarPreview() {
    BlutoothAssistantTheme {
        AppNavBar(allScreens = appDestinations, onTabSelected = {}, currentScreen = Home)
    }
}