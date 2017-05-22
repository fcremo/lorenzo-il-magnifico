package server;

import gamecontroller.GameController;
import gamecontroller.GameEventsInterface;
import model.Game;
import model.action.Action;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.card.effects.interfaces.OncePerTurnEffectInterface;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.Player;
import model.resource.ObtainedResourceSet;

import java.util.ArrayList;

/**
 * This class represents a game room
 */
public class GameRoom implements GameEventsInterface {
    /**
     * The list of the server controllers in this room, one per player
     */
    private ArrayList<ServerController> controllers = new ArrayList<>();

    private GameController gameController = new GameController(new Game(), this);

    /**
     * The name of the room
     */
    private String name;

    public GameRoom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addController(ServerController controller) {
        controllers.add(controller);
    }

    public void removeController(ServerController controller) {
        controllers.remove(controller);
    }

    public ArrayList<ServerController> getControllers(){
        return controllers;
    }

    @Override
    public void onGameStart(Game g) {

    }

    @Override
    public void onDicesThrown(int black, int white, int orange) {

    }

    @Override
    public void onPlayerTurnStart(Player player) {

    }

    @Override
    public void playerSkipsTurn(Player player) {

    }

    @Override
    public void onPlayerSpentServants(Player player, int servants) {

    }

    @Override
    public void onPlayerGoesToFloor(Player player, FamilyMemberColor familyMemberColor, Floor floor) {

    }

    @Override
    public void goToSmallHarvest(Player player, FamilyMemberColor familyMemberColor) {

    }

    @Override
    public void goToBigHarvest(Player player, FamilyMemberColor familyMemberColor) {

    }

    @Override
    public void goToSmallProducion(Player player, FamilyMemberColor familyMemberColor) {

    }

    @Override
    public void goToBigProducion(Player player, FamilyMemberColor familyMemberColor) {

    }

    @Override
    public void goToCouncilPalace(Player player, FamilyMemberColor familyMemberColor) {

    }

    @Override
    public void goToMarket(Player player, FamilyMemberColor familyMemberColor, ActionSpace marketActionSpace) {

    }

    @Override
    public void onPlayerPlacesFamilyMember(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace) {

    }

    @Override
    public void onPlayerPlayedLeaderCard(Player player, LeaderCard leaderCard) {

    }

    @Override
    public void onPlayerDiscardsLeaderCard(Player player, LeaderCard leaderCard) {

    }

    @Override
    public <T extends OncePerTurnEffectInterface> void onPlayerActivatesOncePerTurnEffect(Player player, T effect) {

    }

    @Override
    public void onPlayerGetsResources(Player player, ObtainedResourceSet obtainedResourceSet) {

    }

    @Override
    public void onPlayerOccupiesActionSpace(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace) {

    }

    @Override
    public void onPlayerPerformsAction(Player player, Action action) {

    }
}
