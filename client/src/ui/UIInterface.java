package ui;

import gamecontroller.GameEventsInterface;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;
import model.resource.ObtainableResourceSet;

import java.util.List;

/**
 * This interface extends the GameEventInterface with more methods that are specific to the UI
 */
public interface UIInterface extends GameEventsInterface {

    void showWaitingMessage(String message);

    void showChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles);

    void showChooseLeaderCard(List<LeaderCard> leaderCards);

    void showMainTurnContext();

    void showChooseCouncilPrivileges(List<ObtainableResourceSet> allowedPrivileges, int councilPrivilegesAmount);

    void showDecideExcommunicationContext();

    void showFatalError(String errorMessage);

    void onNetworkError();

    // public void onChatMessage();

    void showLoginPrompt();
}