package gamecontroller;

import model.board.actionspace.ActionSpace;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.player.FamilyMemberColor;
import model.player.Player;

import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface specifies all the game-related events
 * (e.g. Player X occupies a certain action space)
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
     * Called when new development cards are drawn
     *
     * The list of territory cards must be ordered from lower to higher floors
     *
     * @param territoryCards
     * @param characterCards
     * @param buildingCards
     * @param ventureCards
     * @throws RemoteException
     */
    void onCardsDrawn(List<TerritoryCard> territoryCards, List<CharacterCard> characterCards, List<BuildingCard> buildingCards, List<VentureCard> ventureCards) throws RemoteException;

    /**
     * Called when the dice are thrown
     * @param blackDie black die value
     * @param whiteDie white die value
     * @param orangeDie orange die value
     * @throws RemoteException
     */
    void onDiceThrown(int blackDie, int whiteDie, int orangeDie) throws RemoteException;

    /**
     * Called when the turn of a player starts
     * @param player
     * @throws RemoteException
     */
    void onPlayerTurnStarted(Player player) throws RemoteException;

    /**
     * Called when a player occupies an action space
     * @param player the player
     * @param familyMemberColor the family member used
     * @param actionSpace the action space occupied
     * @throws RemoteException
     */
    void onPlayerOccupiesActionSpace(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace) throws RemoteException;
}
