package com.purush.app.blutoothassistant

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.ui.graphics.vector.ImageVector

interface AppDestination {
    val icon: ImageVector
    val route: String
}

object Home: AppDestination {
    override val icon = Icons.Filled.Home
    override val route = "Home"
}

object Keyboard: AppDestination {
    override val icon = Icons.Filled.Keyboard
    override val route = "Keyboard"
}

object Mouse: AppDestination {
    override val icon = Icons.Filled.Mouse
    override val route = "Mouse"
}

val appDestinations = listOf(Home, Keyboard, Mouse)