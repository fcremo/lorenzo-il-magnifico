package ui.cli;

import client.ClientController;
import client.ConnectionMethod;
import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import gamecontroller.GameState;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;
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

    public void start(String username) {
        controller = new ClientController(this);

        try {
            controller.connect(ConnectionMethod.RMI, "localhost", 1099);
            controller.login(username);
        } catch (NetworkException | RemoteException | LoginException e) {
            e.printStackTrace();
        }

        keyboardHandler = new KeyboardHandler();
        keyboardHandler.run();
    }


    public void onNetworkError() {
        System.out.println("Network error!");
        System.exit(1);
    }

    @Override
    public void onGameStateChange(GameState gameState) {
        System.out.println("Game state changed to " + gameState.name());
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
        public void run() {
            // TODO: find a way to disable the infinite loop inspection in intellij (is it bugged?)
            while ((true)) {
                String input = askForString(">");
                currentContext.handleInput(input);
            }
        }
    }

    private static String askForString(String prompt) {
        System.out.print(prompt + " ");
        return new Scanner(System.in).nextLine();
    }
}
