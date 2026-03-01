package com.purush.app.blutoothassistant

import android.annotation.SuppressLint
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

    private val _connectedDeviceName = MutableStateFlow<String?>(null)
    val connectedDeviceName: StateFlow<String?> = _connectedDeviceName.asStateFlow()

    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    init {
        bluetoothHidController.init()
        bluetoothHidController.onConnectionStateChanged = { connected, device ->
            _isConnected.value = connected
            if (connected && device != null) {
                lastDevice = device
                prefs.edit { putString(LAST_DEVICE_ADDRESS_KEY, device.address) }
                try {
                    @SuppressLint("MissingPermission")
                    val name = device.name
                    _connectedDeviceName.value = name ?: "Unknown Device"
                } catch(e: Exception) {
                    _connectedDeviceName.value = "Unknown Device"
                }
            } else {
                _connectedDeviceName.value = null
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

    fun disconnect() {
        bluetoothHidController.disconnect()
    }

    fun sendSpecialKey(keycode: Int, modifier: Int = 0) {
        bluetoothHidController.sendKeyboardReport(modifier, keycode)
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

    // Returns Pair(modifier, keycode)
    private fun mapCharToHidCode(c: Char): Pair<Int, Int> {
        val isShift = c.isUpperCase()
        val shiftMod = if (isShift) 0x02 else 0x00
        
        return when (c.lowercaseChar()) {
            'a' -> Pair(shiftMod, 4)
            'b' -> Pair(shiftMod, 5)
            'c' -> Pair(shiftMod, 6)
            'd' -> Pair(shiftMod, 7)
            'e' -> Pair(shiftMod, 8)
            'f' -> Pair(shiftMod, 9)
            'g' -> Pair(shiftMod, 10)
            'h' -> Pair(shiftMod, 11)
            'i' -> Pair(shiftMod, 12)
            'j' -> Pair(shiftMod, 13)
            'k' -> Pair(shiftMod, 14)
            'l' -> Pair(shiftMod, 15)
            'm' -> Pair(shiftMod, 16)
            'n' -> Pair(shiftMod, 17)
            'o' -> Pair(shiftMod, 18)
            'p' -> Pair(shiftMod, 19)
            'q' -> Pair(shiftMod, 20)
            'r' -> Pair(shiftMod, 21)
            's' -> Pair(shiftMod, 22)
            't' -> Pair(shiftMod, 23)
            'u' -> Pair(shiftMod, 24)
            'v' -> Pair(shiftMod, 25)
            'w' -> Pair(shiftMod, 26)
            'x' -> Pair(shiftMod, 27)
            'y' -> Pair(shiftMod, 28)
            'z' -> Pair(shiftMod, 29)
            '1', '!' -> Pair(if (c == '!') 0x02 else 0, 30)
            '2', '@' -> Pair(if (c == '@') 0x02 else 0, 31)
            '3', '#' -> Pair(if (c == '#') 0x02 else 0, 32)
            '4', '$' -> Pair(if (c == '$') 0x02 else 0, 33)
            '5', '%' -> Pair(if (c == '%') 0x02 else 0, 34)
            '6', '^' -> Pair(if (c == '^') 0x02 else 0, 35)
            '7', '&' -> Pair(if (c == '&') 0x02 else 0, 36)
            '8', '*' -> Pair(if (c == '*') 0x02 else 0, 37)
            '9', '(' -> Pair(if (c == '(') 0x02 else 0, 38)
            '0', ')' -> Pair(if (c == ')') 0x02 else 0, 39)
            '\n' -> Pair(0, 40)
            '\b' -> Pair(0, 42)
            '\t' -> Pair(0, 43)
            ' ' -> Pair(0, 44)
            '-', '_' -> Pair(if (c == '_') 0x02 else 0, 45)
            '=', '+' -> Pair(if (c == '+') 0x02 else 0, 46)
            '[', '{' -> Pair(if (c == '{') 0x02 else 0, 47)
            ']', '}' -> Pair(if (c == '}') 0x02 else 0, 48)
            '\\', '|' -> Pair(if (c == '|') 0x02 else 0, 49)
            ';', ':' -> Pair(if (c == ':') 0x02 else 0, 51)
            '\'', '"' -> Pair(if (c == '"') 0x02 else 0, 52)
            '`', '~' -> Pair(if (c == '~') 0x02 else 0, 53)
            ',', '<' -> Pair(if (c == '<') 0x02 else 0, 54)
            '.', '>' -> Pair(if (c == '>') 0x02 else 0, 55)
            '/', '?' -> Pair(if (c == '?') 0x02 else 0, 56)
            else -> Pair(0, 0)
        }
    }
}
