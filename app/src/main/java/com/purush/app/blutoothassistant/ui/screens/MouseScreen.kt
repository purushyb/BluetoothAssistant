package com.purush.app.blutoothassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.purush.app.blutoothassistant.ui.theme.BlutoothAssistantTheme

@Composable
fun MouseScreen(
    modifier: Modifier = Modifier,
    onScroll: (Int) -> Unit,
    moveMouse: (Int, Int) -> Unit,
    onZoom: (Boolean) -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit
) {
    Column {
        Row(
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        )
        {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        var zoomAccumulator = 1f
                        detectTransformGestures { _, pan, zoom, _ ->
                            // Handle Mouse Movement (Pan)
                            if (pan.x != 0f || pan.y != 0f) {
                                moveMouse(pan.x.toInt(), pan.y.toInt())
                            }

                            // Handle Zoom (Pinch)
                            zoomAccumulator *= zoom
                            if (zoomAccumulator > 1.2f) {
                                onZoom(true) // Zoom In
                                zoomAccumulator = 1f
                            } else if (zoomAccumulator < 0.8f) {
                                onZoom(false) // Zoom Out
                                zoomAccumulator = 1f
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onLeftClick() },
                            onDoubleTap = { onRightClick() } // Right click on double tap for demo
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Drag to move mouse\nTap to click\nDouble tap to R-click\nPinch to Zoom")
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.Gray)
            )

            Box(
                modifier = Modifier
                    .width(50.dp)
                    .fillMaxHeight()
                    .background(Color.DarkGray.copy(alpha = 0.1f))
                    .pointerInput(Unit) {
                        var accumulatedY = 0f
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            accumulatedY += dragAmount.y
                            val steps = -(accumulatedY / 60).toInt()
                            if (steps != 0) {
                                onScroll(steps)
                                accumulatedY += (steps * 60) // Adding because steps is negative of accumulatedY/60
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Scroll", modifier = Modifier.rotate(90f))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    .clickable(onClick = onLeftClick),
                contentAlignment = Alignment.Center
            ) {}
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    .clickable(onClick = onRightClick),
                contentAlignment = Alignment.Center
            ) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MouseScreenPreview() {
    BlutoothAssistantTheme {
        MouseScreen(
            onScroll = {},
            moveMouse = { x, y -> Unit },
            onZoom = {},
            onLeftClick = {},
            onRightClick = {})
    }
}
