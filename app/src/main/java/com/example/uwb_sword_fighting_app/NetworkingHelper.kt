// NetworkingHelper.kt

package com.example.swordfightinggame

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

/**
 * Helper class to manage connections using Nearby Connections API.
 */
class NetworkingHelper(
    private val context: Context,
    private val onGameStateReceived: (GameState) -> Unit
) {

    companion object {
        const val SERVICE_ID = "com.example.swordfightinggame.SERVICE_ID"
    }

    private val connectionsClient = Nearby.getConnectionsClient(context)
    private val endpointIds = mutableSetOf<String>()

    /**
     * Start advertising to nearby devices.
     */
    fun startAdvertising() {
        connectionsClient.startAdvertising(
            getUserNickname(),
            SERVICE_ID,
            connectionLifecycleCallback,
            AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        ).addOnSuccessListener {
            Log.d("NetworkingHelper", "Advertising started.")
        }.addOnFailureListener {
            Log.e("NetworkingHelper", "Advertising failed: ${it.message}")
        }
    }

    /**
     * Start discovering nearby devices.
     */
    fun startDiscovery() {
        connectionsClient.startDiscovery(
            SERVICE_ID,
            endpointDiscoveryCallback,
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        ).addOnSuccessListener {
            Log.d("NetworkingHelper", "Discovery started.")
        }.addOnFailureListener {
            Log.e("NetworkingHelper", "Discovery failed: ${it.message}")
        }
    }

    /**
     * Stop all connections.
     */
    fun stopAllConnections() {
        connectionsClient.stopAllEndpoints()
    }

    /**
     * Send the game state to connected devices.
     */
    fun sendGameState(gameState: GameState) {
        val payload = Payload.fromBytes(serializeGameState(gameState))
        endpointIds.forEach { endpointId ->
            connectionsClient.sendPayload(endpointId, payload)
        }
    }

    /**
     * Get a user nickname for identification.
     */
    private fun getUserNickname(): String {
        return "Player_${System.currentTimeMillis() % 1000}"
    }

    /**
     * Serialize the GameState object to a byte array.
     */
    private fun serializeGameState(gameState: GameState): ByteArray {
        // TODO: Implement serialization (e.g., using JSON or Protocol Buffers)
        // This is a placeholder implementation
        return gameState.toString().toByteArray()
    }

    /**
     * Deserialize the byte array back to a GameState object.
     */
    private fun deserializeGameState(bytes: ByteArray): GameState {
        // TODO: Implement deserialization
        // This is a placeholder implementation
        return GameState()
    }

    /**
     * Connection lifecycle callback.
     */
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            // Automatically accept the connection on both sides.
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                // Connection established
                endpointIds.add(endpointId)
                Log.d("NetworkingHelper", "Connected to $endpointId")
            } else {
                Log.e("NetworkingHelper", "Connection failed with $endpointId")
            }
        }

        override fun onDisconnected(endpointId: String) {
            endpointIds.remove(endpointId)
            Log.d("NetworkingHelper", "Disconnected from $endpointId")
        }
    }

    /**
     * Endpoint discovery callback.
     */
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            // Request connection to the discovered endpoint
            connectionsClient.requestConnection(getUserNickname(), endpointId, connectionLifecycleCallback)
        }

        override fun onEndpointLost(endpointId: String) {
            // Endpoint lost
            Log.d("NetworkingHelper", "Endpoint lost: $endpointId")
        }
    }

    /**
     * Payload callback for receiving data.
     */
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            payload.asBytes()?.let { bytes ->
                val receivedGameState = deserializeGameState(bytes)
                onGameStateReceived(receivedGameState)
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Handle payload transfer updates if needed
        }
    }
}
