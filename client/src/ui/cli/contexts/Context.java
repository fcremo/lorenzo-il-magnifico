package ui.cli.contexts;

import gamecontroller.exceptions.ActionNotAllowedException;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import ui.cli.exceptions.InvalidCommandException;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Base class for a UI context
 */
public abstract class Context {
    protected UIContextInterface uiContextInterface;

    private HashMap<String, Command> commands = new HashMap<>();
    private HashMap<String, String> helps = new HashMap<>();

    private Context previousContext;

    public Context(UIContextInterface uiContextInterface) {
        this.uiContextInterface = uiContextInterface;
        addCommand("help", this::help, "[command] Get help");
    }

    public Context(UIContextInterface uiContextInterface, Context previousContext) {
        this(uiContextInterface);
        this.previousContext = previousContext;
        addCommand("back", args -> goBack(), "Go back");
    }

    public void addCommand(String commandName, Command command, String helpString) {
        this.commands.put(commandName, command);
        this.helps.put(commandName, helpString);
    }

    @SuppressWarnings("squid:S1166") // Suppress "rethrow this exception" warnings
    public void handleInput(String input, boolean reprintPrompt) {
        // Ignore empty strings
        if (input.trim().equals("")) return;

        String[] splitInput = input.split(" ");
        String command = splitInput[0];
        String[] arguments = Arrays.copyOfRange(input.split(" "), 1, splitInput.length);

        if (commands.containsKey(command)) {
            try {
                commands.get(command).execute(arguments);
            }
            catch (InvalidCommandException e) {
                uiContextInterface.println("Invalid command (" + e.getMessage() + ")");
                uiContextInterface.println("Try \"help\"");
            }
            catch (ActionNotAllowedException e) {
                uiContextInterface.println("The server said you did something illegal:");
                uiContextInterface.println(e.getMessage());
            }
        }
        else {
            uiContextInterface.println("This command does not exist. Try \"help\"");
        }

        if (reprintPrompt) uiContextInterface.printPrompt();
    }

    private void help(String[] params) throws InvalidCommandException {
        if (params.length > 1) throw new InvalidCommandException("Command \"help\" takes 0 or 1 arguments");

        if (params.length == 1) {
            if (helps.containsKey(params[0])) {
                uiContextInterface.println(helps.get(params[0]));
            }
            else {
                throw new InvalidCommandException(String.format("Command %s does not exist", params[0]));
            }
        }
        else if (params.length == 0) {
            for (String command : helps.keySet()) {
                uiContextInterface.println(" " + command + " \t" + helps.get(command));
            }
        }
    }

    /**
     * Sets the previous context
     *
     * @param previousContext
     */
    public void setPreviousContext(Context previousContext) {
        this.previousContext = previousContext;
        if (!commands.containsKey("back")) {
            addCommand("back", s -> goBack(), "Go back");
        }
    }

    public Completer getCompleter() {
        return new StringsCompleter(commands.keySet());
    }

    protected void goBack() {
        if (previousContext != null) uiContextInterface.changeContext(previousContext);
    }

    public void printHelp(boolean reprintPrompt) {
        handleInput("help", reprintPrompt);
    }
}
