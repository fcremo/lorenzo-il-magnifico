package ui.cli.contexts;

import jdk.nashorn.internal.codegen.CompilerConstants;
import jdk.nashorn.internal.objects.annotations.Function;
import ui.cli.InvalidCommandException;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Base class for a UI context
 */
public class Context {
    HashMap<String, Command> commands = new HashMap<>();
    HashMap<String, String> helps = new HashMap<>();

    public Context(){
        commands.put("help", new HelpCommand());
    }

    public void addCommand(String commandName, Command command, String helpString){
        this.commands.put(commandName, command);
        this.helps.put(commandName, helpString);
    }

    public void handleInput(String input) {
        // Ignore empty strings
        if(input.trim().equals("")) return;

        String[] splitInput = input.split(" ");
        String command = splitInput[0];
        String[] arguments = Arrays.copyOfRange(input.split(" "), 1, splitInput.length);

        if(commands.containsKey(command)){
            try {
                commands.get(command).execute(arguments);
            } catch (InvalidCommandException e) {
                System.out.println("Invalid command. Try \"help\".");
            }
        }
        else {
            System.out.println("This command does not exist. Try \"help\"");
        }
    }

    private class HelpCommand implements Command {
        public void execute(String[] arguments) throws InvalidCommandException  {
            if(arguments.length == 1 && helps.containsKey(arguments[0])){
                System.out.println(helps.get(arguments[0]));
            }
            else if (arguments.length == 0) {
                for (String command : helps.keySet()) {
                    System.out.println(command + ": " + helps.get(command));
                }
            }
            else {
                throw new InvalidCommandException();
            }
        }
    }
}
