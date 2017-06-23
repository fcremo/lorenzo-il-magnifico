package gamecontroller;

import model.player.Player;

import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface specifies all the game-related events (e.g. Player X occupies a certain action space)
 */
public interface GameEventsInterface {
    /**
     * Called when the state of the game changes
     */
    void onGameStateChange(GameState gameState) throws RemoteException;

    /**
     * Called when the turn order changes
     * @param playerOrder the new turn order
     * @throws RemoteException
     */
    void onTurnOrderChanged(List<Player> playerOrder) throws RemoteException;

    /**
     * Called when the turn of a player starts
     * @param player
     * @throws RemoteException
     */
    void onPlayerTurnStarted(Player player) throws RemoteException;
}
