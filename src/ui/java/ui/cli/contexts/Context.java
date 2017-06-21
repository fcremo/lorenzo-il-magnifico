package ui.cli.contexts;

import client.exceptions.NetworkException;
import gamecontroller.exceptions.ActionNotAllowedException;
import ui.cli.exceptions.InvalidCommandException;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Base class for a UI context
 */
public abstract class Context {
    HashMap<String, Command> commands = new HashMap<>();
    HashMap<String, String> helps = new HashMap<>();

    public Context() {
        commands.put("help", new HelpCommand());
    }

    public void addCommand(String commandName, Command command, String helpString) {
        this.commands.put(commandName, command);
        this.helps.put(commandName, helpString);
    }

    @SuppressWarnings("squid:S1166") // Suppress "rethrow this exception" warnings
    public void handleInput(String input) {
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
                System.out.printf("Invalid command (%s).%n", e.getMessage());
                System.out.println("Try \"help\"");
            }
            catch (ActionNotAllowedException e) {
                System.out.println("The server said you did something illegal:");
                System.out.println(e.getMessage());
            }
            catch (RemoteException | NetworkException e) {
                e.printStackTrace();
                // TODO: decide how to handle network exceptions
            }
        }
        else {
            System.out.println("This command does not exist. Try \"help\"");
        }
    }

    public void printHelp() {
        handleInput("help");
    }

    private class HelpCommand implements Command {
        public void execute(String[] arguments) throws InvalidCommandException {
            if (arguments.length == 1) {
                if (helps.containsKey(arguments[0])) {
                    System.out.println(helps.get(arguments[0]));
                }
                else {
                    System.out.println(String.format("Command %s does not exist", arguments[0]));
                }
            }
            else if (arguments.length == 0) {
                for (String command : helps.keySet()) {
                    System.out.println("\t" + command + ": " + helps.get(command));
                }
            }
            else {
                throw new InvalidCommandException("Command \"help\" takes 0 or 1 argument");
            }
        }
    }
}
