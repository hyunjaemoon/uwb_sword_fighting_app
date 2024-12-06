package com.example.uwb_sword_fighting_app

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log

enum class NetworkingRole {
    ADVERTISER,
    SCANNER
}

class NetworkingHelper(
    context: Context,
    private val onGameStateReceived: (gameState: GameState) -> Unit
) {

    private var isAdvertising = false
    private var isDiscovering = false
    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null
    private var mContext: Context = context
    private var mNetworkingRole: NetworkingRole = NetworkingRole.SCANNER

    fun setNetworkingRole(role: NetworkingRole) {
        mNetworkingRole = role
    }

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d("NetworkingHelper", "Scan result: $result")
        }
    }

    init {
        mBluetoothManager = mContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothLeScanner = mBluetoothManager?.adapter?.bluetoothLeScanner
    }

    fun startAdvertising() {
        // Skip if role is not ADVERTISER
        if (mNetworkingRole != NetworkingRole.ADVERTISER) {
            return
        }

        // Mock implementation for starting the advertising process
        isAdvertising = true
        Log.d("NetworkingHelper", "Started advertising")
        simulateIncomingGameState()
    }

    @SuppressLint("MissingPermission")
    fun startDiscovery() {
        // Skip if role is not ADVERTISER
        if (mNetworkingRole != NetworkingRole.SCANNER) {
            return
        }

        // Mock implementation for starting the discovery process
        isDiscovering = true
        Log.d("NetworkingHelper", "Started discovery as $mNetworkingRole.")
        mBluetoothLeScanner?.startScan(leScanCallback)
    }

    fun sendGameState(gameState: GameState) {
        // Mock implementation for sending game state to the opponent
        Log.d("NetworkingHelper", "Sending game state: $gameState")
        // Here you would send the game state using a networking library
    }

    fun stopAllConnections() {
        // Mock implementation for stopping all networking activities
        isAdvertising = false
        isDiscovering = false
        Log.d("NetworkingHelper", "Stopped all connections.")
    }

    private fun simulateIncomingGameState() {
        // Simulate receiving game state updates for testing
        Thread {
            while (isAdvertising) {
                try {
                    Thread.sleep(2000) // Simulate receiving data every 2 seconds
                    val simulatedGameState = GameState().apply {
                        opponentHealth -= 5 // Simulate the opponent losing health
                    }
                    Log.d("NetworkingHelper", "Received simulated game state: $simulatedGameState")
                    onGameStateReceived(simulatedGameState)
                } catch (e: InterruptedException) {
                    Log.e("NetworkingHelper", "Error: $e")
                    break
                }
            }
        }.start()
    }
}
