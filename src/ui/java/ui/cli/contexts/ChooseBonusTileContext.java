package ui.cli.contexts;

import client.exceptions.NetworkException;
import model.player.PersonalBonusTile;
import ui.cli.InvalidCommandException;

import java.rmi.RemoteException;
import java.util.List;

public class ChooseBonusTileContext extends Context {
    private List<PersonalBonusTile> personalBonusTiles;

    private Callback callback;

    public ChooseBonusTileContext(Callback callback, List<PersonalBonusTile> personalBonusTiles) {
        this.callback = callback;
        this.personalBonusTiles = personalBonusTiles;
        this.addCommand("show", this::showBonusTiles, "Show available bonus tiles");
        this.addCommand("choose", this::chooseBonusTile, "Choose a bonus tile");
        System.out.println("Choose a personal bonus tile");
        this.printBonusTiles();
    }

    private void showBonusTiles(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException();
        printBonusTiles();
    }

    private void printBonusTiles() {
        for(int i=1; i<=personalBonusTiles.size(); i++){
            System.out.println(i + ") " + personalBonusTiles.get(i-1).toString());
        }
    }

    private void chooseBonusTile(String[] params) throws InvalidCommandException {
        if (params.length != 1) throw new InvalidCommandException();
        try {
            int chosenBonusTile = Integer.parseInt(params[0]);
            callback.chooseBonusTile(personalBonusTiles.get(chosenBonusTile-1));
        }
        catch (NumberFormatException e) {
            throw new InvalidCommandException();
        } catch (NetworkException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface Callback {
        void chooseBonusTile(PersonalBonusTile bonusTile) throws NetworkException, RemoteException;
    }
}
