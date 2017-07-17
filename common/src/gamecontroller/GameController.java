package gamecontroller;

import gamecontroller.exceptions.ActionNotAllowedException;
import gamecontroller.exceptions.PlayerDoesNotExistException;
import model.Excommunication;
import model.Game;
import model.action.Action;
import model.action.ActionType;
import model.board.Board;
import model.board.actionspace.*;
import model.card.Card;
import model.card.development.*;
import model.card.effects.*;
import model.card.effects.interfaces.FamilyMemberValueSetterEffectInterface;
import model.card.effects.interfaces.OncePerRoundEffectInterface;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.PersonalBonusTile;
import model.player.Player;
import model.resource.ObtainableResource;
import model.resource.ObtainableResourceSet;
import model.resource.ObtainedResourceSet;
import model.resource.RequiredResourceSet;
import model.util.Tuple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static model.resource.ObtainableResource.*;

/**
 * This class implements the game logic and is responsible for handling player actions.
 * It raises exceptions if the actions are illegal, and updates the game state otherwise.
 *
 * The code is shared by the client and the server so that both use the exact same logic
 * for updating the state of the game.
 */
public class GameController {
    /**
     * The game being played
     */
    private Game game;

    /**
     * The state of the game
     */
    private GameState gameState;

    /**
     * True if the current player has not already placed his family member
     */
    private boolean hasCurrentPlayerPlacedFamilyMember;

    /**
     * The development card being taken when going to a floor
     * or performing an action
     */
    private DevelopmentCard developmentCardBeingTaken;

    /**
     * This list is used to keep track of whick players
     * still have to decide whether to be excommunicated or not
     */
    private List<Player> playersWithPendingExcommunicationDecision = new ArrayList<>();

    public GameController() {
        this.gameState = GameState.WAITING_FOR_PLAYERS_TO_CONNECT;
    }

    /**
     * Called when a player chooses a personal bonus tile
     *
     * @param username
     * @param personalBonusTileId
     * @throws ActionNotAllowedException
     */
    public void setPersonalBonusTile(String username, UUID personalBonusTileId) throws ActionNotAllowedException {
        assertGameState(GameState.DRAFTING_BONUS_TILES);

        // If the bonus tile is not available will throw an exception
        PersonalBonusTile personalBonusTile = getLocalAvailablePersonalBonusTile(personalBonusTileId);

        // Set the bonus tile
        Player player = getLocalPlayer(username);
        player.setBonusTile(personalBonusTile);

        // Remove the chosen bonus tile from the available ones
        getGame().getAvailablePersonalBonusTiles().remove(personalBonusTile);
    }

    /**
     * Does the preparation for starting a new round
     */
    public void prepareNewRound() {
        game.nextRound();

        game.getPlayers().forEach(Player::resetAvailableFamilyMembers);

        // Updated the turn order according to the occupations of the council palace
        updateTurnOrder();

        // Sets first player
        game.setCurrentPlayer(game.getPlayers().get(0));
    }

    /**
     * Sets the player as current and starts his turn
     *
     * @param username
     * @throws PlayerDoesNotExistException
     */
    public void startPlayerTurn(String username) throws PlayerDoesNotExistException {
        Player player = getLocalPlayer(username);
        player.setSpentServants(0);
        game.setCurrentPlayer(player);
        gameState = GameState.PLAYER_TURN;
        hasCurrentPlayerPlacedFamilyMember = false;
    }

    /**
     * Starts the vatican report and immediately excommunicates players that
     * don't have the required faith points
     *
     * @throws ActionNotAllowedException
     */
    public void startVaticanReport() throws ActionNotAllowedException {
        int currentRound = getGame().getCurrentRound();

        int faithPointsNeeded;
        if(currentRound == 2) faithPointsNeeded = 3;
        else if(currentRound == 4) faithPointsNeeded = 4;
        else if(currentRound == 6) faithPointsNeeded = 5;
        else throw new ActionNotAllowedException("The vatican report starts only after even rounds!");

        gameState = GameState.VATICAN_REPORT;

        Excommunication excommunication = game.getBoard().getExcommunications()[currentRound/2];

        // Assign excommunication to players that don't have the necessary faith points
        game.getPlayers()
            .stream()
            .filter(player -> !player.getResources().hasAtLeast(faithPointsNeeded, ObtainableResource.FAITH_POINTS))
            .forEach(player -> player.getExcommunications().add(excommunication));

        playersWithPendingExcommunicationDecision = new ArrayList<>();

        // Keep track of which players can choose if they want to be excommunicated
        game.getPlayers()
            .stream()
            .filter(player -> player.getResources().hasAtLeast(faithPointsNeeded, ObtainableResource.FAITH_POINTS))
            .forEach(playersWithPendingExcommunicationDecision::add);
    }

    /**
     * Called when a player decides whether to be excommunicated or not
     * @param username
     * @param beExcommunicated
     */
    public void decideExcommunication(String username, boolean beExcommunicated) throws ActionNotAllowedException {
        assertGameState(GameState.VATICAN_REPORT);
        Player player = getLocalPlayer(username);
        assertPlayerCanDecideHisExcommunication(player);

        if(beExcommunicated) {
            player.getResources().setResourceQty(ObtainableResource.FAITH_POINTS, 0);
        }
        else {
            int currentRound = game.getCurrentRound();
            Excommunication excommunication = game.getBoard().getExcommunications()[currentRound/2];
            player.getExcommunications().add(excommunication);
        }
        playersWithPendingExcommunicationDecision.remove(player);
    }

    /**
     * Changes the current player
     *
     * @param username
     */
    public void setCurrentPlayer(String username) throws PlayerDoesNotExistException {
        game.setCurrentPlayer(getLocalPlayer(username));
    }

