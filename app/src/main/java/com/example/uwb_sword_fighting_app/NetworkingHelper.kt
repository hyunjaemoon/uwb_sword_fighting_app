package com.example.uwb_sword_fighting_app

import android.content.Context
import android.util.Log

class NetworkingHelper(
    private val context: Context,
    private val onGameStateReceived: (gameState: GameState) -> Unit
) {

    private var isAdvertising = false
    private var isDiscovering = false

    fun startAdvertising() {
        // Mock implementation for starting the advertising process
        isAdvertising = true
        Log.d("NetworkingHelper", "Started advertising.")
        simulateIncomingGameState()
    }

    fun startDiscovery() {
        // Mock implementation for starting the discovery process
        isDiscovering = true
        Log.d("NetworkingHelper", "Started discovery.")
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
                    break
                }
            }
        }.start()
    }
}
