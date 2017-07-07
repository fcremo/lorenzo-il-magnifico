package ui.cli.contexts;

import client.exceptions.NetworkException;
import server.exceptions.ActionNotAllowedException;
import server.exceptions.PersonalBonusTileNotAvailableException;
import model.player.PersonalBonusTile;
import ui.cli.exceptions.InvalidCommandException;

import java.rmi.RemoteException;
import java.util.List;

public class ChooseBonusTileContext extends Context {
    private List<PersonalBonusTile> personalBonusTiles;

    private Callback callback;

    public ChooseBonusTileContext(UIContextInterface uiContextInterface, List<PersonalBonusTile> personalBonusTiles, Callback callback) {
        super(uiContextInterface);
        this.personalBonusTiles = personalBonusTiles;
        this.callback = callback;
        this.addCommand("show", this::showBonusTiles, "Show available bonus tiles");
        this.addCommand("choose", this::chooseBonusTile, "Choose a bonus tile");
        this.uiContextInterface.println("Choose a personal bonus tile");
        this.printBonusTiles();
    }

    private void showBonusTiles(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException("This command takes no arguments");
        printBonusTiles();
    }

    private void printBonusTiles() {
        for (int i = 1; i <= personalBonusTiles.size(); i++) {
            uiContextInterface.println(i + ") " + personalBonusTiles.get(i - 1).toString());
        }
        uiContextInterface.printPrompt();
    }

    private void chooseBonusTile(String[] params) throws InvalidCommandException, RemoteException, PersonalBonusTileNotAvailableException, NetworkException, ActionNotAllowedException {
        if (params.length != 1) throw new InvalidCommandException("You have to choose a bonus tile!");
        try {
            int chosenBonusTileIndex = Integer.parseInt(params[0]);
            if (chosenBonusTileIndex < 1 || chosenBonusTileIndex > personalBonusTiles.size()) {
                throw new InvalidCommandException("You chose an invalid bonus tile");
            }
            callback.chooseBonusTile(personalBonusTiles.get(chosenBonusTileIndex - 1));
        }
        catch (NumberFormatException e) {
            throw new InvalidCommandException("Input a valid number please");
        }
    }

    @FunctionalInterface
    public interface Callback {
        void chooseBonusTile(PersonalBonusTile bonusTile) throws NetworkException, RemoteException, PersonalBonusTileNotAvailableException, ActionNotAllowedException;
    }
}
