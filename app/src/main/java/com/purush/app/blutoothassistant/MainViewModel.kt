package com.purush.app.blutoothassistant

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import com.purush.app.blutoothassistant.bluetooth.BluetoothHidController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.core.content.edit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothHidController = BluetoothHidController(application)
    private var lastDevice: BluetoothDevice? = null
    private val prefs = application.getSharedPreferences("bluetooth_prefs", android.content.Context.MODE_PRIVATE)
    private val LAST_DEVICE_ADDRESS_KEY = "last_device_address"

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    init {
        bluetoothHidController.init()
        bluetoothHidController.onConnectionStateChanged = { connected, device ->
            _isConnected.value = connected
            if (connected && device != null) {
                lastDevice = device
                prefs.edit { putString(LAST_DEVICE_ADDRESS_KEY, device.address) }
            }
        }
        bluetoothHidController.onServiceConnected = {
            _isServiceRunning.value = true
            val savedAddress = prefs.getString(LAST_DEVICE_ADDRESS_KEY, null)
            if (savedAddress != null) {
                 bluetoothHidController.connect(savedAddress)
            }
        }
    }

    fun onResume() {
        if (_isServiceRunning.value) {
            bluetoothHidController.registerApp()
        }

        if (!_isConnected.value && _isServiceRunning.value) {
            val savedAddress = prefs.getString(LAST_DEVICE_ADDRESS_KEY, null)
            if (savedAddress != null) {
                bluetoothHidController.connect(savedAddress)
            } else if (lastDevice != null) {
                bluetoothHidController.connect(lastDevice!!)
            }
        }
    }

    fun startBluetoothService() {
        // In a real app, we might want to ensure permissions here too, 
        // but we rely on UI to check before calling this.
        bluetoothHidController.registerApp()
        _isServiceRunning.value = true
        
        // Try to reconnect if we have a saved device
        val savedAddress = prefs.getString(LAST_DEVICE_ADDRESS_KEY, null)
        if (savedAddress != null) {
            bluetoothHidController.connect(savedAddress)
        }
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

    fun scroll(amount: Int) {
        bluetoothHidController.sendMouseReport(0, 0, false, false, amount)
    }

    fun zoom(zoomIn: Boolean) {
        // 1 is Left Ctrl
        bluetoothHidController.sendKeyboardReport(1, 0)
        // Scroll step
        val scrollAmount = if (zoomIn) 1 else -1
        bluetoothHidController.sendMouseReport(0, 0, false, false, scrollAmount)
        // Reset scroll (optional, but good practice to zero it out if we are simulating steps)
        bluetoothHidController.sendMouseReport(0, 0, false, false, 0)
        // Release Ctrl
        bluetoothHidController.sendKeyboardReport(0, 0)
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
