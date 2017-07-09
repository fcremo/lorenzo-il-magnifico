package ui.cli.contexts;

import client.exceptions.NetworkException;
import gamecontroller.exceptions.ActionNotAllowedException;
import server.exceptions.LeaderCardNotAvailableException;
import model.card.leader.LeaderCard;
import ui.cli.exceptions.InvalidCommandException;

import java.rmi.RemoteException;
import java.util.List;

public class ChooseLeaderCardContext extends Context {
    private List<LeaderCard> leaderCards;

    private Callback callback;

    public ChooseLeaderCardContext(UIContextInterface uiContextInterface, List<LeaderCard> leaderCards, Callback callback) {
        super(uiContextInterface);
        this.leaderCards = leaderCards;
        this.callback = callback;
        this.addCommand("show", this::showLeaderCards, "Show available leader cards");
        this.addCommand("choose", this::chooseLeaderCard, "Choose a leader card");
        uiContextInterface.println("Choose a leader card");
        this.printLeaderCards();
    }

    private void showLeaderCards(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException("This command takes no arguments");
        printLeaderCards();
    }

    private void printLeaderCards() {
        for (int i = 1; i <= leaderCards.size(); i++) {
            uiContextInterface.println(i + ") " + leaderCards.get(i - 1).toString());
        }
        uiContextInterface.printPrompt();
    }

    private void chooseLeaderCard(String[] params) throws InvalidCommandException, NetworkException, ActionNotAllowedException, RemoteException {
        if (params.length != 1) throw new InvalidCommandException("You have to choose a leader card!");
        try {
            int chosenLeaderCard = Integer.parseInt(params[0]);
            if (chosenLeaderCard < 1 || chosenLeaderCard > leaderCards.size()) {
                throw new InvalidCommandException("You chose an invalid leader card");
            }
            callback.chooseLeaderCard(leaderCards.get(chosenLeaderCard - 1));
        }
        catch (NumberFormatException e) {
            throw new InvalidCommandException("Input a valid number please");
        }
    }

    @FunctionalInterface
    public interface Callback {
        void chooseLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, LeaderCardNotAvailableException, ActionNotAllowedException;
    }
}
