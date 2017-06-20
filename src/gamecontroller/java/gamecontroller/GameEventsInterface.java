package gamecontroller;

import java.rmi.RemoteException;

/**
 * This interface specifies all the game-related events (e.g. Player X occupies a certain action space)
 */
public interface GameEventsInterface {
    /**
     * Called when the state of the game changes
     */
    void onGameStateChange(GameState gameState) throws RemoteException;
}
