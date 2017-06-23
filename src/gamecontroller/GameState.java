package gamecontroller;

public enum GameState {
    /* ------------------------------------------------------
     * Initial phases
     * ------------------------------------------------------ */
    INITIALIZING,           // Game just created and not fully configured
    DRAFTING_LEADER_CARDS, WAITING_FOR_PLAYERS_TO_CONNECT, STARTED, DRAFTING_BONUS_TILES    // Bonus tiles draft phase
}