    public void setDevelopmentCards(List<UUID> territoryCardsIds, List<UUID> characterCardsIds, List<UUID> buildingCardsIds, List<UUID> ventureCardsIds) throws ActionNotAllowedException {
        Board board = getGame().getBoard();

        List<TerritoryCard> territoryCards = getLocalAvailableTerritoryCards(territoryCardsIds);
        List<CharacterCard> characterCards = getLocalAvailableCharacterCards(characterCardsIds);
        List<BuildingCard> buildingCards = getLocalAvailableBuildingCards(buildingCardsIds);
        List<VentureCard> ventureCards = getLocalAvailableVentureCards(ventureCardsIds);

        board.getTerritoryTower().setCards(territoryCards);
        board.getCharacterTower().setCards(characterCards);
        board.getBuildingTower().setCards(buildingCards);
        board.getVentureTower().setCards(ventureCards);
    }

    /**
     * Called when a player commits servants
     *
     * @param username
     * @param servants
     * @throws ActionNotAllowedException
     */
    public void spendServants(String username, int servants) throws ActionNotAllowedException {
        if(servants < 0) throw new ActionNotAllowedException("Servants must be positive");
        Player player = getLocalPlayer(username);
        player.setSpentServants(servants);

        // TODO: check if the player has the servants he wants to commit.
        // Not urgent since the number of servants will be checked
        // when he will try to do an action
    }

    /**
     * Called when a player goes to an action space.
     *
     * @param username the username of the player
     * @param actionSpaceId the action space ID the player wants to occupy
     * @param familyMemberColor the family member the player wants to use
     * @param chosenPrivileges the council privileges chosen to occupy the action space (if it gives them as bonus)
     * @throws ActionNotAllowedException if the action is not allowed
     */
    public void goToActionSpace(String username, UUID actionSpaceId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges) throws ActionNotAllowedException {
        Player player = getLocalPlayer(username);
        ActionSpace actionSpace = getLocalActionSpace(actionSpaceId);
        assertPlayerTurn(player);
        assertGameState(GameState.PLAYER_TURN);
        assertPlayerHasNotPlacedFamilyMember();
        assertFamilyMemberAvailable(player, familyMemberColor);

        ObtainableResourceSet bonus = getEffectiveActionSpaceBonus(actionSpace, player);

        int allowedCouncilPrivileges = bonus.getObtainedAmount(ObtainableResource.COUNCIL_PRIVILEGES);
        assertValidCouncilPrivilegesChoice(chosenPrivileges, allowedCouncilPrivileges);

        if (actionSpace instanceof MarketActionSpace) {
            assertSmallActionSpaceOccupiableBy(actionSpace, player, familyMemberColor);
            assertFamilyMemberValueIsAtLeast(player, familyMemberColor, 1);
        }
        else if (actionSpace instanceof CouncilPalace) {
            assertBigActionSpaceOccupiableBy(actionSpace, player, familyMemberColor);
            assertFamilyMemberValueIsAtLeast(player, familyMemberColor, 1);
        }
        else if (actionSpace instanceof SmallProductionArea) {
            assertSmallActionSpaceOccupiableBy(actionSpace, player, familyMemberColor);
            assertEffectiveActionValueIsAtLeast(player, ActionType.PRODUCTION, familyMemberColor, 1);
            startProduction(player);
        }
        else if (actionSpace instanceof BigProductionArea) {
            assertBigActionSpaceOccupiableBy(actionSpace, player, familyMemberColor);
            assertEffectiveActionValueIsAtLeast(player, ActionType.HARVEST, familyMemberColor, 1);
            startProduction(player);
        }
        else if (actionSpace instanceof SmallHarvestArea) {
            assertSmallActionSpaceOccupiableBy(actionSpace, player, familyMemberColor);
            assertEffectiveActionValueIsAtLeast(player, ActionType.HARVEST, familyMemberColor, 1);
            startHarvest(player);
        }
        else if (actionSpace instanceof BigHarvestArea) {
            assertBigActionSpaceOccupiableBy(actionSpace, player, familyMemberColor);
            assertEffectiveActionValueIsAtLeast(player, ActionType.HARVEST, familyMemberColor, 1);
            startHarvest(player);
        }

        player.addResources(bonus);
        player.addResources(chosenPrivileges);

        actionSpace.addOccupant(player, familyMemberColor);
        hasCurrentPlayerPlacedFamilyMember = true;
    }

    /**
     * Called when a player goes to a floor
     *
     * @param username
     * @param familyMemberColor
     * @param floorId
     * @param paymentForCard
     * @throws ActionNotAllowedException
     */
    public void goToFloor(String username, FamilyMemberColor familyMemberColor, UUID floorId, RequiredResourceSet paymentForCard, List<ObtainableResourceSet> chosenPrivileges) throws ActionNotAllowedException {
        Player player = getLocalPlayer(username);
        Floor floor = getLocalFloor(floorId);
        assertPlayerTurn(player);
        assertGameState(GameState.PLAYER_TURN);
        assertPlayerHasNotPlacedFamilyMember();
        assertFamilyMemberAvailable(player, familyMemberColor);
        assertEffectiveActionValueIsAtLeast(player, floor.getCard().getCardTakingActionType(), familyMemberColor, floor.getRequiredFamilyMemberValue());
        assertFloorOccupiableBy(floor, player, familyMemberColor, paymentForCard, chosenPrivileges);

        // Spend servants and reset spent servants count
        player.getResources().subtractResource(ObtainableResource.SERVANTS, player.getSpentServants());
        player.setSpentServants(0);

        // Pay resources needed to occupy the floor
        if (player.getEffectsImplementing(DoubleOccupationCostIgnoreEffect.class).isEmpty()) {
            RequiredResourceSet cost = floor.getDoubleOccupationCost();
            player.getResources().subtractResources(cost);
        }

        // Add bonus resources to the player
        ObtainableResourceSet actionSpaceBonus = floor.getBonus();
        for (ObtainableResourceSet privilege : chosenPrivileges) {
            actionSpaceBonus.addResources(privilege);
        }

        // Apply action space bonus setter effect (Preacher)
        for (FloorBonusResourcesSetterEffect e : player.getEffectsImplementing(FloorBonusResourcesSetterEffect.class)) {
            actionSpaceBonus = e.setObtainedResourceSet();
        }

        // Apply obtained resource set modifier effects
        for (ObtainableResourceSetModifierEffect e : player.getEffectsImplementing(ObtainableResourceSetModifierEffect.class)) {
            actionSpaceBonus = e.modifyResources(actionSpaceBonus);
        }

        player.addResources(actionSpaceBonus);

        // Pay the card
        // Apply effects that modify the cost of the card
        for (DevelopmentCardRequiredResourceSetModifierEffect e : player.getEffectsImplementing(DevelopmentCardRequiredResourceSetModifierEffect.class)) {
            paymentForCard = e.modifyResources(paymentForCard, floor.getCard());
        }
        player.getResources().subtractResources(paymentForCard);

        // Set the floor as occupied
        floor.addOccupant(player, familyMemberColor);

        hasCurrentPlayerPlacedFamilyMember = true;

        player.getAvailableFamilyMembers().remove(familyMemberColor);

        developmentCardBeingTaken = floor.getCard();

        gameState = GameState.TAKING_CARD;
    }

