package gamecontroller;

public enum GameState {
    /* ------------------------------------------------------
     * Initial phases
     * ------------------------------------------------------ */
    INITIALIZING,           // Game just created and not fully configured
    DRAFTING_LEADER_CARDS, DRAFTING_BONUS_TILES    // Bonus tiles draft phase
}
