package ui.cli;

import client.ClientController;
import gamecontroller.GameState;
import jline.Terminal;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.CandidateListCompletionHandler;
import jline.console.completer.Completer;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;
import model.player.Player;
import ui.UIInterface;
import ui.cli.contexts.*;
import ui.cli.layout.LayoutInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

/**
 * This is the command line implementation of the user interface.
 */
public class CLIUserInterface implements UIInterface, PrintInterface {
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

    public CLIUserInterface(ClientController controller) {
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
    public void onNetworkError() {
        println("Network error!");
        System.exit(1);
    }

    @Override
    public void onGameStateChange(GameState gameState) {
        println("Game state changed to " + gameState.name(), true);
    }

    @Override
    public void onTurnOrderChanged(List<Player> playerOrder) throws RemoteException {
        StringBuilder s = new StringBuilder("Player order is now ");
        playerOrder.stream().forEach(player -> s.append(player.getUsername()).append(", "));
        s.setLength(s.length() - 2); // Remove last ", "
        println(s.toString(), true);
    }

    @Override
    public void onPlayerTurnStarted(Player player) throws RemoteException {
        println(String.format("It's %s's turn", player.getUsername()), true);
    }

    @Override
    public void showMainTurnContext() {
        currentContext = new MainTurnContext(this, controller.getGame(), controller);
    }

    @Override
    public void onCardsDrawn(List<TerritoryCard> territoryCards, List<CharacterCard> characterCards, List<BuildingCard> buildingCards, List<VentureCard> ventureCards) throws RemoteException {

    }

    @Override
    public void onDiceThrown(int blackDie, int whiteDie, int orangeDie) throws RemoteException {

    }

    @Override
    public void showLoginPrompt() {
        currentContext = new LoginContext(this, controller);
    }

    @Override
    public void showWaitingMessage(String message) {
        currentContext = new WaitingContext(this, message);
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
    public void showAbortGame(String errorMessage) {
        println("The server is aborting the game!");
        println("Error: " + errorMessage);
    }

    private static String askForString() {
        return new Scanner(System.in).nextLine();
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
