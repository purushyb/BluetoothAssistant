package com.purush.app.blutoothassistant

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    val icon: ImageVector,
    val route: String
) {
    Home(Icons.Filled.Home, "Home"),
    Keyboard(Icons.Filled.Keyboard, "Keyboard"),
    Mouse(Icons.Filled.Mouse, "Mouse")
}