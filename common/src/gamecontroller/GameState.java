package gamecontroller;

public enum GameState {
    WAITING_FOR_PLAYERS_TO_CONNECT,
    STARTED,                            // The game is just started
    DRAFTING_BONUS_TILES,               // Bonus tiles draft phase
    DRAFTING_LEADER_CARDS,              // Leader cards draft phase
    PLAYER_TURN,                        // The player's turn, when they can perform actions
    PRODUCTION,                         // Production phase
    HARVEST,                            // Harvest phase
    TAKING_CARD,                        // Taking a card after occupying an action space
    VATICAN_REPORT,                     // Vatican report phase
}
