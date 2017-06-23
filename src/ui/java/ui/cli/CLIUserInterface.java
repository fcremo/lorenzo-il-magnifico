package ui.cli;

import client.ClientController;
import gamecontroller.GameState;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;
import model.player.Player;
import ui.UIEventsInterface;
import ui.cli.contexts.*;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

/**
 * This is the command line implementation of the user interface.
 */
public class CLIUserInterface implements UIEventsInterface {
    private ClientController controller;

    private Context currentContext;

    private Runnable keyboardHandler;

    private static String askForString(String prompt) {
        System.out.print(prompt + " ");
        return new Scanner(System.in).nextLine();
    }

    /**
     * This is the entry point for the user interface
     */
    public void start() {
        controller = new ClientController(this);

        currentContext = new NetworkSettingsContext(controller);

        keyboardHandler = new KeyboardHandler();
        keyboardHandler.run();
    }

    @Override
    public void onNetworkError() {
        System.out.println("Network error!");
        System.exit(1);
    }

    @Override
    public void onGameStateChange(GameState gameState) {
        System.out.println("Game state changed to " + gameState.name());
    }

    @Override
    public void onTurnOrderChanged(List<Player> playerOrder) throws RemoteException {
        StringBuilder s = new StringBuilder("Player order is now ");
        playerOrder.stream().forEach(player -> s.append(player.getUsername()).append(", "));
        s.setLength(s.length() - 2); // Remove last ", "
        System.out.println(s);
    }

    @Override
    public void onPlayerTurnStarted(Player player) throws RemoteException {
        System.out.println(String.format("It's %s turn", player.getUsername()));
    }

    @Override
    public void showLoginPrompt() {
        currentContext = new LoginContext(controller);
    }

    @Override
    public void showWaitingMessage(String message) {
        currentContext = new WaitingContext(message);
    }

    @Override
    public void showChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) {
        currentContext = new ChooseBonusTileContext(controller, personalBonusTiles);
    }

    @Override
    public void showChooseLeaderCard(List<LeaderCard> leaderCards) {
        currentContext = new ChooseLeaderCardContext(controller, leaderCards);
    }

    @Override
    public void showAbortGame(String errorMessage) {
        System.out.println("The server is aborting the game!");
        System.out.println("Error: " + errorMessage);
    }

    private class KeyboardHandler implements Runnable {
        @Override
        @SuppressWarnings("squid:S2189") // Disable infinite loop warning
        public void run() {
            while (true) {
                String input = askForString(">");
                currentContext.handleInput(input);
            }
        }
    }
}