    /**
     * Called when a player takes a development card
     *
     * @param username
     * @param cardId
     * @param councilPrivileges
     */
    public void takeDevelopmentCard(String username, UUID cardId, List<ObtainableResourceSet> councilPrivileges) throws ActionNotAllowedException {
        Player player = getLocalPlayer(username);
        if (!developmentCardBeingTaken.getId().equals(cardId)) {
            throw new ActionNotAllowedException("Unrecognized card ID");
        }
        DevelopmentCard card = developmentCardBeingTaken;

        assertPlayerTurn(player);
        assertGameState(GameState.TAKING_CARD);
        assertValidCouncilPrivilegesChoiceForCard(card, councilPrivileges);

        // Add immediate resources
        for (ImmediateResourcesEffect e : card.getEffectsContainer().getEffectsImplementing(ImmediateResourcesEffect.class)) {
            player.addResources(e.getObtainableResourceSet());
        }

        // Add council privileges
        for (ObtainableResourceSet councilPrivilege : councilPrivileges) {
            player.addResources(councilPrivilege);
        }

        player.addDevelopmentCard(card);

        game.getBoard().removeDevelopmentCardFromFloor(card);

        gameState = GameState.PLAYER_TURN;
    }

    public void playLeaderCard(String username, LeaderCard leaderCard) {
    }

    public void discardLeaderCard(String username, LeaderCard leaderCard) {
    }

    public <T extends OncePerRoundEffectInterface> void activateOncePerRoundEffect(String username, T effect) {
    }

    public void endGame() {
        for (Player player : game.getPlayers()) {
            int victoryPoints = 0;

            victoryPoints += computeVictoryPointsFromConqueredTerritories(player);
            victoryPoints += computeVictoryPointsFromInfluencedCharacters(player);
            victoryPoints += computeVictoryPointsFromEncouragedVentures(player);
            victoryPoints += computeVictoryPointsFromCollectedResources(player);

            player.getResources().addResource(ObtainableResource.VICTORY_POINTS, victoryPoints);

            computeVictoryPointsFromMilitaryStrength();
        }
    }

    /**
     * Performs an action
     *
     * @param username          the player performing the action
     * @param familyMemberValue the family member value used for performing the action
     * @returns an ArrayList representing the possible choices of resources that can be obtained from performing the action
     */
    void performAction(String username, int familyMemberValue, Action action) throws ActionNotAllowedException {
        // TODO
    }

    /**
     * Update the turn order
     * <p>
     * Algorithm: scans the players in the council palace *from right to left* and sets them as first,
     * so that the leftmost (the first that has occupied the council palace) will be first in the new turn order.
     */
    private void updateTurnOrder() {
        List<Player> councilPalaceOccupants = game.getBoard()
                                                  .getCouncilPalace()
                                                  .getOccupants()
                                                  .stream()
                                                  .map(occupation -> occupation.first)
                                                  .collect(Collectors.toList());

        for (int i = councilPalaceOccupants.size() - 1; i >= 0; i--) {
            game.setFirstPlayer(councilPalaceOccupants.get(i));
        }
    }

    private void startProduction(Player player) {
        setGameState(GameState.PRODUCTION);
        player.getBuildings().forEach(buildingCard -> buildingCard.setActivated(false));
    }

    private void startHarvest(Player player) {
        setGameState(GameState.HARVEST);
        player.getTerritories().forEach(territoryCard -> territoryCard.setActivated(false));
    }

    /* --------------------------------------------------------------------------------------
     * End of game victory points computation routines
     * -------------------------------------------------------------------------------------- */

    private int computeVictoryPointsFromConqueredTerritories(Player player) {
        boolean ignore = player.getEffectsImplementing(IgnoreEndOfGameVictoryPointsFromDevelopmentCardsEffect.class)
                               .stream()
                               .anyMatch(effect -> effect.ignoreVictoryPointsFromDevelopmentCard(TerritoryCard.class));
        if (ignore) return 0;

        int victoryPoints = 0;
        int conqueredTerritories = player.getTerritories().size();
        if (conqueredTerritories == 3) {
            victoryPoints = 1;
        }
        else if (conqueredTerritories == 4) {
            victoryPoints = 4;
        }
        else if (conqueredTerritories == 5) {
            victoryPoints = 10;
        }
        else if (conqueredTerritories == 6) {
            victoryPoints = 20;
        }
        return victoryPoints;
    }

