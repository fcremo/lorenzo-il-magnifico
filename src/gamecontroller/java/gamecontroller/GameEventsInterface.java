package gamecontroller;

import model.Game;
import model.action.Action;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.card.effects.interfaces.OncePerTurnEffectInterface;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.Player;
import model.resource.ObtainedResourceSet;

/**
 * This interface specifies all the game-related events (e.g. Player X occupies a certain action space)
 */
public interface GameEventsInterface {
    /**
     * Called when the game is started with the game configuration
     */
    void onGameStart(Game g);

    void onDicesThrown(int black, int white, int orange);

    /**
     * Called when a player's turn starts
     *
     * @param player
     */
    void onPlayerTurnStart(Player player);

    /**
     * Called when a player skips a turn
     *
     * @param player
     */
    void playerSkipsTurn(Player player);

    /**
     * Called when a player commits servants to perform an action
     *
     * @param player
     * @param servants
     */
    void onPlayerSpentServants(Player player, int servants);


    /**
     * Called when a player goes to a floor
     *
     * @param player
     * @param familyMemberColor
     * @param floor
     */
    public void onPlayerGoesToFloor(Player player, FamilyMemberColor familyMemberColor, Floor floor);

    /**
     * Called when a player goes to the small harvest action space
     *
     * @param player
     * @param familyMemberColor
     */
    public void goToSmallHarvest(Player player, FamilyMemberColor familyMemberColor);

    /**
     * Called when a player goes to the big harvest action space
     *
     * @param player
     * @param familyMemberColor
     */
    public void goToBigHarvest(Player player, FamilyMemberColor familyMemberColor);

    /**
     * Called when a player goes to the small production action space
     *
     * @param player
     * @param familyMemberColor
     */
    public void goToSmallProduction(Player player, FamilyMemberColor familyMemberColor);

    /**
     * Called when a player goes to the big production action space
     *
     * @param player
     * @param familyMemberColor
     */
    public void goToBigProduction(Player player, FamilyMemberColor familyMemberColor);

    /**
     * Called when a player goes to the council palace action space
     *
     * @param player
     * @param familyMemberColor
     */
    public void goToCouncilPalace(Player player, FamilyMemberColor familyMemberColor);

    /**
     * Called when a player goes to a market action space
     *
     * @param player
     * @param familyMemberColor
     * @param marketActionSpace
     */
    public void goToMarket(Player player, FamilyMemberColor familyMemberColor, ActionSpace marketActionSpace);

    /**
     * Called when a player places a family member in any action space
     *
     * @param player
     * @param familyMemberColor
     * @param actionSpace
     */
    void onPlayerPlacesFamilyMember(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace);

    /**
     * Called when a player plays a leader card
     *
     * @param player
     * @param leaderCard
     */
    void onPlayerPlayedLeaderCard(Player player, LeaderCard leaderCard);

    /**
     * Called when a player discards a leader card
     *
     * @param player
     * @param leaderCard
     */
    void onPlayerDiscardsLeaderCard(Player player, LeaderCard leaderCard);

    /**
     * Called when a player activates an effect
     *
     * @param player
     * @param effect
     * @param <T>
     */
    <T extends OncePerTurnEffectInterface> void onPlayerActivatesOncePerTurnEffect(Player player, T effect);


    /**
     * Called when a player earns resources
     *
     * @param player
     * @param obtainedResourceSet
     */
    void onPlayerGetsResources(Player player, ObtainedResourceSet obtainedResourceSet);

    /**
     * Called when a player occupies an action space
     *
     * @param player
     * @param familyMemberColor
     * @param actionSpace
     */
    void onPlayerOccupiesActionSpace(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace);

    /**
     * Called when a player performs an action
     *
     * @param player
     * @param action
     */
    void onPlayerPerformsAction(Player player, Action action);


}
