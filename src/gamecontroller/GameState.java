package gamecontroller;

public enum GameState {
    /* ------------------------------------------------------
     * Initial phases
     * ------------------------------------------------------ */
    WAITING_FOR_PLAYERS_TO_CONNECT,
    INITIALIZING,           // Game just created and not fully configured
    DRAFTING_BONUS_TILES,    // Bonus tiles draft phase
    DRAFTING_LEADER_CARDS,
    STARTED,
    PLAYER_TURN,     // The player's turn, when they can perform actions
    PRODUCTION,
    HARVEST,
    CHOOSING_COUNCIL_PRIVILEGES
}
