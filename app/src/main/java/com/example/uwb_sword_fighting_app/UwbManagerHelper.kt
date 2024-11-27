package com.example.uwb_sword_fighting_app

import android.content.Context
import android.util.Log

class UwbManagerHelper(
    private val context: Context,
    private val uwbCallback: (distance: Float, azimuth: Float) -> Unit
) {

    fun startRangingSession() {
        // Mock implementation for starting a UWB ranging session
        Log.d("UwbManagerHelper", "Starting UWB ranging session.")
        // Simulate UWB data updates (distance and azimuth)
        simulateUwbData()
    }

    fun stopRangingSession() {
        // Mock implementation for stopping a UWB ranging session
        Log.d("UwbManagerHelper", "Stopping UWB ranging session.")
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
                    break
                }
            }
        }.start()
    }
}
