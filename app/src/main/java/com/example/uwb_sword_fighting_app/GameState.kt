// GameState.kt

package com.example.uwb_sword_fighting_app

/**
 * A class to manage the game state, including player health and actions.
 */
data class GameState(
    var isAttacking: Boolean = false,
    var isDefending: Boolean = false,
    var playerHealth: Float = 100f,
    var opponentHealth: Float = 100f
) {
    companion object {
        const val ATTACK_DAMAGE = 10f
        const val ATTACK_RANGE = 2.0f // Meters
    }

    /**
     * Perform an attack action.
     */
    fun performAttack() {
        isAttacking = true
        isDefending = false
    }

    /**
     * Perform a defense action.
     */
    fun performDefense() {
        isDefending = true
        isAttacking = false
    }

    /**
     * Update game state based on opponent's game state.
     */
    fun updateFromOpponent(opponentGameState: GameState) {
        // Handle incoming game state updates
        if (opponentGameState.isAttacking && isDefending.not()) {
            playerHealth -= ATTACK_DAMAGE
        }
    }
}
