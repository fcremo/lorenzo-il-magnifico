package gamecontroller;

import model.Game;
import model.action.Action;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.card.effects.interfaces.OncePerTurnEffectInterface;
import model.card.leader.LeaderCard;
import model.exceptions.CantPerformActionException;
import model.player.FamilyMemberColor;
import model.player.Player;
import model.resource.CouncilPrivilege;
import model.resource.ObtainedResourceSet;
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

    public void startGame() {
    }

    public void throwDices() {
    }

    public void spendServants(Player player, int servants) {
    }

    public void goToFloor(Player player, FamilyMemberColor familyMemberColor, Floor floor) {
    }

    public void goToSmallHarvest(Player player, FamilyMemberColor familyMemberColor) {
    }

    public void goToBigHarvest(Player player, FamilyMemberColor familyMemberColor) {
    }

    public void goToSmallProducion(Player player, FamilyMemberColor familyMemberColor) {
    }

    public void goToBigProducion(Player player, FamilyMemberColor familyMemberColor) {
    }

    public void goToCouncilPalace(Player player, FamilyMemberColor familyMemberColor) {
    }

    public void goToMarket(Player player, FamilyMemberColor familyMemberColor, ActionSpace marketActionSpace) {
    }

    /**
     * Adds the player to the occupants of an action space
     *
     * @param player
     * @param familyMemberColor
     * @param actionSpace
     */
    public void placeFamilyMember(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace) {
    }

    public void chooseCouncilPrivileges(Player player, ArrayList<CouncilPrivilege> councilPrivileges) {
    }

    public void playLeaderCard(Player player, LeaderCard leaderCard) {
    }

    public void discardLeaderCard(Player player, LeaderCard leaderCard) {
    }

    public <T extends OncePerTurnEffectInterface> void activateOncePerTurnEffect(Player player, T effect) {
    }


    /**
     * Performs the action and returns the set of resources obtained
     *
     * @param player            the player performing the action
     * @param familyMemberValue the family member value used for performing the action
     * @returns an ArrayList representing the possible choices of resources that can be obtained from performing the action
     */
    ArrayList<ObtainedResourceSet> performAction(Player player, int familyMemberValue, Action action) throws CantPerformActionException {
        throw new NotImplementedException();
    }

}