package gamecontroller;

import gamecontroller.exceptions.PersonalBonusTileNotAvailableException;
import model.Game;
import model.action.Action;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.card.effects.interfaces.OncePerRoundEffectInterface;
import model.card.leader.LeaderCard;
import model.exceptions.CantPerformActionException;
import model.player.FamilyMemberColor;
import model.player.PersonalBonusTile;
import model.player.Player;
import model.resource.ObtainedResourceSet;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;

/**
 * This class implements the game logic and is responsible for handling player actions (and raise exceptions),
 * updating the game state and making callbacks to report the changes.
 * The code is shared by the client and the server so that both use the exact same logic for updating the state of the game.
 */
public class GameController {
    /**
     * The game being played
     */
    private Game game;

    /**
     * The callback interface for game events
     */
    private GameEventsInterface callback;

    /**
     * The state of the game
     */
    private GameState gameState;

    public GameController(GameEventsInterface callback) {
        this.callback = callback;
        this.gameState = GameState.INITIALIZING;
    }

    public boolean isGameStarting() {
        return gameState == GameState.INITIALIZING;
    }

    public void setPersonalBonusTile(Player player, PersonalBonusTile personalBonusTile) throws PersonalBonusTileNotAvailableException {
        player.setBonusTile(personalBonusTile);
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

    public void chooseCouncilPrivileges(Player player, ArrayList<ObtainedResourceSet> councilPrivileges) {
    }

    public void playLeaderCard(Player player, LeaderCard leaderCard) {
    }

    public void discardLeaderCard(Player player, LeaderCard leaderCard) {
    }

    public <T extends OncePerRoundEffectInterface> void activateOncePerRoundEffect(Player player, T effect) {
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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void addPlayer(Player player) {
        game.addPlayer(player);
    }
}
