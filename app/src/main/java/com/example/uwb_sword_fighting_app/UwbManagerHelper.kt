package com.example.uwb_sword_fighting_app

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import kotlinx.coroutines.*
import androidx.core.uwb.UwbManager

class UwbManagerHelper(
    context: Context,
    private val uwbCallback: (distance: Float, azimuth: Float) -> Unit
) {

    private var mUwbManager: UwbManager? = null
    private var mContext: Context = context
    private var job: Job? = null

    init {
        if (isUwbSupported()) {
            mUwbManager = UwbManager.createInstance(mContext)
        } else {
            Log.e("UwbManagerHelper", "UWB is not supported on this device.")
        }
    }

    // create the uwb manager is supported by this device
    private fun isUwbSupported(): Boolean {
        var packageManager: PackageManager = mContext.packageManager
        return packageManager.hasSystemFeature("android.hardware.uwb")
    }

    fun startRangingSession() {
        // Mock implementation for starting a UWB ranging session
        Log.d("UwbManagerHelper", "Starting UWB ranging session.")
        job = CoroutineScope(Dispatchers.Main.immediate).launch {
            startRanging()
        }
        // Simulate UWB data updates (distance and azimuth)
        simulateUwbData()
    }

    fun stopRangingSession() {
        // Mock implementation for stopping a UWB ranging session
        Log.d("UwbManagerHelper", "Stopping UWB ranging session.")
        job?.cancel()
    }

    private fun startRanging() {
        // ... (Implementation from the Android documentation example) ...
        // Replace placeholders like listenForPartnersAddress, broadcastMyParameters,
        // and doSomethingWithPosition with your actual logic.
    }

    private fun simulateUwbData() {
        // Simulate periodic UWB data updates for testing
        Thread {
            while (true) {
                try {
                    Thread.sleep(1000) // Update every second
                    val simulatedDistance = (1..5).random().toFloat() // Random distance between 1-5 meters
                    val simulatedAzimuth = (0..360).random().toFloat() // Random azimuth between 0-360 degrees
                    uwbCallback(simulatedDistance, simulatedAzimuth)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    break
                }
            }
        }.start()
    }
}
