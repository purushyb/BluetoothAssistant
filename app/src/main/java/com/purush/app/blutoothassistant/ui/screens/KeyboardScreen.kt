package com.purush.app.blutoothassistant.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
    orientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
) {
    var shiftOn by remember { mutableStateOf(false) }
    var ctrlOn by remember { mutableStateOf(false) }
    var altOn by remember { mutableStateOf(false) }
    var metaOn by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val activity = context as? Activity
    
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isCurrentlyLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Reset orientation when leaving this screen
    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    val activeModifier = (if (ctrlOn) 0x01 else 0) or
            (if (shiftOn) 0x02 else 0) or
            (if (altOn) 0x04 else 0) or
            (if (metaOn) 0x08 else 0)

    val handleKeyPress: (Int) -> Unit = { keyCode ->
        onSendSpecialKey(keyCode, activeModifier)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(4.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Push keyboard to bottom

        if (isCurrentlyLandscape) {
            FullLayoutContent(
                handleKeyPress = handleKeyPress,
                shiftOn = shiftOn,
                ctrlOn = ctrlOn,
                altOn = altOn,
                metaOn = metaOn,
                onShiftToggle = { shiftOn = !shiftOn },
                onCtrlToggle = { ctrlOn = !ctrlOn },
                onAltToggle = { altOn = !altOn },
                onMetaToggle = { metaOn = !metaOn }
            )
        } else {
            HorizontalLayout(
                handleKeyPress = handleKeyPress,
                shiftOn = shiftOn,
                ctrlOn = ctrlOn,
                altOn = altOn,
                metaOn = metaOn,
                onShiftToggle = { shiftOn = !shiftOn },
                onCtrlToggle = { ctrlOn = !ctrlOn },
                onAltToggle = { altOn = !altOn },
                onMetaToggle = { metaOn = !metaOn }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun HorizontalLayout(
    handleKeyPress: (Int) -> Unit,
    shiftOn: Boolean,
    ctrlOn: Boolean,
    altOn: Boolean,
    metaOn: Boolean,
    onShiftToggle: () -> Unit,
    onCtrlToggle: () -> Unit,
    onAltToggle: () -> Unit,
    onMetaToggle: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1: Numbers 1-0
        KeyboardRow {
            KeyButton("1", 30, handleKeyPress, isHorizontalLayout = true)
            KeyButton("2", 31, handleKeyPress, isHorizontalLayout = true)
            KeyButton("3", 32, handleKeyPress, isHorizontalLayout = true)
            KeyButton("4", 33, handleKeyPress, isHorizontalLayout = true)
            KeyButton("5", 34, handleKeyPress, isHorizontalLayout = true)
            KeyButton("6", 35, handleKeyPress, isHorizontalLayout = true)
            KeyButton("7", 36, handleKeyPress, isHorizontalLayout = true)
            KeyButton("8", 37, handleKeyPress, isHorizontalLayout = true)
            KeyButton("9", 38, handleKeyPress, isHorizontalLayout = true)
            KeyButton("0", 39, handleKeyPress, isHorizontalLayout = true)
        }
        // Row 2: Q W E R T Y U I O P
        KeyboardRow {
            KeyButton("Q", 20, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("W", 26, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("E", 8, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("R", 21, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("T", 23, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("Y", 28, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("U", 24, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("I", 12, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("O", 18, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("P", 19, handleKeyPress, shiftOn, isHorizontalLayout = true)
        }
        // Row 3: A S D F G H J K L
        KeyboardRow {
            Spacer(modifier = Modifier.weight(0.2f))
            KeyButton("A", 4, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("S", 22, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("D", 7, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("F", 9, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("G", 10, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("H", 11, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("J", 13, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("K", 14, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("L", 15, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("←", 42, handleKeyPress, weight = 1.2f, isHorizontalLayout = true) // Backspace
        }
        // Row 4: Shift Z X C V B N M Enter
        KeyboardRow {
            ModifierKeyButton("shft", shiftOn, onShiftToggle, weight = 1.2f, isHorizontalLayout = true)
            KeyButton("Z", 29, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("X", 27, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("C", 6, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("V", 25, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("B", 5, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("N", 17, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("M", 16, handleKeyPress, shiftOn, isHorizontalLayout = true)
            KeyButton("sym", 56, handleKeyPress, weight = 1f, isHorizontalLayout = true)
            KeyButton("⏎", 40, handleKeyPress, weight = 1.2f, isHorizontalLayout = true)
        }
        // Row 5: Ctrl Alt Space Arrows
        KeyboardRow {
            ModifierKeyButton("ctrl", ctrlOn, onCtrlToggle, weight = 1f, isHorizontalLayout = true)
            ModifierKeyButton("alt", altOn, onAltToggle, weight = 1f, isHorizontalLayout = true)
            KeyButton("SPACE", 44, handleKeyPress, weight = 3.5f, isHorizontalLayout = true)
            KeyButton("◄", 80, handleKeyPress, isHorizontalLayout = true)
            KeyButton("▼", 81, handleKeyPress, isHorizontalLayout = true)
            KeyButton("▲", 82, handleKeyPress, isHorizontalLayout = true)
            KeyButton("►", 79, handleKeyPress, isHorizontalLayout = true)
        }
    }
}

@Composable
fun FullLayoutContent(
    handleKeyPress: (Int) -> Unit,
    shiftOn: Boolean,
    ctrlOn: Boolean,
    altOn: Boolean,
    metaOn: Boolean,
    onShiftToggle: () -> Unit,
    onCtrlToggle: () -> Unit,
    onAltToggle: () -> Unit,
    onMetaToggle: () -> Unit
) {
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
            KeyButton("PrtSc", 70, handleKeyPress)
            KeyButton("Home", 74, handleKeyPress)
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
            ModifierKeyButton("Shift", shiftOn, onShiftToggle, weight = 2.2f)
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
            ModifierKeyButton("Shift", shiftOn, onShiftToggle, weight = 2.8f)
        }
        // Row 6 (Bottom)
        KeyboardRow {
            ModifierKeyButton("Ctrl", ctrlOn, onCtrlToggle, weight = 1.2f)
            ModifierKeyButton("Win/Cmd", metaOn, onMetaToggle, weight = 1f)
            ModifierKeyButton("Alt", altOn, onAltToggle, weight = 1f)
            KeyButton("Space", 44, handleKeyPress, weight = 4f)
            ModifierKeyButton("Alt", altOn, onAltToggle, weight = 1f)
            ModifierKeyButton("Ctrl", ctrlOn, onCtrlToggle, weight = 1.2f)
            KeyButton("←", 80, handleKeyPress)
            KeyButton("↓", 81, handleKeyPress)
            KeyButton("↑", 82, handleKeyPress)
            KeyButton("→", 79, handleKeyPress)
        }
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
    weight: Float = 1f,
    isHorizontalLayout: Boolean = false
) {
    val displayLabel =
        if (label.length == 1 && shiftOn) label.uppercase() else if (label.length == 1) label.lowercase() else label
    
    val shape = if (isHorizontalLayout) RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                else RoundedCornerShape(6.dp)
    
    val bgColor = if (isHorizontalLayout) Color(0xFF252525) else Color(0xFF333333)

    Box(
        modifier = Modifier
            .weight(weight)
            .height(if (isHorizontalLayout) 60.dp else 54.dp)
            .clip(shape)
            .background(bgColor)
            .clickable { onPress(keyCode) },
        contentAlignment = Alignment.Center
    ) {
        if (isHorizontalLayout) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 2.dp, start = 4.dp, end = 4.dp, bottom = 30.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            )
        }
        
        Text(
            text = displayLabel,
            color = Color.White,
            fontSize = if (label.length > 1) 12.sp else if (isHorizontalLayout) 20.sp else 18.sp,
            fontWeight = if (isHorizontalLayout) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RowScope.ModifierKeyButton(
    label: String,
    isActive: Boolean,
    onToggle: () -> Unit,
    weight: Float = 1f,
    isHorizontalLayout: Boolean = false
) {
    val shape = if (isHorizontalLayout) RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                else RoundedCornerShape(6.dp)

    Box(
        modifier = Modifier
            .weight(weight)
            .height(if (isHorizontalLayout) 60.dp else 54.dp)
            .clip(shape)
            .background(if (isActive) Color(0xFF007ACC) else if (isHorizontalLayout) Color(0xFF3A3A3A) else Color(0xFF444444))
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
