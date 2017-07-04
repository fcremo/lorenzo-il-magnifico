package ui;

import gamecontroller.GameEventsInterface;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;

import java.util.List;

/**
 * This interface extends the GameEventInterface with more events that are specific to the UI
 */
public interface UIInterface extends GameEventsInterface {
    // public void onChatMessage();

    void showLoginPrompt();

    void showWaitingMessage(String message);

    void showChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles);

    void showChooseLeaderCard(List<LeaderCard> leaderCards);

    void onNetworkError();

    void showAbortGame(String errorMessage);

    void showMainTurnContext();

}