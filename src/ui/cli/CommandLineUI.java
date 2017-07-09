package ui.cli;

import client.ClientController;
import jline.Terminal;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.CandidateListCompletionHandler;
import jline.console.completer.Completer;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.PersonalBonusTile;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import ui.UIInterface;
import ui.cli.contexts.*;
import ui.cli.layout.LayoutInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This is the command line implementation of the user interface.
 */
public class CommandLineUI implements UIInterface, UIContextInterface {
    private ClientController controller;

    private Context currentContext;

    /**
     * The terminal we're attached to
     */
    private Terminal terminal;

    /**
     * Terminal width
     */
    private int width;

    /**
     * Terminal height
     */
    private int height;

    private KeyboardHandler keyboardHandler;

    public CommandLineUI(ClientController controller) {
        this.controller = controller;
    }

    public void start() {
        terminal = TerminalFactory.get();

        width = terminal.getWidth();
        height = terminal.getHeight();

        if(width < 140 || height < 50 || !terminal.isSupported()) {
            println("Your terminal is too small or unsupported.");
            println("Defaulting to 140x50 size");
            width = 140;
            height = 50;
        }

        currentContext = new NetworkSettingsContext(this, controller);

        try {
            keyboardHandler = new KeyboardHandler(terminal);
        }
        catch (IOException e) {
            println("IOException while trying to open the terminal.");
        }
        keyboardHandler.run();
    }

    /* -------------------------------------------------------------------------
     * UIContextInterface
     * These methods are used by the context to
     * print to the screen and change context
     * ------------------------------------------------------------------------- */

    /**
     * Prints a row on the screen. Does not reprint the prompt!
     * @param line the line to print
     */
    @Override
    public void println(String line) {
        println(line, false);
    }

    /**
     * Prints a line on the screen
     *
     * Pass false to reprintPrompt to display multiple lines
     *
     * @param line the line to print
     * @param reprintPrompt if true the prompt is reprinted
     */
    @Override
    @SuppressWarnings("squid:S106") // Suppress warning, I want to use System.out.println
    public void println(String line, boolean reprintPrompt) {
        System.out.println(line);
        if(reprintPrompt) {
            printPrompt();
        }
    }

    @Override
    public void printLayout(LayoutInterface layout) {
        println(layout.render(width, height));
    }

    @Override
    @SuppressWarnings("squid:S106") // Suppress warning, I want to use System.out.println
    public void printPrompt() {
        System.out.print("> ");
    }

    @Override
    public void changeContext(Context context) {
        this.currentContext = context;
    }

    /* -------------------------------------------------------------------------
     * Game events interface
     * These methods get called when a player does something
     * so the UI can report the events to the user.
     * ------------------------------------------------------------------------- */

    @Override
    public void onNetworkError() {
        println("Network error! Exiting..");
        System.exit(1);
    }

    @Override
    public void onPlayerTurnStarted(String username) throws RemoteException {
        showWaitingMessage(String.format("It's %s's turn", username));
    }

    @Override
    public void onPlayerSpendsServants(String username, int servants) throws RemoteException {
        // Not interested in this event for now
    }

    @Override
    public void onPlayerOccupiesActionSpace(String username, UUID actionSpaceId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges) throws RemoteException {
        // TODO
    }

    @Override
    public void onPlayerOccupiesFloor(String username, UUID floorId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges, RequiredResourceSet paymentForCard) throws RemoteException {
        // TODO
    }

    @Override
    public void onPlayerTakesDevelopmentCard(String username, UUID cardId, List<ObtainableResourceSet> councilPrivileges) throws RemoteException {
        // TODO
    }

    @Override
    public void onCardsDrawn(List<UUID> territoryCards, List<UUID> characterCards, List<UUID> buildingCards, List<UUID> ventureCards) throws RemoteException {
        // We're not interested in this event for now
    }

    @Override
    public void onDiceThrown(int blackDie, int whiteDie, int orangeDie) throws RemoteException {
        // We're not interested in this event for now
    }

    @Override
    public void onPrepareNewRound() throws RemoteException {
        // We're not interested in this event for now
    }

    /* -------------------------------------------------------------------------
     * UIInterface
     * These methods are called by the client controller.
     * They basically just switch context.
     * ------------------------------------------------------------------------- */

    @Override
    public void showLoginPrompt() {
        currentContext = new LoginContext(this, controller);
    }

    @Override
    public void showChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) {
        currentContext = new ChooseBonusTileContext(this, personalBonusTiles, controller);
    }

    @Override
    public void showChooseLeaderCard(List<LeaderCard> leaderCards) {
        currentContext = new ChooseLeaderCardContext(this, leaderCards, controller);
    }

    @Override
    public void showMainTurnContext() {
        currentContext = new MainTurnContext(this, controller.getGameController(), controller);
    }

    @Override
    public void showChooseCouncilPrivileges(List<ObtainableResourceSet> allowedCouncilPrivileges, int councilPrivilegesAmount) {
        if(councilPrivilegesAmount == 1) {
            currentContext = new SingleChoiceContext<>(this,
                    allowedCouncilPrivileges,
                    choice -> {
                        ArrayList<ObtainableResourceSet> choices = new ArrayList<>();
                        choices.add(choice);
                        controller.chooseDevelopmentCardCouncilPrivileges(choices);
                    }
                );
        }
        else {
            currentContext = new MultipleChoiceContext<>(this,
                    allowedCouncilPrivileges,
                    councilPrivilegesAmount,
                    councilPrivilegesAmount,
                    choices -> controller.chooseDevelopmentCardCouncilPrivileges(choices),
                    true);
        }
    }

    @Override
    public void showWaitingMessage(String message) {
        currentContext = new WaitingContext(this, message);
    }

    @Override
    public void showAbortGame(String errorMessage) {
        println("The server is aborting the game!");
        println("Error: " + errorMessage);
    }

    private class KeyboardHandler implements Runnable {
        ConsoleReader cr;

        public KeyboardHandler(Terminal terminal) throws IOException {
            cr = new ConsoleReader(System.in, System.out, terminal);
            cr.setHistoryEnabled(true);
            cr.setPrompt("> ");
            cr.setCompletionHandler(new CandidateListCompletionHandler());
        }

        public void clearAutocompleters() {
            for(Completer c : cr.getCompleters()){
                cr.removeCompleter(c);
            }
        }

        public void addAutocompleter(Completer completer) {
            cr.addCompleter(completer);
        }

        @Override
        @SuppressWarnings("squid:S2189") // Disable infinite loop warning
        public void run() {
            while (true) {
                // String input = askForString();
                try {
                    String input = cr.readLine();
                    currentContext.handleInput(input, false);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
