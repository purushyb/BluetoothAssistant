package com.purush.app.blutoothassistant

import android.app.Activity
import android.bluetooth.BluetoothAssignedNumbers
import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.purush.app.blutoothassistant.ui.theme.BlutoothAssistantTheme

@Composable
fun KeyboardScreen(
    onSendSpecialKey: (Int, Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    orientation: Int = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
) {
    var shiftOn by remember { mutableStateOf(false) }
    var ctrlOn by remember { mutableStateOf(false) }
    var altOn by remember { mutableStateOf(false) }
    var metaOn by remember { mutableStateOf(false) }

    val activeModifier = (if (ctrlOn) 0x01 else 0) or
            (if (shiftOn) 0x02 else 0) or
            (if (altOn) 0x04 else 0) or
            (if (metaOn) 0x08 else 0)

    val handleKeyPress: (Int) -> Unit = { keyCode ->
        onSendSpecialKey(keyCode, activeModifier)
    }

    val context = LocalContext.current

//    DisposableEffect(orientation) {
//        // Find the activity from context
//        val activity = context as? Activity // Simplified context lookup
//        val originalOrientation = activity?.requestedOrientation
//
//        // Set new orientation
//        activity?.requestedOrientation = orientation
//
//        // Restore original on dispose
//        onDispose {
//            if (originalOrientation != null) {
//                activity.requestedOrientation = originalOrientation
//            }
//        }
//    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(4.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Push keyboard to bottom

        // Keyboard layout
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Row 1 (Functions)
            KeyboardRow {
                KeyButton("Esc", 41, handleKeyPress)
                KeyButton("F1", 58, handleKeyPress)
                KeyButton("F2", 59, handleKeyPress)
                KeyButton("F3", 60, handleKeyPress)
                KeyButton("F4", 61, handleKeyPress)
                KeyButton("F5", 62, handleKeyPress)
                KeyButton("F6", 63, handleKeyPress)
                KeyButton("F7", 64, handleKeyPress)
                KeyButton("F8", 65, handleKeyPress)
                KeyButton("F9", 66, handleKeyPress)
                KeyButton("F10", 67, handleKeyPress)
                KeyButton("F11", 68, handleKeyPress)
                KeyButton("F12", 69, handleKeyPress)
                KeyButton("Del", 76, handleKeyPress, weight = 1.2f)
            }
            // Row 2 (Numbers)
            KeyboardRow {
                KeyButton(if (shiftOn) "~" else "`", 53, handleKeyPress)
                KeyButton(if (shiftOn) "!" else "1", 30, handleKeyPress)
                KeyButton(if (shiftOn) "@" else "2", 31, handleKeyPress)
                KeyButton(if (shiftOn) "#" else "3", 32, handleKeyPress)
                KeyButton(if (shiftOn) "$" else "4", 33, handleKeyPress)
                KeyButton(if (shiftOn) "%" else "5", 34, handleKeyPress)
                KeyButton(if (shiftOn) "^" else "6", 35, handleKeyPress)
                KeyButton(if (shiftOn) "&" else "7", 36, handleKeyPress)
                KeyButton(if (shiftOn) "*" else "8", 37, handleKeyPress)
                KeyButton(if (shiftOn) "(" else "9", 38, handleKeyPress)
                KeyButton(if (shiftOn) ")" else "0", 39, handleKeyPress)
                KeyButton(if (shiftOn) "_" else "-", 45, handleKeyPress)
                KeyButton(if (shiftOn) "+" else "=", 46, handleKeyPress)
                KeyButton("Bksp", 42, handleKeyPress, weight = 2f)
            }
            // Row 3 (QWERTY)
            KeyboardRow {
                KeyButton("Tab", 43, handleKeyPress, weight = 1.5f)
                KeyButton("Q", 20, handleKeyPress, shiftOn)
                KeyButton("W", 26, handleKeyPress, shiftOn)
                KeyButton("E", 8, handleKeyPress, shiftOn)
                KeyButton("R", 21, handleKeyPress, shiftOn)
                KeyButton("T", 23, handleKeyPress, shiftOn)
                KeyButton("Y", 28, handleKeyPress, shiftOn)
                KeyButton("U", 24, handleKeyPress, shiftOn)
                KeyButton("I", 12, handleKeyPress, shiftOn)
                KeyButton("O", 18, handleKeyPress, shiftOn)
                KeyButton("P", 19, handleKeyPress, shiftOn)
                KeyButton(if (shiftOn) "{" else "[", 47, handleKeyPress)
                KeyButton(if (shiftOn) "}" else "]", 48, handleKeyPress)
                KeyButton(if (shiftOn) "|" else "\\", 49, handleKeyPress, weight = 1.5f)
            }
            // Row 4 (ASDF)
            KeyboardRow {
                KeyButton("Caps", 57, handleKeyPress, weight = 1.8f)
                KeyButton("A", 4, handleKeyPress, shiftOn)
                KeyButton("S", 22, handleKeyPress, shiftOn)
                KeyButton("D", 7, handleKeyPress, shiftOn)
                KeyButton("F", 9, handleKeyPress, shiftOn)
                KeyButton("G", 10, handleKeyPress, shiftOn)
                KeyButton("H", 11, handleKeyPress, shiftOn)
                KeyButton("J", 13, handleKeyPress, shiftOn)
                KeyButton("K", 14, handleKeyPress, shiftOn)
                KeyButton("L", 15, handleKeyPress, shiftOn)
                KeyButton(if (shiftOn) ":" else ";", 51, handleKeyPress)
                KeyButton(if (shiftOn) "\"" else "'", 52, handleKeyPress)
                KeyButton("Enter", 40, handleKeyPress, weight = 2.2f)
            }
            // Row 5 (ZXCV)
            KeyboardRow {
                ModifierKeyButton("Shift", shiftOn, { shiftOn = !shiftOn }, weight = 2.2f)
                KeyButton("Z", 29, handleKeyPress, shiftOn)
                KeyButton("X", 27, handleKeyPress, shiftOn)
                KeyButton("C", 6, handleKeyPress, shiftOn)
                KeyButton("V", 25, handleKeyPress, shiftOn)
                KeyButton("B", 5, handleKeyPress, shiftOn)
                KeyButton("N", 17, handleKeyPress, shiftOn)
                KeyButton("M", 16, handleKeyPress, shiftOn)
                KeyButton(if (shiftOn) "<" else ",", 54, handleKeyPress)
                KeyButton(if (shiftOn) ">" else ".", 55, handleKeyPress)
                KeyButton(if (shiftOn) "?" else "/", 56, handleKeyPress)
                ModifierKeyButton("Shift", shiftOn, { shiftOn = !shiftOn }, weight = 2.8f)
            }
            // Row 6 (Bottom)
            KeyboardRow {
                ModifierKeyButton("Ctrl", ctrlOn, { ctrlOn = !ctrlOn }, weight = 1.5f)
                ModifierKeyButton("Win", metaOn, { metaOn = !metaOn }, weight = 1.2f)
                ModifierKeyButton("Alt", altOn, { altOn = !altOn }, weight = 1.2f)
                KeyButton("Space", 44, handleKeyPress, weight = 5f)
                ModifierKeyButton("Alt", altOn, { altOn = !altOn }, weight = 1.2f)
                KeyButton("←", 80, handleKeyPress)
                KeyButton("↓", 81, handleKeyPress)
                KeyButton("↑", 82, handleKeyPress)
                KeyButton("→", 79, handleKeyPress)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun KeyboardRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = content
    )
}

@Composable
fun RowScope.KeyButton(
    label: String,
    keyCode: Int,
    onPress: (Int) -> Unit,
    shiftOn: Boolean = false,
    weight: Float = 1f
) {
    val displayLabel =
        if (label.length == 1 && shiftOn) label.uppercase() else if (label.length == 1) label.lowercase() else label
    Box(
        modifier = Modifier
            .weight(weight)
            .height(54.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF333333))
            .clickable { onPress(keyCode) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayLabel,
            color = Color.White,
            fontSize = if (label.length > 1) 12.sp else 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RowScope.ModifierKeyButton(
    label: String,
    isActive: Boolean,
    onToggle: () -> Unit,
    weight: Float = 1f
) {
    Box(
        modifier = Modifier
            .weight(weight)
            .height(54.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) Color(0xFF007ACC) else Color(0xFF444444))
            .clickable { onToggle() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp,orientation=landscape",
    name = "Landscape Spec"
)
@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp,orientation=portrait",
    name = "Portrait Spec"
)
@Composable
fun KeyboardScreenPreview() {
    BlutoothAssistantTheme { KeyboardScreen(onSendSpecialKey = { _, _ -> Unit }, onBack = {}) }
}
