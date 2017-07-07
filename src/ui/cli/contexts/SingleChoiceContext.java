package ui.cli.contexts;

import client.exceptions.NetworkException;
import server.exceptions.ActionNotAllowedException;
import ui.cli.exceptions.InvalidChoiceException;
import ui.cli.exceptions.InvalidCommandException;

import java.rmi.RemoteException;
import java.util.List;

public class SingleChoiceContext<T> extends Context {
    private List<T> allowedChoices;

    private Callback callback = null;

    public SingleChoiceContext(UIContextInterface uiContextInterface, List<T> allowedChoices, Callback callback) {
        super(uiContextInterface);
        this.allowedChoices = allowedChoices;
        this.callback = callback;
        this.addCommand("show", this::show, "Show possible choices");
        this.addCommand("choose", this::choose, "[index] Perform choice");
        this.printAvailableChoices();
    }

    private void show(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException("This command takes no arguments");
        printAvailableChoices();
    }

    private void printAvailableChoices() {
        uiContextInterface.println("Choose between:");
        for (int i = 1; i <= allowedChoices.size(); i++) {
            uiContextInterface.println(i + ") " + allowedChoices.get(i - 1).toString());
        }

        uiContextInterface.printPrompt();
    }

    private void choose(String[] params) throws InvalidCommandException, NetworkException, ActionNotAllowedException, RemoteException {
        if (params.length != 1) throw new InvalidCommandException("You have to specify what you want to choose!");
        try {
            int chosenIndex = Integer.parseInt(params[0]);
            if (chosenIndex < 1 || chosenIndex > allowedChoices.size()) {
                throw new InvalidCommandException(String.format("Choose between %d and %d", 1, allowedChoices.size()));
            }

            T choice = allowedChoices.get(chosenIndex - 1);

            callback.choose(choice);
        }
        catch (NumberFormatException e) {
            throw new InvalidCommandException("Input a valid number please");
        }
        catch (InvalidChoiceException e) {
            throw new InvalidCommandException("You performed an invalid choice");
        }
    }

    @FunctionalInterface
    public interface Callback {
        <T> void choose(T choice) throws NetworkException, RemoteException, InvalidChoiceException;
    }
}
