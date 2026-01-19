package com.purush.app.blutoothassistant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.purush.app.blutoothassistant.bluetooth.BluetoothHidController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothHidController = BluetoothHidController(application)

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    init {
        bluetoothHidController.init()
        bluetoothHidController.onConnectionStateChanged = { connected ->
            _isConnected.value = connected
        }
    }

    fun startBluetoothService() {
        // In a real app, we might want to ensure permissions here too, 
        // but we rely on UI to check before calling this.
        // Re-registering effectively "starts" the advertisement/visibility
        // bluetoothHidController.registerApp() // logic is inside init for now but good to have explicit control
        _isServiceRunning.value = true
    }
    
    fun stopBluetoothService() {
        bluetoothHidController.unregisterApp()
        _isServiceRunning.value = false
    }

    fun sendChar(char: Char) {
        // Simple mapping for demo. Real mapping is complex.
        val keycode = mapCharToHidCode(char)
        if (keycode != 0) {
            bluetoothHidController.sendKeyboardReport(0, keycode)
            bluetoothHidController.releaseKey()
        }
    }

    fun sendEnter() {
        bluetoothHidController.sendKeyboardReport(0, 40) // 40 is Enter
        bluetoothHidController.releaseKey()
    }
    
    fun sendBackspace() {
        bluetoothHidController.sendKeyboardReport(0, 42) // 42 is Backspace
        bluetoothHidController.releaseKey()
    }

    fun moveMouse(dx: Int, dy: Int) {
        bluetoothHidController.sendMouseReport(dx, dy, false, false)
    }

    fun mouseClick(left: Boolean) {
        bluetoothHidController.sendMouseReport(0, 0, left, !left)
        // Release immediately for a click
        bluetoothHidController.sendMouseReport(0, 0, false, false)
    }

    private fun mapCharToHidCode(c: Char): Int {
        return when (c.lowercaseChar()) {
            'a' -> 4
            'b' -> 5
            'c' -> 6
            'd' -> 7
            'e' -> 8
            'f' -> 9
            'g' -> 10
            'h' -> 11
            'i' -> 12
            'j' -> 13
            'k' -> 14
            'l' -> 15
            'm' -> 16
            'n' -> 17
            'o' -> 18
            'p' -> 19
            'q' -> 20
            'r' -> 21
            's' -> 22
            't' -> 23
            'u' -> 24
            'v' -> 25
            'w' -> 26
            'x' -> 27
            'y' -> 28
            'z' -> 29
            '1' -> 30
            '2' -> 31
            '3' -> 32
            '4' -> 33
            '5' -> 34
            '6' -> 35
            '7' -> 36
            '8' -> 37
            '9' -> 38
            '0' -> 39
            ' ' -> 44
            else -> 0
        }
    }
}
