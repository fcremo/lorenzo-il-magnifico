package gamecontroller;

import model.Game;
import model.action.Action;
import model.action.ActionType;
import model.board.Board;
import model.board.actionspace.*;
import model.card.development.*;
import model.card.effects.*;
import model.card.effects.interfaces.FamilyMemberValueSetterEffectInterface;
import model.card.effects.interfaces.OncePerRoundEffectInterface;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.Player;
import model.resource.ObtainableResource;
import model.resource.ObtainableResourceSet;
import model.resource.ObtainedResourceSet;
import model.resource.RequiredResourceSet;
import model.util.Tuple;
import server.exceptions.ActionNotAllowedException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

import static model.action.ActionType.HARVEST;

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

    public void setDevelopmentCards(List<TerritoryCard> territoryCards, List<CharacterCard> characterCards, List<BuildingCard> buildingCards, List<VentureCard> ventureCards) {
        Board board = getGame().getBoard();

        board.getTerritoryTower().setCards(territoryCards);
        board.getCharacterTower().setCards(characterCards);
        board.getBuildingTower().setCards(buildingCards);
        board.getVentureTower().setCards(ventureCards);
    }

    public boolean canGoThere(Player player, FamilyMemberColor familyMember, ActionSpace actionSpace) {
        // Check if it is this player's turn
        if (!game.getCurrentPlayer().equals(player)) return false;

        // Check that the turn phase is right
        if (gameState != GameState.PLAYER_TURN) return false;

        // Check if action space is enabled in current game
        if (!actionSpace.isEnabled()) return false;

        // Check if player has an excommunication that prohibits going to action space
        if (player.getEffectsImplementing(InhibitActionSpaceEffect.class).stream()
                  .anyMatch(e -> e.isInhibited(actionSpace))) {
            return false;
        }

        // Check if the action space is not already occupied,
        // or if player has an ability that allows to occupy an already occupied action space
        if (actionSpace instanceof Floor && !actionSpace.getOccupants().isEmpty()) return false;
        if (actionSpace instanceof MarketActionSpace
                || actionSpace instanceof SmallProductionArea
                || actionSpace instanceof SmallHarvestArea) {
            if (!player.getEffectsImplementing(SkipOccupationCheckEffect.class).stream()
                       .findAny().isPresent()) {
                if (!actionSpace.getOccupants().isEmpty()) {
                    return false;
                }
            }
        }

        // Check if player is trying to occupy the same tower or large action space with two colored family members
        if (familyMember != FamilyMemberColor.NEUTRAL) {
            if (actionSpace instanceof Floor) {
                Floor floor = ((Floor) actionSpace);
                if (floor.getTower().isOccupiedBy(player)) return false;
            }
            if (actionSpace instanceof BigProductionArea
                    || actionSpace instanceof BigHarvestArea
                    || actionSpace instanceof CouncilPalace) {
                if (actionSpace.getOccupants().stream()
                               .anyMatch(o -> o.first.equals(player) && !o.second.equals(FamilyMemberColor.NEUTRAL)))
                    return false;
            }
        }

        // Check if family member value is sufficient
        int effectiveFamilyMemberValue = 0;
        if(familyMember == FamilyMemberColor.BLACK) effectiveFamilyMemberValue = game.getBlackDie();
        if(familyMember == FamilyMemberColor.ORANGE) effectiveFamilyMemberValue = game.getOrangeDie();
        if(familyMember == FamilyMemberColor.WHITE) effectiveFamilyMemberValue = game.getWhiteDie();

        // First evaluate the effects that override the family member value
        for (FamilyMemberValueSetterEffectInterface e: player.getEffectsImplementing(FamilyMemberValueSetterEffectInterface.class)) {
            // If there's more than one setter effect the rules don't specify what should happen.
            // Not really an issue since it is almost impossible that the player has two such effects
            effectiveFamilyMemberValue = e.setValue(effectiveFamilyMemberValue, familyMember);
        }

        // Then evaluate effects that modify the initial value
        for (FamilyMemberValueModifierEffect e: player.getEffectsImplementing(FamilyMemberValueModifierEffect.class)){
            effectiveFamilyMemberValue = e.modifyFamilyMemberValue(familyMember, effectiveFamilyMemberValue);
        }

        ActionType actionType = null;
        if (actionSpace instanceof Floor){
            Floor f = ((Floor) actionSpace);
            if(f.getCard() instanceof TerritoryCard) actionType = ActionType.TAKE_TERRITORY_CARD;
            if(f.getCard() instanceof CharacterCard) actionType = ActionType.TAKE_CHARACTER_CARD;
            if(f.getCard() instanceof BuildingCard) actionType = ActionType.TAKE_BUILDING_CARD;
            if(f.getCard() instanceof VentureCard) actionType = ActionType.TAKE_VENTURE_CARD;
        }
        else if(actionSpace instanceof SmallProductionArea
                || actionSpace instanceof BigProductionArea) actionType = ActionType.PRODUCTION;
        else if(actionSpace instanceof SmallHarvestArea
                || actionSpace instanceof BigHarvestArea) actionType = HARVEST;

        for (ActionValueModifierEffect e : player.getEffectsImplementing(ActionValueModifierEffect.class)) {
            effectiveFamilyMemberValue = e.modifyValue(effectiveFamilyMemberValue, actionType);
        }

        int servantsValue = player.getSpentServants();
        for(ServantsValueMultiplierEffect e : player.getEffectsImplementing(ServantsValueMultiplierEffect.class)) {
            servantsValue = e.multiplyServantValue(servantsValue);
        }

        if(effectiveFamilyMemberValue + servantsValue < actionSpace.getRequiredFamilyMemberValue()) {
            return false;
        }

        // Check that the player does not have 6 cards already
        if (!player.getEffectsImplementing(SkipMilitaryPointsRequirementEffect.class).isEmpty()
                && actionSpace instanceof Floor) {

            DevelopmentCard card = ((Floor) actionSpace).getCard();
            int currentCards;

            if (card instanceof TerritoryCard) currentCards = player.getTerritories().size();
            else if (card instanceof CharacterCard) currentCards = player.getCharacters().size();
            else if (card instanceof BuildingCard) currentCards = player.getBuildings().size();
            else currentCards = player.getVentures().size();

            if (currentCards > 5) {
                return false;
            }
        }

        // If the action space has a territory card inside check military points requirement
        if (!player.getEffectsImplementing(SkipMilitaryPointsRequirementEffect.class).isEmpty()
                && actionSpace instanceof Floor){

            DevelopmentCard card = ((Floor)actionSpace).getCard();
            if(card instanceof TerritoryCard) {
                int militaryPoints = player.getResources().get(ObtainableResource.MILITARY_POINTS);
                int currentTerritories = player.getTerritories().size();

                if(currentTerritories == 5 && militaryPoints < 18
                    || currentTerritories == 4 && militaryPoints < 12
                    || currentTerritories == 3 && militaryPoints < 7
                    || currentTerritories == 2 && militaryPoints < 3){
                    return false;
                }
            }
        }

        // Check that the player has the money to pay the double occupation cost, if the action space is a floor
        if(!player.getEffectsImplementing(DoubleOccupationCostIgnoreEffect.class).isEmpty()
                && actionSpace instanceof Floor){
            Floor floor = (Floor) actionSpace;
            if(floor.getTower().isOccupied()) {
                if(player.getResources().get(ObtainableResource.GOLD) < 3) {
                    return false;
                }
            }
        }

        // Check if the player has the necessary resources to take the card (if the action space is a floor)
        // TODO: apply the double occupation cost, discount the resources gained from the floor
        if(actionSpace instanceof Floor) {
            Floor floor = (Floor) actionSpace;
            List<RequiredResourceSet> currentCardCosts = floor.getCard().getRequiredResourceSet();

            // Apply discounts/maluses
            for(DevelopmentCardRequiredResourceSetModifierEffect e : player.getEffectsImplementing(DevelopmentCardRequiredResourceSetModifierEffect.class)){
                e.modifyResources(currentCardCosts, floor.getCard());
            }

            // If in no way the player can pay for the card then he can't go there
            if(currentCardCosts.stream().noneMatch(player::hasEnoughResources)){
                return false;
            }

        }

        return true;
    }

    public void spendServants(Player player, int servants) {
    }

    /**
     * Called when a player goes to an action space
     * @param player
     * @param familyMemberColor
     * @param floor
     * @param paymentForCard
     * @throws ActionNotAllowedException
     */
    public void goToFloor(Player player, FamilyMemberColor familyMemberColor, Floor floor, RequiredResourceSet paymentForCard)  throws ActionNotAllowedException {
        assertPlayerTurn(player);
        assertGameState(GameState.PLAYER_TURN);
        assertPlayerHasNotPlacedFamilyMember();
        assertFamilyMemberAvailable(player, familyMemberColor);
        assertActionValueIsAtLeast(player, floor.getCard().getCardTakingActionType(), familyMemberColor, floor.getRequiredFamilyMemberValue());
        assertFloorOccupiableBy(floor, player, familyMemberColor, paymentForCard);

        // if(!canGoThere(player, familyMemberColor, floor)) throw new ActionNotAllowedException("You cannot go there!");

        // Spend servants and reset spent servants count
        player.getResources().subtractResource(ObtainableResource.SERVANTS, player.getSpentServants());
        player.setSpentServants(0);

        // Pay resources needed to occupy the floor
        if(player.getEffectsImplementing(DoubleOccupationCostIgnoreEffect.class).isEmpty()){
            RequiredResourceSet cost = floor.getRequiredResourceSet();
            player.getResources().subtractResources(cost);
        }

        // Add bonus resources to the player
        ObtainableResourceSet bonus = floor.getBonus();
        for(FloorBonusResourcesSetterEffect e : player.getEffectsImplementing(FloorBonusResourcesSetterEffect.class)) {
            // If there's more than one such effect the rules don't specify what to do.
            // Not an issue since in the original game there's only one card with this effect (the Preacher).
            bonus = e.setObtainedResourceSet();
        }
        
        for(ObtainedResourceSetModifierEffect e : player.getEffectsImplementing(ObtainedResourceSetModifierEffect.class)) {
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
    }

    public void goToCouncilPalace(Player player, FamilyMemberColor familyMemberColor,
                                  List<ObtainableResourceSet> bonusChoices,
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

    public void goToMarket(Player player, FamilyMemberColor familyMemberColor,
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

    public void goToSmallHarvest(Player player,
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

    public void goToBigHarvest(Player player,
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

    public void goToSmallProducion(Player player,
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

    public void goToBigProducion(Player player,
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

    /**
     * Adds the player to the occupants of an action space and gives the player the bonus resources
     *
     * @param player
     * @param familyMemberColor
     * @param actionSpace
     */
    public void placeFamilyMember(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace) throws ActionNotAllowedException {
        assertGameState(GameState.PLAYER_TURN);
        assertPlayerTurn(player);
        assertFamilyMemberAvailable(player, familyMemberColor);
        if(hasCurrentPlayerPlacedFamilyMember) throw new ActionNotAllowedException("You have already placed a family member");
        if(!canGoThere(player, familyMemberColor, actionSpace)) throw new ActionNotAllowedException("You cannot go there!");

        // TODO: Add the bonus resources to the player
        actionSpace.getBonus();

    }

    public void chooseCouncilPrivileges(Player player, List<ObtainableResourceSet> councilPrivileges) {
    }

    public void playLeaderCard(Player player, LeaderCard leaderCard) {
    }

    public void discardLeaderCard(Player player, LeaderCard leaderCard) {
    }

    public <T extends OncePerRoundEffectInterface> void activateOncePerRoundEffect(Player player, T effect) {
    }

    /**
     * Performs the action and returns the set of resources obtained
     *
     * @param player            the player performing the action
     * @param familyMemberValue the family member value used for performing the action
     * @returns an ArrayList representing the possible choices of resources that can be obtained from performing the action
     */
    ArrayList<ObtainableResourceSet> performAction(Player player, int familyMemberValue, Action action) throws ActionNotAllowedException {
        throw new NotImplementedException();
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
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
     * Validations and assertions
     * -------------------------------------------------------------------------------------- */

    /**
     * Asserts that the game is in a given state or throw an ActionNotAllowedException
     * @param gameState
     * @throws ActionNotAllowedException
     */
    private void assertGameState(GameState gameState) throws ActionNotAllowedException {
        if (this.gameState != gameState) {
            throw new ActionNotAllowedException();
        }
    }

    /**
     * Asserts that the current player is the provided one or throws an ActionNotAllowedException
     * @param player
     * @throws ActionNotAllowedException
     */
    private void assertPlayerTurn(Player player) throws ActionNotAllowedException {
        if(!game.getCurrentPlayer().equals(player)) throw new ActionNotAllowedException("It's not your turn!");
    }

    /**
     * Asserts that the player can use the provided family member
     * @param player
     * @param familyMemberColor
     * @throws ActionNotAllowedException
     */
    private void assertFamilyMemberAvailable(Player player, FamilyMemberColor familyMemberColor) throws ActionNotAllowedException {
        if(!player.getAvailableFamilyMembers().contains(familyMemberColor)) throw new ActionNotAllowedException("You have already used that family member");
    }

    /**
     * Asserts that the player has not yet placed his family member in his turn
     * @param player
     * @throws ActionNotAllowedException
     */
    private void assertPlayerHasNotPlacedFamilyMember() throws ActionNotAllowedException {
        if(hasCurrentPlayerPlacedFamilyMember) {
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
     * @param player
     * @param familyMemberColor
     */
    private void assertSmallActionSpaceOccupiableBy(ActionSpace actionSpace, Player player, FamilyMemberColor familyMemberColor) throws ActionNotAllowedException {
        // Check if action space is enabled in current game
        assertActionSpaceIsEnabled(actionSpace);

        // Check if player has an excommunication that prohibits going to action space
        assertPlayerIsNotInhibitedFromGoingTo(player, actionSpace);

        // If the player cannot double occupy the action space we just check if it is occupied
        if(player.getEffectsImplementing(SkipOccupationCheckEffect.class).isEmpty()
                && !actionSpace.getOccupants().isEmpty()) {

            throw new ActionNotAllowedException("That action space is already occupied");
        }

        // Otherwise he can occupy it twice, but one of the family members must be neutral
        for(Tuple<Player, FamilyMemberColor> occupation : actionSpace.getOccupants()) {
            Player occupant = occupation.first;
            FamilyMemberColor occupantFamilyMemberColor = occupation.second;

            // If the player already occupies the market with a colored family member
            // he can occupy it again but only with his neutral family member
            if(occupant.equals(player)){
                // So if we find a colored family member AND the player is trying to use another colored one
                // we deny the action
                if(occupantFamilyMemberColor != FamilyMemberColor.NEUTRAL
                        && familyMemberColor != FamilyMemberColor.NEUTRAL) {
                    throw new ActionNotAllowedException("You cannot place two colored family members in that action space");
                }
            }
        }
    }

    /**
     * Asserts that a big action space is occupiable by a given player with a given family member
     * @param player
     * @param familyMemberColor
     */
    private void assertBigActionSpaceOccupiableBy(ActionSpace actionSpace, Player player, FamilyMemberColor familyMemberColor) throws ActionNotAllowedException {
        assertActionSpaceIsEnabled(actionSpace);

        assertPlayerIsNotInhibitedFromGoingTo(player, actionSpace);

        // A player can occupy the action space twice, but one of the family members must be neutral
        for(Tuple<Player, FamilyMemberColor> occupation : actionSpace.getOccupants()) {
            Player occupant = occupation.first;
            FamilyMemberColor occupantFamilyMemberColor = occupation.second;

            // If the player already occupies the market with a colored family member
            // he can occupy it again but only with his neutral family member
            if(occupant.equals(player)){
                // So if we find a colored family member AND the player is trying to use another colored one
                // we deny the action
                if(occupantFamilyMemberColor != FamilyMemberColor.NEUTRAL
                        && familyMemberColor != FamilyMemberColor.NEUTRAL) {
                    throw new ActionNotAllowedException("You cannot place two colored family members in that action space");
                }
            }
        }
    }

    private void assertFloorHasCard(Floor floor) throws ActionNotAllowedException {
        if(floor.getCard() == null) throw new ActionNotAllowedException("The card of that floor has already been taken");
    }

    /**
     * Asserts that a floor is occupiable by a given player with a given family member
     * @param floor
     * @param player
     * @param familyMemberColor
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
        for(Floor f : floors) {
            occupations.addAll(f.getOccupants());
        }

        // A player can occupy a tower twice, but one of the family members must be neutral
        for(Tuple<Player, FamilyMemberColor> occupation : occupations) {
            Player occupant = occupation.first;
            FamilyMemberColor occupantFamilyMemberColor = occupation.second;

            // If the player already occupies the market with a colored family member
            // he can occupy it again but only with his neutral family member
            if(occupant.equals(player)){
                // So if we find a colored family member AND the player is trying to use another colored one
                // we deny the action
                if(occupantFamilyMemberColor != FamilyMemberColor.NEUTRAL
                        && familyMemberColor != FamilyMemberColor.NEUTRAL) {
                    throw new ActionNotAllowedException("You cannot place two colored family members in the same tower");
                }
            }
        }

        // STEP 2: check that the player can cover the cost of occupying the floor
        RequiredResourceSet doubleOccupationCost = new RequiredResourceSet();
        doubleOccupationCost.setRequiredAmount(ObtainableResource.GOLD, 3);

        if(!player.getEffectsImplementing(DoubleOccupationCostIgnoreEffect.class).isEmpty()){
            doubleOccupationCost.setRequiredAmount(ObtainableResource.GOLD, 0);
        }

        if(!player.getResources().has(doubleOccupationCost)) {
            throw new ActionNotAllowedException("You don't have the resources to occupy an already occupied tower");
        }

        ObtainedResourceSet playerClonedResourceSet = new ObtainedResourceSet(player.getResources());
        playerClonedResourceSet.subtractResources(doubleOccupationCost);

        // STEP 3: check the requirements chosen to pay for the card
        if(!floor.getCard().getRequiredResourceSet().contains(paymentForCard)) {
            throw new ActionNotAllowedException("You cannot pay that resources to take that card");
        }

        if(!playerClonedResourceSet.has(paymentForCard)) {
            throw new ActionNotAllowedException("You don't have the resources necessary to take that card");
        }

        // TODO: check military requirements
        // TODO: ensure that the player does not have too many cards
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

        for(FamilyMemberValueSetterEffectInterface e : player.getEffectsImplementing(FamilyMemberValueSetterEffectInterface.class)){
            value = e.setValue(value, familyMember);
        }

        for(FamilyMemberValueModifierEffect e : player.getEffectsImplementing(FamilyMemberValueModifierEffect.class)) {
            value = e.modifyFamilyMemberValue(familyMember, value);
        }

        int spentServantsValue = player.getSpentServants();
        for(ServantsValueMultiplierEffect e : player.getEffectsImplementing(ServantsValueMultiplierEffect.class)) {
            spentServantsValue = e.multiplyServantValue(spentServantsValue);
        }

        if(value + spentServantsValue < minimumValue) throw new ActionNotAllowedException("Family member value is too low");
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

        for(FamilyMemberValueSetterEffectInterface e : player.getEffectsImplementing(FamilyMemberValueSetterEffectInterface.class)){
            value = e.setValue(value, familyMember);
        }

        for(FamilyMemberValueModifierEffect e : player.getEffectsImplementing(FamilyMemberValueModifierEffect.class)) {
            value = e.modifyFamilyMemberValue(familyMember, value);
        }

        for(ActionValueModifierEffect e : player.getEffectsImplementing(ActionValueModifierEffect.class)) {
            value = e.modifyValue(value, actionType);
        }

        int spentServantsValue = player.getSpentServants();
        for(ServantsValueMultiplierEffect e : player.getEffectsImplementing(ServantsValueMultiplierEffect.class)) {
            spentServantsValue = e.multiplyServantValue(spentServantsValue);
        }

        if(value + spentServantsValue < minimumValue) throw new ActionNotAllowedException("Family member value is too low");
    }

    /**
     * Asserts that the chosen council privileges are valid
     * @param chosenCouncilPrivileges
     * @param allowedCouncilPrivileges
     * @throws ActionNotAllowedException
     */
    private void assertValidCouncilPrivilegesChoice(List<ObtainableResourceSet> chosenCouncilPrivileges, int allowedCouncilPrivileges) throws ActionNotAllowedException {
        if(!validateCouncilPrivileges(chosenCouncilPrivileges, allowedCouncilPrivileges)) {
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
        List<ObtainableResourceSet> allowedCouncilPrivileges = new ArrayList<>(game.getCouncilPrivileges());

        for(ObtainableResourceSet chosenPrivilege : chosenCouncilPrivileges) {
            if(!allowedCouncilPrivileges.contains(chosenPrivilege)) return false;
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
        if(chosenCouncilPrivileges.size() > maxAllowedChoices) return false;
        return validateCouncilPrivileges(chosenCouncilPrivileges);
    }
}
