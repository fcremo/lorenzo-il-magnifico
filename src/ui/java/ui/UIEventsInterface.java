package ui;

import gamecontroller.GameEventsInterface;
import model.player.PersonalBonusTile;

import java.util.List;

/**
 * This interface extends the GameEventInterface with more events that are not strictly game-related.
 */
public interface UIEventsInterface extends GameEventsInterface {
    // public void onChatMessage();

    void showLoginPrompt();

    void showWaitingForGameToStart();

    void showChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles);

    void onNetworkError();

}