    private int computeVictoryPointsFromInfluencedCharacters(Player player) {
        boolean ignore = player.getEffectsImplementing(IgnoreEndOfGameVictoryPointsFromDevelopmentCardsEffect.class)
                               .stream()
                               .anyMatch(effect -> effect.ignoreVictoryPointsFromDevelopmentCard(CharacterCard.class));
        if (ignore) return 0;

        int victoryPoints = 0;
        int influencedCharacters = player.getTerritories().size();
        if (influencedCharacters == 1) {
            victoryPoints = 1;
        }
        else if (influencedCharacters == 2) {
            victoryPoints = 3;
        }
        else if (influencedCharacters == 3) {
            victoryPoints = 6;
        }
        else if (influencedCharacters == 4) {
            victoryPoints = 10;
        }
        else if (influencedCharacters == 5) {
            victoryPoints = 15;
        }
        else if (influencedCharacters == 6) {
            victoryPoints = 21;
        }
        return victoryPoints;
    }

    private int computeVictoryPointsFromEncouragedVentures(Player player) {
        boolean ignore = player.getEffectsImplementing(IgnoreEndOfGameVictoryPointsFromDevelopmentCardsEffect.class)
                               .stream()
                               .anyMatch(effect -> effect.ignoreVictoryPointsFromDevelopmentCard(VentureCard.class));
        if (ignore) return 0;

        int victoryPoints = 0;

        for (EndOfGameResourcesEffect e : player.getEffectsImplementing(EndOfGameResourcesEffect.class)) {
            victoryPoints += e.getResourceSet().getObtainedAmount(VICTORY_POINTS);
        }

        return victoryPoints;
    }

    private void computeVictoryPointsFromMilitaryStrength() {
        List<Player> sortedPlayers = new ArrayList<>();
        sortedPlayers.addAll(game.getPlayers());
        sortedPlayers.sort(Comparator.comparingInt(player -> player.getResources().getAmount(MILITARY_POINTS)));


        int currentMilitaryPoints = sortedPlayers.get(sortedPlayers.size() - 1).getResources().getAmount(MILITARY_POINTS);
        int i = sortedPlayers.size() - 1;
        do {
            sortedPlayers.get(i).getResources().addResource(VICTORY_POINTS, 5);
            i--;
        }
        while (sortedPlayers.get(i).getResources().getAmount(MILITARY_POINTS) == currentMilitaryPoints && i >= 0);

        if (i < 0) return;

        currentMilitaryPoints = sortedPlayers.get(i).getResources().getAmount(MILITARY_POINTS);
        do {
            sortedPlayers.get(i).getResources().addResource(VICTORY_POINTS, 2);
            i--;
        }
        while (sortedPlayers.get(i).getResources().getAmount(MILITARY_POINTS) == currentMilitaryPoints && i >= 0);
    }

    private int computeVictoryPointsFromCollectedResources(Player player) {
        int victoryPoints;
        int resources = 0;
        resources += player.getResources().getAmount(WOOD);
        resources += player.getResources().getAmount(STONE);
        resources += player.getResources().getAmount(GOLD);
        resources += player.getResources().getAmount(SERVANTS);
        victoryPoints = resources / 5;
        return victoryPoints;

    }

    /* --------------------------------------------------------------------------------------
     * Validations and assertions
     * These methods are used to verify preconditions when players want to do something
     * -------------------------------------------------------------------------------------- */

    /**
     * Asserts that the game is in a given state or throw an ActionNotAllowedException
     *
     * @param gameState
     * @throws ActionNotAllowedException
     */
    public void assertGameState(GameState gameState) throws ActionNotAllowedException {
        if (this.gameState != gameState) {
            throw new ActionNotAllowedException();
        }
    }

    /**
     * Asserts that the current player is the provided one or throws an ActionNotAllowedException
     *
     * @param player
     * @throws ActionNotAllowedException
     */
    public void assertPlayerTurn(Player player) throws ActionNotAllowedException {
        if (!game.getCurrentPlayer().equals(player)) throw new ActionNotAllowedException("It's not your turn!");
    }

    /**
     * Asserts that the player can use the provided family member
     *
     * @param player
     * @param familyMemberColor
     * @throws ActionNotAllowedException
     */
    private void assertFamilyMemberAvailable(Player player, FamilyMemberColor familyMemberColor) throws ActionNotAllowedException {
        if (!player.getAvailableFamilyMembers().contains(familyMemberColor)) {
            throw new ActionNotAllowedException("You have already used that family member");
        }
    }

    /**
     * Asserts that the player has not yet placed his family member in his turn
     *
     * @throws ActionNotAllowedException
     */
    private void assertPlayerHasNotPlacedFamilyMember() throws ActionNotAllowedException {
        if (hasCurrentPlayerPlacedFamilyMember) {
            throw new ActionNotAllowedException("You have already placed a family member");
        }
    }

    private void assertActionSpaceIsEnabled(ActionSpace actionSpace) throws ActionNotAllowedException {
        if (!actionSpace.isEnabled()) {
            throw new ActionNotAllowedException("That action space is disabled in this game");
        }
    }

    private void assertPlayerIsNotInhibitedFromGoingTo(Player player, ActionSpace actionSpace) throws ActionNotAllowedException {
        if (player.getEffectsImplementing(InhibitActionSpaceEffect.class).stream()
                  .anyMatch(e -> e.isInhibited(actionSpace))) {
            throw new ActionNotAllowedException("You are prohibited from going to that action space");
        }
    }

    private void assertValidCouncilPrivilegesChoiceForCard(Card card, List<ObtainableResourceSet> councilPrivileges) throws ActionNotAllowedException {
        // Validate council privileges choice
        List<ImmediateResourcesEffect> effects = developmentCardBeingTaken.getEffectsContainer().getEffectsImplementing(ImmediateResourcesEffect.class);
        int allowedCouncilPrivileges = 0;
        for (ImmediateResourcesEffect e : effects) {
            allowedCouncilPrivileges += e.getObtainableResourceSet().getObtainedAmount(ObtainableResource.COUNCIL_PRIVILEGES);
        }
        assertValidCouncilPrivilegesChoice(councilPrivileges, allowedCouncilPrivileges);
    }

