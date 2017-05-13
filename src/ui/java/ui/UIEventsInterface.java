package ui;

import it.polimi.ingsw.lorenzo.gamecontroller.GameEventsInterface;

/**
 * This interface extends the GameEventInterface with more events that are not strictly game-related.
 */
public interface UIEventsInterface extends GameEventsInterface {
    // public void onChatMessage();
    void onNetworkError();

}