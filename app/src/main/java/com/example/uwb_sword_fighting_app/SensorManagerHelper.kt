package com.example.uwb_sword_fighting_app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class SensorManagerHelper(
    private val context: Context,
    private val swingCallback: (isSwingDetected: Boolean) -> Unit
) : SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null

    init {
        // Initialize the accelerometer sensor
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    fun registerSensors() {
        // Register the accelerometer sensor
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("SensorManagerHelper", "Accelerometer registered.")
        } ?: run {
            Log.e("SensorManagerHelper", "Accelerometer not available.")
        }
    }

    fun unregisterSensors() {
        // Unregister the accelerometer sensor
        sensorManager.unregisterListener(this)
        Log.d("SensorManagerHelper", "Accelerometer unregistered.")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Check for a "swing" motion based on accelerometer data
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            // Detect swing motion based on a threshold
            val swingDetected = Math.sqrt((x * x + y * y + z * z).toDouble()) > 15
            if (swingDetected) {
                Log.d("SensorManagerHelper", "Swing detected!")
                swingCallback(true)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle sensor accuracy changes if needed
    }
}