    /**
     * Asserts that a small action space is occupiable by a given player with a given family member
     *
     * @param actionSpace
     * @param player
     * @param familyMemberColor
     */
    private void assertSmallActionSpaceOccupiableBy(ActionSpace actionSpace, Player player, FamilyMemberColor familyMemberColor) throws ActionNotAllowedException {
        // Check if action space is enabled in current game
        assertActionSpaceIsEnabled(actionSpace);

        // Check if player has an excommunication that prohibits going to action space
        assertPlayerIsNotInhibitedFromGoingTo(player, actionSpace);

        // If the player cannot double occupy the action space we just check if it is occupied
        if (player.getEffectsImplementing(SkipOccupationCheckEffect.class).isEmpty()
                && !actionSpace.getOccupants().isEmpty()) {

            throw new ActionNotAllowedException("That action space is already occupied");
        }

        // Otherwise he can occupy it twice, but one of the family members must be neutral
        for (Tuple<Player, FamilyMemberColor> occupation : actionSpace.getOccupants()) {
            Player occupant = occupation.first;
            FamilyMemberColor occupantFamilyMemberColor = occupation.second;

            // If the player already occupies the market with a colored family member
            // he can occupy it again but only with his neutral family member
            if (occupant.equals(player)) {
                // So if we find a colored family member AND the player is trying to use another colored one
                // we deny the action
                if (occupantFamilyMemberColor != FamilyMemberColor.NEUTRAL
                        && familyMemberColor != FamilyMemberColor.NEUTRAL) {
                    throw new ActionNotAllowedException("You cannot place two colored family members in that action space");
                }
            }
        }
    }

    /**
     * Asserts that a big action space is occupiable by a given player with a given family member
     *
     * @param actionSpace
     * @param player
     * @param familyMemberColor
     */
    private void assertBigActionSpaceOccupiableBy(ActionSpace actionSpace, Player player, FamilyMemberColor familyMemberColor) throws ActionNotAllowedException {
        assertActionSpaceIsEnabled(actionSpace);

        assertPlayerIsNotInhibitedFromGoingTo(player, actionSpace);

        // A player can occupy the action space twice, but one of the family members must be neutral
        for (Tuple<Player, FamilyMemberColor> occupation : actionSpace.getOccupants()) {
            Player occupant = occupation.first;
            FamilyMemberColor occupantFamilyMemberColor = occupation.second;

            // If the player already occupies the market with a colored family member
            // he can occupy it again but only with his neutral family member
            if (occupant.equals(player)) {
                // So if we find a colored family member AND the player is trying to use another colored one
                // we deny the action
                if (occupantFamilyMemberColor != FamilyMemberColor.NEUTRAL
                        && familyMemberColor != FamilyMemberColor.NEUTRAL) {
                    throw new ActionNotAllowedException("You cannot place two colored family members in that action space");
                }
            }
        }
    }

    private void assertFloorHasCard(Floor floor) throws ActionNotAllowedException {
        if (floor.getCard() == null) {
            throw new ActionNotAllowedException("The card of that floor has already been taken");
        }
    }

    /**
     * Asserts that a floor is occupiable by a given player with a given family member
     *
     * @param floor
     * @param player
     * @param familyMemberColor
     * @param paymentForCard
     * @param chosenPrivileges
     */
    private void assertFloorOccupiableBy(Floor floor,
                                         Player player,
                                         FamilyMemberColor familyMemberColor,
                                         RequiredResourceSet paymentForCard,
                                         List<ObtainableResourceSet> chosenPrivileges) throws ActionNotAllowedException {
        assertFloorHasCard(floor);
        assertActionSpaceIsEnabled(floor);
        assertPlayerIsNotInhibitedFromGoingTo(player, floor);
        assertFamilyMemberColorRule(floor, player, familyMemberColor);
        assertMilitaryRequirement(player, floor.getCard());
        assertPlayerCanTakeAnotherCard(player, floor.getCard().getClass());

        // check that the player can cover the cost of occupying the floor
        ObtainedResourceSet playerClonedResourceSet = new ObtainedResourceSet(player.getResources());

        // Double occupation cost
        if (!player.hasEffectsImplementing(DoubleOccupationCostIgnoreEffect.class)) {
            RequiredResourceSet doubleOccupationCost = floor.getDoubleOccupationCost();
            if (!player.getResources().has(doubleOccupationCost)) {
                throw new ActionNotAllowedException("You don't have the resources to occupy an already occupied tower");
            }
            playerClonedResourceSet.subtractResources(doubleOccupationCost);
        }

        // Floor bonus
        ObtainableResourceSet actionSpaceBonus = floor.getBonus();
        for (ObtainableResourceSet privilege : chosenPrivileges) {
            actionSpaceBonus.addResources(privilege);
        }

        // Apply action space bonus setter effect (Preacher)
        for (FloorBonusResourcesSetterEffect e : player.getEffectsImplementing(FloorBonusResourcesSetterEffect.class)) {
            actionSpaceBonus = e.setObtainedResourceSet();
        }

        // Apply obtained resource set modifier effects
        for (ObtainableResourceSetModifierEffect e : player.getEffectsImplementing(ObtainableResourceSetModifierEffect.class)) {
            actionSpaceBonus = e.modifyResources(actionSpaceBonus);
        }

        playerClonedResourceSet.addResources(actionSpaceBonus);

        // STEP 3: check the requirements chosen to pay for the card
        paymentForCard = new RequiredResourceSet(paymentForCard);
        if (!floor.getCard().isPayableWith(paymentForCard)) {
            throw new ActionNotAllowedException("You cannot pay that price to take that card");
        }

        // Apply effects that modify the cost of the card
        for (DevelopmentCardRequiredResourceSetModifierEffect e : player.getEffectsImplementing(DevelopmentCardRequiredResourceSetModifierEffect.class)) {
            paymentForCard = e.modifyResources(paymentForCard, floor.getCard());
        }

        if (!playerClonedResourceSet.has(paymentForCard)) {
            throw new ActionNotAllowedException("You don't have the resources necessary to take that card");
        }
    }

