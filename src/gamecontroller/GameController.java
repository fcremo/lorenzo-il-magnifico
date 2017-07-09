package gamecontroller;

import gamecontroller.exceptions.ActionNotAllowedException;
import gamecontroller.exceptions.PlayerDoesNotExistException;
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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class implements the game logic and is responsible for handling player actions (and raise exceptions),
 * updating the game state and making callbacks to report the changes.
 * The code is shared by the client and the server so that both use the exact same logic for updating the state of the game.
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

    public GameController() {
        this.gameState = GameState.WAITING_FOR_PLAYERS_TO_CONNECT;
    }

    /**
     * Called when a player chooses a personal bonus tile
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
     * Changes the current player
     *
     * @param username
     */
    public void setCurrentPlayer(String username) throws PlayerDoesNotExistException {
        game.setCurrentPlayer(getLocalPlayer(username));
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

    public void setDevelopmentCards(List<TerritoryCard> territoryCards, List<CharacterCard> characterCards, List<BuildingCard> buildingCards, List<VentureCard> ventureCards) {
        Board board = getGame().getBoard();

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
        Player player = getLocalPlayer(username);
        player.setSpentServants(servants);

        // TODO: check if the player has the servants he wants to commit.
        // Not urgent since the number of servants will be checked
        // when he will try to do an action
    }

    /**
     * Called when a player goes to an action space
     *
     * @param username
     * @param actionSpaceId
     * @param familyMemberColor
     * @param chosenPrivileges
     * @throws ActionNotAllowedException
     */
    public void goToActionSpace(String username, UUID actionSpaceId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges) throws ActionNotAllowedException {
        Player player = getLocalPlayer(username);
        ActionSpace actionSpace = getLocalActionSpace(actionSpaceId);
        assertPlayerTurn(player);
        assertGameState(GameState.PLAYER_TURN);
        assertPlayerHasNotPlacedFamilyMember();
        assertFamilyMemberAvailable(player, familyMemberColor);

        if(actionSpace instanceof MarketActionSpace) goToMarket(player, familyMemberColor, (MarketActionSpace)actionSpace, chosenPrivileges);
        else if(actionSpace instanceof CouncilPalace) goToCouncilPalace(player, familyMemberColor, chosenPrivileges);
        else if(actionSpace instanceof SmallProductionArea) goToSmallProduction(player, familyMemberColor, chosenPrivileges);
        else if(actionSpace instanceof BigProductionArea) goToBigProduction(player, familyMemberColor, chosenPrivileges);
        else if(actionSpace instanceof SmallHarvestArea) goToSmallHarvest(player, familyMemberColor, chosenPrivileges);
        else if(actionSpace instanceof BigHarvestArea) goToBigHarvest(player, familyMemberColor, chosenPrivileges);
        else throw new ActionNotAllowedException("Unrecognized action space");
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
        assertActionValueIsAtLeast(player, floor.getCard().getCardTakingActionType(), familyMemberColor, floor.getRequiredFamilyMemberValue());
        assertFloorOccupiableBy(floor, player, familyMemberColor, paymentForCard);

        // Spend servants and reset spent servants count
        player.getResources().subtractResource(ObtainableResource.SERVANTS, player.getSpentServants());
        player.setSpentServants(0);

        // Pay resources needed to occupy the floor
        if (player.getEffectsImplementing(DoubleOccupationCostIgnoreEffect.class).isEmpty()) {
            RequiredResourceSet cost = floor.getDoubleOccupationCost();
            player.getResources().subtractResources(cost);
        }

        // Add bonus resources to the player
        ObtainableResourceSet bonus = floor.getBonus();
        for (FloorBonusResourcesSetterEffect e : player.getEffectsImplementing(FloorBonusResourcesSetterEffect.class)) {
            // If there's more than one such effect the rules don't specify what to do.
            // Not an issue since in the original game there's only one card with this effect (the Preacher).
            bonus = e.setObtainedResourceSet();
        }

        for (ObtainableResourceSetModifierEffect e : player.getEffectsImplementing(ObtainableResourceSetModifierEffect.class)) {
            // TODO: theoretically the effect could return more than one choice
            List<ObtainableResourceSet> tmpList = new ArrayList<>();
            tmpList.add(bonus);
            tmpList = e.modifyResources(tmpList);
            bonus = tmpList.get(0);
        }
        player.addResources(bonus);

        // Set the floor as occupied
        floor.addOccupant(player, familyMemberColor);

        hasCurrentPlayerPlacedFamilyMember = true;

        gameState = GameState.TAKING_CARD;
    }

    public void chooseCouncilPrivileges(String username, List<ObtainableResourceSet> councilPrivileges) {
    }

    public void playLeaderCard(String username, LeaderCard leaderCard) {
    }

    public void discardLeaderCard(String username, LeaderCard leaderCard) {
    }

    public <T extends OncePerRoundEffectInterface> void activateOncePerRoundEffect(String username, T effect) {
    }

    /**
     * Performs the action and returns the set of resources obtained
     *
     * @param username            the player performing the action
     * @param familyMemberValue the family member value used for performing the action
     * @returns an ArrayList representing the possible choices of resources that can be obtained from performing the action
     */
    ArrayList<ObtainableResourceSet> performAction(String username, int familyMemberValue, Action action) throws ActionNotAllowedException {
        throw new NotImplementedException();
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

    /* --------------------------------------------------------------------------------------
     * Private methods section
     * -------------------------------------------------------------------------------------- */
    private void goToCouncilPalace(Player player, FamilyMemberColor familyMemberColor,
                                   List<ObtainableResourceSet> chosenCouncilPrivileges) throws ActionNotAllowedException {
        assertPlayerTurn(player);
        assertGameState(GameState.PLAYER_TURN);
        assertPlayerHasNotPlacedFamilyMember();
        assertFamilyMemberAvailable(player, familyMemberColor);
        assertFamilyMemberValueIsAtLeast(player, familyMemberColor, 1);
        assertBigActionSpaceOccupiableBy(game.getBoard().getCouncilPalace(), player, familyMemberColor);

        ObtainableResourceSet bonus = game.getBoard().getCouncilPalace().getBonus();
        int allowedCouncilPrivileges = bonus.getObtainedAmount(ObtainableResource.COUNCIL_PRIVILEGES);
        assertValidCouncilPrivilegesChoice(chosenCouncilPrivileges, allowedCouncilPrivileges);

        player.addResources(bonus);
        player.addResources(chosenCouncilPrivileges);

        game.getBoard().getCouncilPalace().addOccupant(player, familyMemberColor);
        hasCurrentPlayerPlacedFamilyMember = true;
    }

    private void goToMarket(Player player, FamilyMemberColor familyMemberColor,
                            MarketActionSpace marketActionSpace,
                            List<ObtainableResourceSet> chosenCouncilPrivileges) throws ActionNotAllowedException {
        assertPlayerTurn(player);
        assertGameState(GameState.PLAYER_TURN);
        assertPlayerHasNotPlacedFamilyMember();
        assertFamilyMemberAvailable(player, familyMemberColor);
        assertFamilyMemberValueIsAtLeast(player, familyMemberColor, 1);
        assertSmallActionSpaceOccupiableBy(marketActionSpace, player, familyMemberColor);

        ObtainableResourceSet bonus = marketActionSpace.getBonus();
        int allowedCouncilPrivileges = bonus.getObtainedAmount(ObtainableResource.COUNCIL_PRIVILEGES);
        assertValidCouncilPrivilegesChoice(chosenCouncilPrivileges, allowedCouncilPrivileges);

        player.addResources(bonus);
        player.addResources(chosenCouncilPrivileges);

        marketActionSpace.addOccupant(player, familyMemberColor);
        hasCurrentPlayerPlacedFamilyMember = true;
    }

    private void goToSmallHarvest(Player player,
                                  FamilyMemberColor familyMemberColor,
                                  List<ObtainableResourceSet> chosenCouncilPrivileges) throws ActionNotAllowedException {
        assertPlayerTurn(player);
        assertGameState(GameState.PLAYER_TURN);
        assertPlayerHasNotPlacedFamilyMember();
        assertFamilyMemberAvailable(player, familyMemberColor);
        assertActionValueIsAtLeast(player, ActionType.HARVEST, familyMemberColor, 1);

        ActionSpace harvestArea = game.getBoard().getSmallHarvestArea();
        assertSmallActionSpaceOccupiableBy(harvestArea, player, familyMemberColor);

        ObtainableResourceSet bonus = harvestArea.getBonus();
        int allowedCouncilPrivileges = bonus.getObtainedAmount(ObtainableResource.COUNCIL_PRIVILEGES);
        assertValidCouncilPrivilegesChoice(chosenCouncilPrivileges, allowedCouncilPrivileges);

        player.addResources(bonus);
        player.addResources(chosenCouncilPrivileges);

        harvestArea.addOccupant(player, familyMemberColor);
        hasCurrentPlayerPlacedFamilyMember = true;

        setGameState(GameState.HARVEST);
    }

    private void goToBigHarvest(Player player,
                                FamilyMemberColor familyMemberColor,
                                List<ObtainableResourceSet> chosenCouncilPrivileges) throws ActionNotAllowedException {
        assertPlayerTurn(player);
        assertGameState(GameState.PLAYER_TURN);
        assertPlayerHasNotPlacedFamilyMember();
        assertFamilyMemberAvailable(player, familyMemberColor);
        assertActionValueIsAtLeast(player, ActionType.HARVEST, familyMemberColor, 1);

        ActionSpace productionArea = game.getBoard().getBigHarvestArea();
        assertBigActionSpaceOccupiableBy(productionArea, player, familyMemberColor);

        ObtainableResourceSet bonus = productionArea.getBonus();
        int allowedCouncilPrivileges = bonus.getObtainedAmount(ObtainableResource.COUNCIL_PRIVILEGES);
        assertValidCouncilPrivilegesChoice(chosenCouncilPrivileges, allowedCouncilPrivileges);

        player.addResources(bonus);
        player.addResources(chosenCouncilPrivileges);

        productionArea.addOccupant(player, familyMemberColor);
        hasCurrentPlayerPlacedFamilyMember = true;

        setGameState(GameState.HARVEST);
    }

    private void goToSmallProduction(Player player,
                                     FamilyMemberColor familyMemberColor,
                                     List<ObtainableResourceSet> chosenCouncilPrivileges) throws ActionNotAllowedException {
        assertPlayerTurn(player);
        assertGameState(GameState.PLAYER_TURN);
        assertPlayerHasNotPlacedFamilyMember();
        assertFamilyMemberAvailable(player, familyMemberColor);
        assertActionValueIsAtLeast(player, ActionType.HARVEST, familyMemberColor, 1);

        ActionSpace productionArea = game.getBoard().getSmallProductionArea();
        assertSmallActionSpaceOccupiableBy(productionArea, player, familyMemberColor);

        ObtainableResourceSet bonus = productionArea.getBonus();
        int allowedCouncilPrivileges = bonus.getObtainedAmount(ObtainableResource.COUNCIL_PRIVILEGES);
        assertValidCouncilPrivilegesChoice(chosenCouncilPrivileges, allowedCouncilPrivileges);

        player.addResources(bonus);
        player.addResources(chosenCouncilPrivileges);

        productionArea.addOccupant(player, familyMemberColor);
        hasCurrentPlayerPlacedFamilyMember = true;

        setGameState(GameState.PRODUCTION);
    }

    private void goToBigProduction(Player player,
                                   FamilyMemberColor familyMemberColor,
                                   List<ObtainableResourceSet> chosenCouncilPrivileges) throws ActionNotAllowedException {
        assertPlayerTurn(player);
        assertGameState(GameState.PLAYER_TURN);
        assertPlayerHasNotPlacedFamilyMember();
        assertFamilyMemberAvailable(player, familyMemberColor);
        assertActionValueIsAtLeast(player, ActionType.HARVEST, familyMemberColor, 1);

        ActionSpace productionArea = game.getBoard().getBigProductionArea();
        assertBigActionSpaceOccupiableBy(productionArea, player, familyMemberColor);

        ObtainableResourceSet bonus = productionArea.getBonus();
        int allowedCouncilPrivileges = bonus.getObtainedAmount(ObtainableResource.COUNCIL_PRIVILEGES);
        assertValidCouncilPrivilegesChoice(chosenCouncilPrivileges, allowedCouncilPrivileges);

        player.addResources(bonus);
        player.addResources(chosenCouncilPrivileges);

        productionArea.addOccupant(player, familyMemberColor);
        hasCurrentPlayerPlacedFamilyMember = true;

        setGameState(GameState.PRODUCTION);
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
     */
    private void assertFloorOccupiableBy(Floor floor,
                                         Player player,
                                         FamilyMemberColor familyMemberColor,
                                         RequiredResourceSet paymentForCard) throws ActionNotAllowedException {
        assertFloorHasCard(floor);
        assertActionSpaceIsEnabled(floor);
        assertPlayerIsNotInhibitedFromGoingTo(player, floor);

        // STEP 1: check that the player can place that family member (color) on this floor
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

        // STEP 2: check that the player can cover the cost of occupying the floor
        ObtainedResourceSet playerClonedResourceSet = new ObtainedResourceSet(player.getResources());

        RequiredResourceSet doubleOccupationCost = floor.getDoubleOccupationCost();
        if (!player.hasEffectsImplementing(DoubleOccupationCostIgnoreEffect.class)) {
            if (!player.getResources().has(doubleOccupationCost)) {
                throw new ActionNotAllowedException("You don't have the resources to occupy an already occupied tower");
            }
            playerClonedResourceSet.subtractResources(doubleOccupationCost);
        }

        // STEP 3: check the requirements chosen to pay for the card
        if (!floor.getCard().getRequiredResourceSet().contains(paymentForCard)) {
            throw new ActionNotAllowedException("You cannot pay that resources to take that card");
        }

        if (!playerClonedResourceSet.has(paymentForCard)) {
            throw new ActionNotAllowedException("You don't have the resources necessary to take that card");
        }

        // TODO: check military requirements

        assertPlayerCanTakeAnotherCard(player, floor.getCard().getClass());
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
    private void assertActionValueIsAtLeast(Player player, ActionType actionType, FamilyMemberColor familyMember, int minimumValue) throws ActionNotAllowedException {
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

    /* --------------------------------------------------------------------------------------
     * Getters for valid moves, etc
     * These methods are used mostly by the UI for getting which actions the player can perform
     * -------------------------------------------------------------------------------------- */
    public List<ActionSpace> getAllowedActionSpaces() {
        // TODO: take player as parameter and return only action spaces where he's allowed to go
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

        return actionSpaces;
    }

    public List<LeaderCard> getAllowedLeaderCards() {
        // TODO: filter only the leader cards the current player can play
        return game.getCurrentPlayer().getAvailableLeaderCards();
    }

    public List<RequiredResourceSet> getAllowedPaymentsForCard(DevelopmentCard card) {
        Player player = game.getCurrentPlayer();
        // TODO: apply card effects

        return card.getRequiredResourceSet();
    }

    public List<Card> getActivatableCards() {
        // TODO: filter only cards activatable by the current player
        List<Card> cards = new ArrayList<>();

        Player player = game.getCurrentPlayer();

        player.getAllPlayedCards()
              .stream()
              .filter(
                      card -> !card.getEffectsContainer()
                                   .getEffectsImplementing(OncePerRoundEffectInterface.class).isEmpty())
              .forEach(cards::add);

        return cards;
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

    public ActionSpace getLocalActionSpace(UUID actionSpaceId) throws ActionNotAllowedException {
        return game.getActionSpaces().stream()
                   .filter(actionSpace -> actionSpace.getId().equals(actionSpaceId))
                   .findFirst()
                   .orElseThrow(() -> new ActionNotAllowedException("Unrecognized action space ID"));
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
}
