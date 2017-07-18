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

    /**
     * Fallback values for width and height
     */
    private final int DEFAULT_WIDTH = 160;
    private final int DEFAULT_HEIGHT = 40;

    private KeyboardHandler keyboardHandler;

    private String ourUsername;

    public CommandLineUI(ClientController controller) {
        this.controller = controller;
    }

    public void start() {
        TerminalFactory.configure(TerminalFactory.AUTO);
        terminal = TerminalFactory.get();

        width = terminal.getWidth();
        height = terminal.getHeight();

        if(width < DEFAULT_WIDTH || height < DEFAULT_HEIGHT || !terminal.isSupported()) {
            println("Your terminal is too small or unsupported.");
            println(String.format("Defaulting to %dx%d size", DEFAULT_WIDTH, DEFAULT_HEIGHT));
            width = DEFAULT_WIDTH;
            height = DEFAULT_HEIGHT;
        }

        try {
            keyboardHandler = new KeyboardHandler(terminal);
        }
        catch (IOException e) {
            println("IOException while trying to open the terminal.");
        }
        changeContext(new NetworkSettingsContext(this, controller));

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
    public void printPrompt() {
        try {
            keyboardHandler.cr.redrawLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void changeContext(Context context) {
        this.currentContext = context;
        keyboardHandler.clearAutocompleters();
        keyboardHandler.addAutocompleter(context.getCompleter());
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
        showWaitingMessage(String.format("It's %s's turn\n", username));
    }

    @Override
    public void onPlayerSpendsServants(String username, int servants) throws RemoteException {
        if(!username.equals(ourUsername)) {
            showWaitingMessage(String.format("%s commits %d servant(s) for his next action", username, servants));
        }
    }

    @Override
    public void onPlayerOccupiesActionSpace(String username, UUID actionSpaceId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges) throws RemoteException {
        // TODO: show more informative message
        if(!username.equals(ourUsername)) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%s occupies an action space with his %s family member", username, familyMemberColor));

            showWaitingMessage(sb.toString());
        }
    }

    @Override
    public void onPlayerOccupiesFloor(String username, UUID floorId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges, RequiredResourceSet paymentForCard) throws RemoteException {
        // TODO: show more informative message
        if(!username.equals(ourUsername)) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%s occupies a floor with his %s family member", username, familyMemberColor));

            showWaitingMessage(sb.toString());
        }
    }

    @Override
    public void onPlayerTakesDevelopmentCard(String username, UUID cardId, List<ObtainableResourceSet> councilPrivileges) throws RemoteException {
        // TODO: take useful parameters and print which card the player takes
    }

    @Override
    public void onStartVaticanReport() throws RemoteException {
        // Not interested in this event for now
    }

    @Override
    public void onPlayerDecidesExcommunication(String username, Boolean beExcommunicated) {
        if(!username.equals(ourUsername)) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%s decides to be ", username));
            if(beExcommunicated) sb.append("excommunicated");
            else sb.append("blessed");

            showWaitingMessage(sb.toString());
        }
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

    @Override
    public void onGameEnd() throws RemoteException {
        println("The game has ended!");
        // TODO: print the winner
    }

    /* -------------------------------------------------------------------------
     * UIInterface
     * These methods are called by the client controller.
     * They basically just switch context.
     * ------------------------------------------------------------------------- */

    @Override
    public void showLoginPrompt() {
        changeContext(new LoginContext(this, controller));
    }

    @Override
    public void showChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) {
        changeContext(new ChooseBonusTileContext(this, personalBonusTiles, controller));
    }

    @Override
    public void showChooseLeaderCard(List<LeaderCard> leaderCards) {
        changeContext(new ChooseLeaderCardContext(this, leaderCards, controller));
    }

    @Override
    public void showMainTurnContext() {
        changeContext(new MainTurnContext(this, controller.getGameController(), controller));
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
    public void showDecideExcommunicationContext() {
        List choices = new ArrayList<>();
        choices.add(new ChoosableItem<>("Be excommunicated but keep your faith points", true));
        choices.add(new ChoosableItem<>("Be blessed and give up your faith points", false));

        currentContext = new SingleChoiceContext<Boolean>(this,
                choices,
                controller::decideExcommunication
        );
    }

    @Override
    public void showWaitingMessage(String message) {
        changeContext(new WaitingContext(this, message));
    }

    @Override
    public void showFatalError(String errorMessage) {
        println("Error: " + errorMessage);
    }

    public void setOurUsername(String ourUsername) {
        this.ourUsername = ourUsername;
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
                try {
                    cr.println();
                    String input = cr.readLine();
                    currentContext.handleInput(input, false);
                }
                catch (IOException e) {
                    // If this happens we cannot read input from the user,
                    // there's no point in hiding the exception since
                    // it's probably an unrecoverable condition.
                    //
                    // This way the player can report the stack trace.
                    // This exception should never occur anyway.
                    e.printStackTrace();
                }
            }
        }
    }
}
