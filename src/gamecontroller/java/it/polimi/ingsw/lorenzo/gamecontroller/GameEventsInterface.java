package it.polimi.ingsw.lorenzo.gamecontroller;

import it.polimi.ingsw.lorenzo.model.action.Action;
import it.polimi.ingsw.lorenzo.model.board.actionspace.ActionSpace;
import it.polimi.ingsw.lorenzo.model.player.FamilyMemberColor;
import it.polimi.ingsw.lorenzo.model.player.Player;

/**
 * This interface specifies all the game-related events (e.g. Player X occupies a certain action space)
 */
public interface GameEventsInterface {
    /**
     * Called when the game is started
     */
    void onGameStart();

    /**
     * Called when a player's turn starts
     * @param player
     */
    void onPlayerTurnStart(Player player);

    /**
     * Called when a player occupies an action space
     * @param player
     * @param familyMemberColor
     * @param actionSpace
     */
    void onPlayerOccupiesActionSpace(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace);

    /**
     * Called when a player performs an action
     * @param player
     * @param action
     */
    void onPlayerPerformsAction(Player player, Action action);


}
