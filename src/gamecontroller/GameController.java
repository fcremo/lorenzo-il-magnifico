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
import model.resource.ObtainedResourceSet;
import model.resource.RequiredResourceSet;
import server.exceptions.ActionNotAllowedException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

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
        if(familyMember == FamilyMemberColor.BLACK) effectiveFamilyMemberValue = game.getBlackDice();
        if(familyMember == FamilyMemberColor.ORANGE) effectiveFamilyMemberValue = game.getOrangeDice();
        if(familyMember == FamilyMemberColor.WHITE) effectiveFamilyMemberValue = game.getWhiteDice();

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
        else if (actionSpace instanceof MarketActionSpace) actionType = ActionType.MARKET;
        else if(actionSpace instanceof SmallProductionArea
                || actionSpace instanceof BigProductionArea) actionType = ActionType.PRODUCTION;
        else if(actionSpace instanceof SmallHarvestArea
                || actionSpace instanceof BigHarvestArea) actionType = ActionType.HARVEST;

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
        // TODO: remember to apply the double occupation cost
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

    public void goToFloor(Player player, FamilyMemberColor familyMemberColor, Floor floor) {
    }

    public void goToSmallHarvest(Player player, FamilyMemberColor familyMemberColor) {
    }

    public void goToBigHarvest(Player player, FamilyMemberColor familyMemberColor) {
    }

    public void goToSmallProducion(Player player, FamilyMemberColor familyMemberColor) {
    }

    public void goToBigProducion(Player player, FamilyMemberColor familyMemberColor) {
    }

    public void goToCouncilPalace(Player player, FamilyMemberColor familyMemberColor) {
    }

    public void goToMarket(Player player, FamilyMemberColor familyMemberColor, ActionSpace marketActionSpace) {
    }

    /**
     * Adds the player to the occupants of an action space
     *
     * @param player
     * @param familyMemberColor
     * @param actionSpace
     */
    public void placeFamilyMember(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace) {
    }

    public void chooseCouncilPrivileges(Player player, List<ObtainedResourceSet> councilPrivileges) {
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
    ArrayList<ObtainedResourceSet> performAction(Player player, int familyMemberValue, Action action) throws ActionNotAllowedException {
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
}
