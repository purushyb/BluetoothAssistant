package com.purush.app.blutoothassistant.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppQosSettings
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import java.util.concurrent.Executor

class BluetoothHidController(private val context: Context) {

    private var bluetoothHidDevice: BluetoothHidDevice? = null
    private var hostDevice: BluetoothDevice? = null
    private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    var onConnectionStateChanged: ((Boolean) -> Unit)? = null

    private val userExecutor = Executor { command -> command.run() }

    private val callback = object : BluetoothHidDevice.Callback() {
        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
            Log.d(TAG, "onAppStatusChanged: registered=$registered device=$pluggedDevice")
            if (registered) {
                // If we are registered, we can potentially connect or wait for connection
                // For this simple example, we wait for incoming connections usually,
                // but we can also initiate if we have a bonded device.
            }
        }

        override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
            Log.d(TAG, "onConnectionStateChanged: device=$device state=$state")
            if (state == BluetoothProfile.STATE_CONNECTED) {
                hostDevice = device
                onConnectionStateChanged?.invoke(true)
            } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                hostDevice = null
                onConnectionStateChanged?.invoke(false)
            }
        }
    }

    private val serviceListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                Log.d(TAG, "HID Device Proxy connected")
                bluetoothHidDevice = proxy as BluetoothHidDevice
                registerApp()
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                Log.d(TAG, "HID Device Proxy disconnected")
                bluetoothHidDevice = null
            }
        }
    }

    fun init() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.getProfileProxy(context, serviceListener, BluetoothProfile.HID_DEVICE)
        }
    }

    @SuppressLint("MissingPermission")
    private fun registerApp() {
        val sdpSettings = BluetoothHidDeviceAppSdpSettings(
            "Bluetooth Assistant",
            "Android HID",
            "Android",
            BluetoothHidDevice.SUBCLASS1_COMBO,
            HID_REPORT_DESC
        )

        val qosSettings = BluetoothHidDeviceAppQosSettings(
            BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
            800,
            900,
            BluetoothHidDeviceAppQosSettings.MAX,
            11250,
            BluetoothHidDeviceAppQosSettings.MAX
        )

        bluetoothHidDevice?.registerApp(
            sdpSettings,
            null,
            qosSettings,
            userExecutor,
            callback
        )
    }

    @SuppressLint("MissingPermission")
    fun unregisterApp() {
        bluetoothHidDevice?.unregisterApp()
    }

    @SuppressLint("MissingPermission")
    fun sendKeyboardReport(modifier: Int, key: Int) {
        val report = ByteArray(8)
        report[0] = modifier.toByte()
        report[1] = 0 // Reserved
        report[2] = key.toByte() // Key 1
        // Rest are 0

        hostDevice?.let {
            bluetoothHidDevice?.sendReport(it, ID_KEYBOARD, report)
        }
    }

    @SuppressLint("MissingPermission")
    fun releaseKey() {
        val report = ByteArray(8) // All zeros
        hostDevice?.let {
            bluetoothHidDevice?.sendReport(it, ID_KEYBOARD, report)
        }
    }

    @SuppressLint("MissingPermission")
    fun sendMouseReport(dx: Int, dy: Int, leftButton: Boolean, rightButton: Boolean) {
        val report = ByteArray(4)
        var buttons = 0
        if (leftButton) buttons = buttons or 1
        if (rightButton) buttons = buttons or 2

        report[0] = buttons.toByte()
        report[1] = dx.toByte()
        report[2] = dy.toByte()
        report[3] = 0 // Wheel/Optional

        hostDevice?.let {
            bluetoothHidDevice?.sendReport(it, ID_MOUSE, report)
        }
    }

    companion object {
        const val TAG = "BluetoothHidController"
        const val ID_KEYBOARD = 1
        const val ID_MOUSE = 2

        val HID_REPORT_DESC = byteArrayOf(
            // Keyboard
            0x05.toByte(), 0x01.toByte(), // Usage Page (Generic Desktop)
            0x09.toByte(), 0x06.toByte(), // Usage (Keyboard)
            0xA1.toByte(), 0x01.toByte(), // Collection (Application)
            0x85.toByte(), ID_KEYBOARD.toByte(), // Report ID (1)
            0x05.toByte(), 0x07.toByte(), // Usage Page (Key Codes)
            0x19.toByte(), 0xE0.toByte(), // Usage Minimum (224)
            0x29.toByte(), 0xE7.toByte(), // Usage Maximum (231)
            0x15.toByte(), 0x00.toByte(), // Logical Minimum (0)
            0x25.toByte(), 0x01.toByte(), // Logical Maximum (1)
            0x75.toByte(), 0x01.toByte(), // Report Size (1)
            0x95.toByte(), 0x08.toByte(), // Report Count (8)
            0x81.toByte(), 0x02.toByte(), // Input (Data, Variable, Absolute) - Modifier byte
            0x95.toByte(), 0x01.toByte(), // Report Count (1)
            0x75.toByte(), 0x08.toByte(), // Report Size (8)
            0x81.toByte(), 0x01.toByte(), // Input (Constant) - Reserved byte
            0x95.toByte(), 0x05.toByte(), // Report Count (5)
            0x75.toByte(), 0x01.toByte(), // Report Size (1)
            0x05.toByte(), 0x08.toByte(), // Usage Page (LEDs)
            0x19.toByte(), 0x01.toByte(), // Usage Minimum (1)
            0x29.toByte(), 0x05.toByte(), // Usage Maximum (5)
            0x91.toByte(), 0x02.toByte(), // Output (Data, Variable, Absolute) - LEDs
            0x95.toByte(), 0x01.toByte(), // Report Count (1)
            0x75.toByte(), 0x03.toByte(), // Report Size (3)
            0x91.toByte(), 0x01.toByte(), // Output (Constant) - LED padding
            0x95.toByte(), 0x06.toByte(), // Report Count (6)
            0x75.toByte(), 0x08.toByte(), // Report Size (8)
            0x15.toByte(), 0x00.toByte(), // Logical Minimum (0)
            0x25.toByte(), 0x65.toByte(), // Logical Maximum (101)
            0x05.toByte(), 0x07.toByte(), // Usage Page (Key Codes)
            0x19.toByte(), 0x00.toByte(), // Usage Minimum (0)
            0x29.toByte(), 0x65.toByte(), // Usage Maximum (101)
            0x81.toByte(), 0x00.toByte(), // Input (Data, Array) - Key arrays (6 bytes)
            0xC0.toByte(), // End Collection

            // Mouse
            0x05.toByte(), 0x01.toByte(), // Usage Page (Generic Desktop)
            0x09.toByte(), 0x02.toByte(), // Usage (Mouse)
            0xA1.toByte(), 0x01.toByte(), // Collection (Application)
            0x85.toByte(), ID_MOUSE.toByte(), // Report ID (2)
            0x09.toByte(), 0x01.toByte(), // Usage (Pointer)
            0xA1.toByte(), 0x00.toByte(), // Collection (Physical)
            0x05.toByte(), 0x09.toByte(), // Usage Page (Button)
            0x19.toByte(), 0x01.toByte(), // Usage Minimum (1)
            0x29.toByte(), 0x03.toByte(), // Usage Maximum (3)
            0x15.toByte(), 0x00.toByte(), // Logical Minimum (0)
            0x25.toByte(), 0x01.toByte(), // Logical Maximum (1)
            0x95.toByte(), 0x03.toByte(), // Report Count (3)
            0x75.toByte(), 0x01.toByte(), // Report Size (1)
            0x81.toByte(), 0x02.toByte(), // Input (Data, Variable, Absolute) - Buttons
            0x95.toByte(), 0x01.toByte(), // Report Count (1)
            0x75.toByte(), 0x05.toByte(), // Report Size (5)
            0x81.toByte(), 0x03.toByte(), // Input (Constant) - Padding
            0x05.toByte(), 0x01.toByte(), // Usage Page (Generic Desktop)
            0x09.toByte(), 0x30.toByte(), // Usage (X)
            0x09.toByte(), 0x31.toByte(), // Usage (Y)
            0x09.toByte(), 0x38.toByte(), // Usage (Wheel)
            0x15.toByte(), 0x81.toByte(), // Logical Minimum (-127)
            0x25.toByte(), 0x7F.toByte(), // Logical Maximum (127)
            0x75.toByte(), 0x08.toByte(), // Report Size (8)
            0x95.toByte(), 0x03.toByte(), // Report Count (3)
            0x81.toByte(), 0x06.toByte(), // Input (Data, Variable, Relative) - X, Y, Wheel
            0xC0.toByte(), // End Collection
            0xC0.toByte()  // End Collection
        )
    }
}
