package gamecontroller;

import model.board.actionspace.ActionSpace;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.player.FamilyMemberColor;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

/**
 * This interface specifies all the game-related events
 * (e.g. Player X occupies a certain action space)
 */
public interface GameEventsInterface {
    /**
     * Called when a new round starts
     *
     * @throws RemoteException
     */
    void onPrepareNewRound() throws RemoteException;

    /**
     * Called when new development cards are drawn
     * <p>
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
     *
     * @param blackDie  black die value
     * @param whiteDie  white die value
     * @param orangeDie orange die value
     * @throws RemoteException
     */
    void onDiceThrown(int blackDie, int whiteDie, int orangeDie) throws RemoteException;

    /**
     * Called when the turn of a player starts
     *
     * @param username the username of the player
     * @throws RemoteException
     */
    void onPlayerTurnStarted(String username) throws RemoteException;

    /**
     * Called when a player occupies an action space
     * @param username
     * @param actionSpaceId
     * @param familyMemberColor
     * @param councilPrivileges
     * @throws RemoteException
     */
    void onPlayerOccupiesActionSpace(String username, UUID actionSpaceId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges) throws RemoteException;

    /**
     * Called when a player occupies a floor
     * @param username
     * @param floorId
     * @param familyMemberColor
     * @param chosenPrivileges
     * @param paymentForCard
     * @throws RemoteException
     */
    void onPlayerOccupiesFloor(String username, UUID floorId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges, RequiredResourceSet paymentForCard) throws RemoteException;

    /**
     * Called when a player commits servants for his next action
     *
     * @param username
     * @param servants
     */
    void onPlayerSpendsServants(String username, int servants) throws RemoteException;
}
