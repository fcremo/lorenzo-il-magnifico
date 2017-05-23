package ui.cli;

import client.ClientController;
import client.exceptions.NetworkException;
import model.Game;
import model.action.Action;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.card.effects.interfaces.OncePerTurnEffectInterface;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.Player;
import model.resource.ObtainedResourceSet;
import ui.UIEventsInterface;
import ui.cli.contexts.Context;
import ui.cli.contexts.LoginContext;
import ui.cli.contexts.NetworkSettingsContext;
import ui.cli.contexts.WaitingForGameToStartContext;

import java.util.Scanner;

/**
 * This is the command line implementation of the user interface.
 */
public class CLIUserInterface implements UIEventsInterface {
    private ClientController controller;

    private Context currentContext;

    private Runnable keyboardHandler;

    public static String askForString(String prompt) {
        System.out.print(prompt + " ");
        return new Scanner(System.in).nextLine();
    }

    /**
     * This is the entry point for the user interface
     *
     * @throws NetworkException
     */
    public void start() {
        controller = new ClientController(this);

        currentContext = new NetworkSettingsContext(controller);

        keyboardHandler = new KeyboardHandler();
        keyboardHandler.run();
    }

    @Override
    public void showLoginPrompt() {
        currentContext = new LoginContext(controller);
    }

    @Override
    public void showWaitingForGameToStart() {
        currentContext = new WaitingForGameToStartContext();
    }

    @Override
    public void onPlayerTurnStart(Player player) {

    }

    @Override
    public void onPlayerOccupiesActionSpace(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace) {

    }

    @Override
    public void onPlayerPerformsAction(Player player, Action action) {

    }

    public void onNetworkError() {
        System.out.println("Network error!");
        System.exit(1);
    }

    @Override
    public void onGameStart(Game g) {

    }

    @Override
    public void onDicesThrown(int black, int white, int orange) {

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

    private class KeyboardHandler implements Runnable {
        @Override
        public void run() {
            while (true) {
                String input = askForString(">");
                currentContext.handleInput(input);
            }
        }
    }
}