    /**
     * Asserts that the player can place his family member on the floor and
     * is not trying to place two colored family members in the same tower
     *
     * @param floor
     * @param player
     * @param familyMemberColor
     * @throws ActionNotAllowedException
     */
    private void assertFamilyMemberColorRule(Floor floor, Player player, FamilyMemberColor familyMemberColor) throws ActionNotAllowedException {
        List<Tuple<Player, FamilyMemberColor>> occupations = new ArrayList<>();
        List<Floor> floors = floor.getTower().getFloors();
        for (Floor f : floors) {
            occupations.addAll(f.getOccupants());
        }

        // A player can occupy a tower twice, but one of the family members must be neutral
        for (Tuple<Player, FamilyMemberColor> occupation : occupations) {
            Player occupant = occupation.first;
            FamilyMemberColor occupantFamilyMemberColor = occupation.second;

            // If the player already occupies the market with a colored family member
            // he can occupy it again but only with his neutral family member
            if (occupant.equals(player)) {
                // So if we find a colored family member AND the player is trying to use another colored one
                // we deny the action
                if (occupantFamilyMemberColor != FamilyMemberColor.NEUTRAL
                        && familyMemberColor != FamilyMemberColor.NEUTRAL) {
                    throw new ActionNotAllowedException("You cannot place two colored family members in the same tower");
                }
            }
        }
    }

    /**
     * Asserts that the player has the necessary military points
     * to take another card (applies only to territory cards)
     *
     * @param player
     * @param card
     */
    private void assertMilitaryRequirement(Player player, DevelopmentCard card) throws ActionNotAllowedException {
        if (!(card instanceof TerritoryCard)) return;

        // If the card is a territory check military points requirement
        if (!player.hasEffectsImplementing(SkipMilitaryPointsRequirementEffect.class)) {

            int currentTerritories = player.getTerritories().size();
            int requiredMilitaryPoints = 0;

            if (currentTerritories == 5) {
                requiredMilitaryPoints = 18;
            }
            else if (currentTerritories == 4) {
                requiredMilitaryPoints = 12;
            }
            else if (currentTerritories == 3) {
                requiredMilitaryPoints = 7;
            }
            else if (currentTerritories == 2) requiredMilitaryPoints = 3;

            if (!player.getResources().hasAtLeast(requiredMilitaryPoints, ObtainableResource.MILITARY_POINTS)) {
                throw new ActionNotAllowedException("You don't have the military points to take another territory");
            }
        }
    }

    /**
     * Asserts that the player can take another card (he can't have more than 6 of the same kind)
     *
     * @param player
     * @param cardType
     * @throws ActionNotAllowedException
     */
    private void assertPlayerCanTakeAnotherCard(Player player, Class<? extends DevelopmentCard> cardType) throws ActionNotAllowedException {
        int currentCards;

        if (cardType == TerritoryCard.class) {
            currentCards = player.getTerritories().size();
        }
        else if (cardType == CharacterCard.class) {
            currentCards = player.getCharacters().size();
        }
        else if (cardType == BuildingCard.class) {
            currentCards = player.getBuildings().size();
        }
        else {
            currentCards = player.getVentures().size();
        }

        if (currentCards > 5) {
            throw new ActionNotAllowedException("You cannot take another development card of that kind");
        }

        // If the card is a territory check military points requirement
        if (!player.hasEffectsImplementing(SkipMilitaryPointsRequirementEffect.class)
                && cardType == TerritoryCard.class) {

            int currentTerritories = player.getTerritories().size();
            int requiredMilitaryPoints = 0;

            if (currentTerritories == 5) {
                requiredMilitaryPoints = 18;
            }
            else if (currentTerritories == 4) {
                requiredMilitaryPoints = 12;
            }
            else if (currentTerritories == 3) {
                requiredMilitaryPoints = 7;
            }
            else if (currentTerritories == 2) requiredMilitaryPoints = 3;

            if (!player.getResources().hasAtLeast(requiredMilitaryPoints, ObtainableResource.MILITARY_POINTS)) {
                throw new ActionNotAllowedException("You don't have the military points to take another territory");
            }
        }
    }

    /**
     * Asserts that the effective family member value is sufficient.
     * Warning: does not account for action-specific effects!
     *
     * @param player
     * @param familyMember
     * @param minimumValue
     * @throws ActionNotAllowedException
     */
    private void assertFamilyMemberValueIsAtLeast(Player player, FamilyMemberColor familyMember, int minimumValue) throws ActionNotAllowedException {
        int value = game.getInitialValueForFamilyMember(familyMember);

        for (FamilyMemberValueSetterEffectInterface e : player.getEffectsImplementing(FamilyMemberValueSetterEffectInterface.class)) {
            value = e.setValue(value, familyMember);
        }

        for (FamilyMemberValueModifierEffect e : player.getEffectsImplementing(FamilyMemberValueModifierEffect.class)) {
            value = e.modifyFamilyMemberValue(familyMember, value);
        }

        int spentServantsValue = player.getSpentServants();
        for (ServantsValueMultiplierEffect e : player.getEffectsImplementing(ServantsValueMultiplierEffect.class)) {
            spentServantsValue = e.multiplyServantValue(spentServantsValue);
        }

        if (value + spentServantsValue < minimumValue) {
            throw new ActionNotAllowedException("Family member value is too low");
        }
    }

