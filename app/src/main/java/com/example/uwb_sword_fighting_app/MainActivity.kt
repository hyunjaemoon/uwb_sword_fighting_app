// MainActivity.kt

package com.example.uwb_sword_fighting_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.forEach

class MainActivity : AppCompatActivity() {

    private lateinit var uwbManagerHelper: UwbManagerHelper
    private lateinit var sensorManagerHelper: SensorManagerHelper
    private lateinit var networkingHelper: NetworkingHelper
    private lateinit var gameState: GameState
    private lateinit var rangingStatusLabel: TextView
    private lateinit var roleRadioGroup: RadioGroup

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
    }

    @SuppressLint("SetTextI18n")
    fun updateRangingStatus(isRangingRunning: Boolean) {
        runOnUiThread { // Update UI on the main thread
            if (isRangingRunning) {
                rangingStatusLabel.text = "Ranging Status: Active"
                roleRadioGroup.forEach { radioButton -> radioButton.isEnabled = false }
            } else {
                rangingStatusLabel.text = "Ranging Status: Idle"
                roleRadioGroup.forEach { radioButton -> radioButton.isEnabled = true }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view to your activity_main.xml
        setContentView(R.layout.activity_main)

        // Check and request necessary permissions
        checkPermissions()

        // Initialize game state
        gameState = GameState()

        // Initialize UWB Manager Helper
        uwbManagerHelper = UwbManagerHelper(this) { distance, azimuth ->
            // Update game state based on UWB data
            updateGameState(distance, azimuth)
        }

        // Initialize Sensor Manager Helper
        sensorManagerHelper = SensorManagerHelper(this) { isSwingDetected ->
            // Handle swing detection
            if (isSwingDetected) {
                gameState.performAttack()
                networkingHelper.sendGameState(gameState)
            }
        }

        // Initialize Networking Helper
        networkingHelper = NetworkingHelper(this) { receivedGameState ->
            // Update game state based on received data
            gameState.updateFromOpponent(receivedGameState)
            // Update UI accordingly
        }

        // Start necessary services
        val startGameButton: Button = findViewById(R.id.startGameButton)
        val stopGameButton: Button = findViewById(R.id.stopGameButton)
        rangingStatusLabel = findViewById(R.id.rangingStatusLabel)
        roleRadioGroup = findViewById(R.id.roleRadioGroup)

        startGameButton.setOnClickListener{
            uwbManagerHelper.startRangingSession()
            sensorManagerHelper.registerSensors()
            networkingHelper.startAdvertising()
            networkingHelper.startDiscovery()
        }

        stopGameButton.setOnClickListener {
            uwbManagerHelper.stopRangingSession()
            sensorManagerHelper.unregisterSensors()
            networkingHelper.stopAllConnections()
        }

        roleRadioGroup.setOnCheckedChangeListener{
            _, checkedId ->
            when (checkedId) {
                R.id.controllerRadioButton -> {
                    uwbManagerHelper.setUwbRole(UwbRole.CONTROLLER)
                    networkingHelper.setNetworkingRole(NetworkingRole.ADVERTISER)
                }
                R.id.controleeRadioButton -> {
                    uwbManagerHelper.setUwbRole(UwbRole.CONTROLEE)
                    networkingHelper.setNetworkingRole(NetworkingRole.SCANNER)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources
        sensorManagerHelper.unregisterSensors()
        uwbManagerHelper.stopRangingSession()
        networkingHelper.stopAllConnections()
    }

    /**
     * Check and request necessary permissions.
     */
    private fun checkPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.UWB_RANGING,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        )

        val missingPermissions = requiredPermissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    /**
     * Handle permission request results.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(
                    this,
                    "All permissions are required for the app to function.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    /**
     * Update the game state based on UWB ranging data.
     */
    private fun updateGameState(distance: Float, azimuth: Float) {
        // Use distance and azimuth to determine if an attack can hit
        if (distance < GameState.ATTACK_RANGE && gameState.isAttacking) {
            Log.d("UwbSwordFightingApp", "Attack hit!: Distance=$distance, Azimuth=$azimuth")
            // Successful hit
            gameState.opponentHealth -= GameState.ATTACK_DAMAGE
            networkingHelper.sendGameState(gameState)
            // Update UI accordingly
        }
    }
}
