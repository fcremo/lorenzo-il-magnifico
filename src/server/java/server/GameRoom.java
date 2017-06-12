package server;

import gamecontroller.GameController;
import gamecontroller.GameEventsInterface;
import gamecontroller.ServerGameController;
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
import java.util.NoSuchElementException;

/**
 * This class represents a game room
 */
public class GameRoom implements GameEventsInterface {
    /**
     * The list of the server controllers in this room, one per player
     */
    private ArrayList<ServerController> controllers = new ArrayList<>();

    private ServerGameController serverGameController = new ServerGameController(this);

    /**
     * The name of the room
     */
    private String name;

    /**
     * The time to wait (in milliseconds) before starting the game after two players have joined the room.
     * TODO: 5/30/17 make this timeout configurable
     */
    private int gameStartTimeout = 5000;

    private Thread timer;

    public GameRoom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServerController getControllerForPlayer (Player player) {
        for (ServerController controller : controllers) {
            if (controller.getPlayer().getUsername().equals(player.getUsername()))
                return controller;
        }
        throw new NoSuchElementException();
    }

    public ServerGameController getServerGameController() {
        return serverGameController;
    }

    public void setServerGameController(ServerGameController serverGameController) {
        this.serverGameController = serverGameController;
    }

    /**
     * Adds a player to the room, starting the room timeout if
     * there are at least 2 players connected
     * @param controller
     */
    public void addPlayer(ServerController controller) {
        controllers.add(controller);
        if (controllers.size() >= 2 && timer == null) {
            System.out.println("Starting room " + name + " timeout");
            timer = new Thread(new RoomTimerClass());
            timer.start();
        }
    }

    public void removeController(ServerController controller) {
        controllers.remove(controller);
    }

    public ArrayList<ServerController> getControllers() {
        return controllers;
    }

    public boolean isAvailable() {
        return (serverGameController.isGameStarting() && !this.isFull());
    }

    public boolean isFull() {
        return (controllers.size() >= 4);
    }

    @Override
    public void onGameStart(Game g) {
        for (ServerController controller: controllers) {
            controller.

        }
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
    public void goToSmallProduction(Player player, FamilyMemberColor familyMemberColor) {

    }

    @Override
    public void goToBigProduction(Player player, FamilyMemberColor familyMemberColor) {

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

    private void onRoomTimeout() {
        System.out.println("Room " + name + " timeout expired, starting game...");
        serverGameController.setPlayers();
        serverGameController.startGame();
    }

    private class RoomTimerClass implements Runnable {
        @Override
        public void run() {
            // The time when the timer was started, needed to handle exceptions
            long startTime;
            long remainingTime = gameStartTimeout;

            while(true){
                startTime = System.currentTimeMillis();
                try {
                    Thread.sleep(remainingTime);
                    onRoomTimeout();
                    break;
                } catch (InterruptedException e) {
                    // If somehow the thread is woken up early compute the
                    // remaining time and go back to sleep again
                    remainingTime = remainingTime - (System.currentTimeMillis() - startTime);
                }
            }
        }
    }
}