    /**
     * Asserts that the effective value of the action is sufficient
     *
     * @param player
     * @param actionType
     * @param familyMember
     * @param minimumValue
     */
    private void assertEffectiveActionValueIsAtLeast(Player player, ActionType actionType, FamilyMemberColor familyMember, int minimumValue) throws ActionNotAllowedException {
        int value = game.getInitialValueForFamilyMember(familyMember);

        for (FamilyMemberValueSetterEffectInterface e : player.getEffectsImplementing(FamilyMemberValueSetterEffectInterface.class)) {
            value = e.setValue(value, familyMember);
        }

        for (FamilyMemberValueModifierEffect e : player.getEffectsImplementing(FamilyMemberValueModifierEffect.class)) {
            value = e.modifyFamilyMemberValue(familyMember, value);
        }

        for (ActionValueModifierEffect e : player.getEffectsImplementing(ActionValueModifierEffect.class)) {
            value = e.modifyValue(value, actionType);
        }

        int spentServantsValue = player.getSpentServants();
        for (ServantsValueMultiplierEffect e : player.getEffectsImplementing(ServantsValueMultiplierEffect.class)) {
            spentServantsValue = e.multiplyServantValue(spentServantsValue);
        }

        if (value + spentServantsValue < minimumValue) {
            throw new ActionNotAllowedException("Family member value is too low");
        }
    }

    /**
     * Asserts that the chosen council privileges are valid
     *
     * @param chosenCouncilPrivileges
     * @param allowedCouncilPrivileges
     * @throws ActionNotAllowedException
     */
    private void assertValidCouncilPrivilegesChoice(List<ObtainableResourceSet> chosenCouncilPrivileges, int allowedCouncilPrivileges) throws ActionNotAllowedException {
        if (!validateCouncilPrivileges(chosenCouncilPrivileges, allowedCouncilPrivileges)) {
            throw new ActionNotAllowedException("Invalid privileges choice");
        }
    }

    /**
     * Asserts that the player is in the list of the ones that can decide whether to be excommunicated or not
     * @param player
     * @throws ActionNotAllowedException
     */
    private void assertPlayerCanDecideHisExcommunication(Player player) throws ActionNotAllowedException {
        if(!playersWithPendingExcommunicationDecision.contains(player)) {
            throw new ActionNotAllowedException("%s can't decide if he has to be excommunicated");
        }
    }

    /**
     * Returns true if the player can choose these council privileges
     *
     * @param chosenCouncilPrivileges
     * @return
     */
    public boolean validateCouncilPrivileges(List<ObtainableResourceSet> chosenCouncilPrivileges) {
        // Copy the council privileges
        List<ObtainableResourceSet> allowedCouncilPrivileges = new ArrayList<>(game.getAllowedCouncilPrivileges());

        for (ObtainableResourceSet chosenPrivilege : chosenCouncilPrivileges) {
            if (!allowedCouncilPrivileges.contains(chosenPrivilege)) return false;
            allowedCouncilPrivileges.remove(chosenPrivilege);
        }

        return true;
    }

    /**
     * Returns true if the player can choose these council privileges
     *
     * @param chosenCouncilPrivileges
     * @param maxAllowedChoices
     * @return
     */
    public boolean validateCouncilPrivileges(List<ObtainableResourceSet> chosenCouncilPrivileges, int maxAllowedChoices) {
        if (chosenCouncilPrivileges.size() > maxAllowedChoices) return false;
        return validateCouncilPrivileges(chosenCouncilPrivileges);
    }

    public boolean canPlayerDecideHisExcommunication(String username) throws PlayerDoesNotExistException {
        Player player = getLocalPlayer(username);
        return playersWithPendingExcommunicationDecision.contains(player);
    }

    /* --------------------------------------------------------------------------------------
     * Getters for valid moves, etc
     * These methods are used mostly by the UI for getting which actions the player can perform
     * -------------------------------------------------------------------------------------- */
    public List<ActionSpace> getAllowedActionSpaces() {
        List<ActionSpace> actionSpaces = new ArrayList<>();
        actionSpaces.addAll(game.getBoard().getTerritoryTower().getFloors());
        actionSpaces.addAll(game.getBoard().getCharacterTower().getFloors());
        actionSpaces.addAll(game.getBoard().getBuildingTower().getFloors());
        actionSpaces.addAll(game.getBoard().getVentureTower().getFloors());
        actionSpaces.add(game.getBoard().getMarket1());
        actionSpaces.add(game.getBoard().getMarket2());
        actionSpaces.add(game.getBoard().getMarket3());
        actionSpaces.add(game.getBoard().getMarket4());
        actionSpaces.add(game.getBoard().getSmallHarvestArea());
        actionSpaces.add(game.getBoard().getBigHarvestArea());
        actionSpaces.add(game.getBoard().getSmallProductionArea());
        actionSpaces.add(game.getBoard().getBigProductionArea());
        actionSpaces.add(game.getBoard().getCouncilPalace());

        return actionSpaces.stream()
                           // Take only enabled action spaces
                           .filter(ActionSpace::isEnabled)
                           // Take only floors with cards
                           .filter(actionSpace -> {
                               if (actionSpace instanceof Floor) {
                                   return ((Floor) actionSpace).getCard() != null;
                               }
                               else {
                                   return true;
                               }
                           })
                           .collect(Collectors.toList());
    }

    public List<LeaderCard> getAllowedLeaderCards() {
        Player player = game.getCurrentPlayer();
        // TODO: account for card effects
        return player.getAvailableLeaderCards()
                     .stream()
                     .filter(card -> player.hasEnoughResourcesForAny(card.getRequiredResourceSet()))
                     .collect(Collectors.toList());
    }

    public List<RequiredResourceSet> getAllowedPaymentsForCard(DevelopmentCard card) {
        Player player = game.getCurrentPlayer();
        // TODO: apply card effects

        return card.getRequiredResourceSet();
    }

