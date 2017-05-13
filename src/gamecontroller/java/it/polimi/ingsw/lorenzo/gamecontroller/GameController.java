package it.polimi.ingsw.lorenzo.gamecontroller;

import it.polimi.ingsw.lorenzo.model.Game;
import it.polimi.ingsw.lorenzo.model.action.Action;
import it.polimi.ingsw.lorenzo.model.exceptions.CantOccupyActionSpaceException;
import it.polimi.ingsw.lorenzo.model.exceptions.CantPerformActionException;
import it.polimi.ingsw.lorenzo.model.player.FamilyMemberColor;
import it.polimi.ingsw.lorenzo.model.player.Player;
import it.polimi.ingsw.lorenzo.model.resource.ObtainedResourceSet;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;

/**
 * This class is responsible for handling player actions (raising exceptions if need be), updating the state and
 * making callbacks to report the changes.
 * It's shared by the client and the server so that both use the exact same logic for updating the state of the game.
 */
public class GameController {
    Game game;

    GameEventsInterface callback;

    public GameController(Game game, GameEventsInterface callback) {
        this.game = game;
        this.callback = callback;
    }

    public void startGame(){

    }

    /**
     * Performs the action and returns the set of resources obtained
     * @param the player performing the action
     * @param familyMemberValue the family member value used for performing the action
     * @returns an ArrayList representing the possible choices of resources that can be obtained from performing the action
     */
    ArrayList<ObtainedResourceSet> performAction(Player player, int familyMemberValue, Action action) throws CantPerformActionException {
        throw new NotImplementedException();
    }


    /**
     * Places player's family member in the action space and returns the associated action to be performed by the player
     * @param player the player that occupies this action space
     * @param familyMember the family member that the player uses
     * @return the action to be performed
     */
    public Action goToActionSpace(Player player, FamilyMemberColor familyMember) throws CantOccupyActionSpaceException {
        // TODO: 4/13/17
        throw new NotImplementedException();
    }
}
