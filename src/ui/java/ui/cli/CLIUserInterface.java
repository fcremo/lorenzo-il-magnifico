package ui.cli;

import it.polimi.ingsw.lorenzo.client.ClientController;
import it.polimi.ingsw.lorenzo.client.exceptions.NetworkException;
import it.polimi.ingsw.lorenzo.model.action.Action;
import it.polimi.ingsw.lorenzo.model.board.actionspace.ActionSpace;
import it.polimi.ingsw.lorenzo.model.player.FamilyMemberColor;
import it.polimi.ingsw.lorenzo.model.player.Player;
import ui.UIEventsInterface;
import ui.cli.contexts.Context;
import ui.cli.contexts.NetworkSettingsContext;

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
     * @throws NetworkException
     */
    public void start() {
        controller = new ClientController(this);

        currentContext = new NetworkSettingsContext(controller);

        keyboardHandler = new KeyboardHandler();
        keyboardHandler.run();
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

    @Override
    public void onGameStart() {
        System.out.println("The game is started!");
    }

    public void onNetworkError() {
        System.out.println("Network error!");
        System.exit(1);
    }

    public static String askForString(String prompt) {
        System.out.print(prompt + " ");
        return new Scanner(System.in).nextLine();
    }

    private class KeyboardHandler implements Runnable {
        @Override
        public void run() {
            while(true){
                String input = askForString("");
                currentContext.handleInput(input);
            }
        }
    }
}