    public List<Card> getActivatableCards() {
        // TODO: filter only cards not already activated
        List<Card> cards = new ArrayList<>();

        Player player = game.getCurrentPlayer();

        player.getAllPlayedCards()
              .stream()
              .filter(card -> !card.getEffectsContainer()
                                   .getEffectsImplementing(OncePerRoundEffectInterface.class)
                                   .isEmpty())
              .forEach(cards::add);

        return cards;
    }

    /**
     * Return the bonus a player gets from occupying an action space,
     * accounting for excommunications
     *
     * @param actionSpace
     * @param player
     * @return
     */
    public ObtainableResourceSet getEffectiveActionSpaceBonus(ActionSpace actionSpace, Player player) {
        ObtainableResourceSet bonus = actionSpace.getBonus();
        // Apply action space bonus setter effect (Preacher)
        for (FloorBonusResourcesSetterEffect e : player.getEffectsImplementing(FloorBonusResourcesSetterEffect.class)) {
            bonus = e.setObtainedResourceSet();
        }

        // Apply obtained resource set modifier effects
        for (ObtainableResourceSetModifierEffect e : player.getEffectsImplementing(ObtainableResourceSetModifierEffect.class)) {
            bonus = e.modifyResources(bonus);
        }
        return bonus;
    }

    /* --------------------------------------------------------------------------------------
     * Getters for local objects
     * These methods retrieve a local object from its ID
     * -------------------------------------------------------------------------------------- */

    /**
     * Method needed to get a reference to a player object in the local Game
     *
     * @param username
     * @return
     * @throws PlayerDoesNotExistException
     */
    public Player getLocalPlayer(String username) throws PlayerDoesNotExistException {
        return game
                .getPlayers()
                .stream()
                .filter(p -> p.getUsername().equals(username))
                .findFirst()
                .orElseThrow(PlayerDoesNotExistException::new);
    }

    public PersonalBonusTile getLocalAvailablePersonalBonusTile(UUID personalBonusTileId) throws ActionNotAllowedException {
        return game.getAvailablePersonalBonusTiles().stream()
                   .filter(tile -> tile.getId().equals(personalBonusTileId))
                   .findFirst()
                   .orElseThrow(() -> new ActionNotAllowedException("Unrecognized ID for personal bonus tile"));
    }

    public Floor getLocalFloor(UUID floorId) throws ActionNotAllowedException {
        return game.getFloors().stream()
                   .filter(floor -> floor.getId().equals(floorId))
                   .findFirst()
                   .orElseThrow(() -> new ActionNotAllowedException("Unrecognized floor ID"));
    }

    public List<TerritoryCard> getLocalAvailableTerritoryCards(List<UUID> ids) throws ActionNotAllowedException {
        List cards = game.getAvailableTerritoryCards().stream()
                         .filter(card -> ids.contains(card.getId()))
                         .collect(Collectors.toList());

        if (ids.size() != cards.size()) throw new ActionNotAllowedException("Card ID not recognized");
        return cards;
    }

    public List<BuildingCard> getLocalAvailableBuildingCards(List<UUID> ids) throws ActionNotAllowedException {
        List cards = game.getAvailableBuildingCards().stream()
                         .filter(card -> ids.contains(card.getId()))
                         .collect(Collectors.toList());

        if (ids.size() != cards.size()) throw new ActionNotAllowedException("Card ID not recognized");
        return cards;
    }

    public List<CharacterCard> getLocalAvailableCharacterCards(List<UUID> ids) throws ActionNotAllowedException {
        List cards = game.getAvailableCharacterCards().stream()
                         .filter(card -> ids.contains(card.getId()))
                         .collect(Collectors.toList());

        if (ids.size() != cards.size()) throw new ActionNotAllowedException("Card ID not recognized");
        return cards;
    }

    public List<VentureCard> getLocalAvailableVentureCards(List<UUID> Ids) throws ActionNotAllowedException {
        List cards = game.getAvailableVentureCards().stream()
                         .filter(card -> Ids.contains(card.getId()))
                         .collect(Collectors.toList());

        if (Ids.size() != cards.size()) throw new ActionNotAllowedException("Card ID not recognized");
        return cards;
    }

    public ActionSpace getLocalActionSpace(UUID actionSpaceId) throws ActionNotAllowedException {
        return game.getActionSpaces().stream()
                   .filter(actionSpace -> actionSpace.getId().equals(actionSpaceId))
                   .findFirst()
                   .orElseThrow(() -> new ActionNotAllowedException("Unrecognized action space ID"));
    }

    public DevelopmentCard getLocalCardFromTowers(UUID cardId) throws ActionNotAllowedException {
        List<DevelopmentCard> cards = game.getBoard().getTerritoryTower().getCards();
        cards.addAll(game.getBoard().getBuildingTower().getCards());
        cards.addAll(game.getBoard().getCharacterTower().getCards());
        cards.addAll(game.getBoard().getVentureTower().getCards());

        return cards.stream()
                    .filter(card -> card.getId().equals(cardId))
                    .findFirst()
                    .orElseThrow(() -> new ActionNotAllowedException("Card ID not recognized"));
    }

    /*
     * Trivial getters and setters
     */

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public DevelopmentCard getDevelopmentCardBeingTaken() {
        return developmentCardBeingTaken;
    }

    public List<Player> getPlayersWithPendingExcommunicationDecision() {
        return playersWithPendingExcommunicationDecision;
    }

    public void setDiceValues(int blackDie, int whiteDie, int orangeDie) {
        game.setBlackDie(blackDie);
        game.setWhiteDie(whiteDie);
        game.setOrangeDie(orangeDie);
    }

    public boolean hasCurrentPlayerPlacedFamilyMember() {
        return hasCurrentPlayerPlacedFamilyMember;
    }

    public void setHasCurrentPlayerPlacedFamilyMember(boolean hasCurrentPlayerPlacedFamilyMember) {
        this.hasCurrentPlayerPlacedFamilyMember = hasCurrentPlayerPlacedFamilyMember;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void addPlayer(Player player) {
        game.addPlayer(player);
    }
}
