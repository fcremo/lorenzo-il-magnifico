package model.card.effects;

import model.card.development.BuildingCard;
import model.card.effects.interfaces.EffectInterface;
import model.player.Player;
import model.resource.ObtainableResource;
import model.resource.RequiredResourceSet;

import static model.resource.ObtainableResource.VICTORY_POINTS;

/**
 * This effect gives the player a penalty of 1 victory point for each wood or stone
 * paid to obtain his building cards.
 * From the rules:
 * "At the end of the game, you lose 1 Victory Point for every wood and stone on your Building Cards’ costs.
 * For example, if all your Building Cards cost 7 wood and 6 stone, you lose 13 Victory Points"
 */
public class EndOfGameBuildingCardCostPenaltyEffect implements EffectInterface {

    /**
     * Compute the penalty from player's building cards costs
     * @param player the player who receives the penalty
     * @return a ResourceSet to be subtracted from player's resources
     */
    public RequiredResourceSet computePenalty(Player player) {
        int penalty = 0;

        for (BuildingCard card : player.getBuildings()) {
            for (RequiredResourceSet requiredResourceSet : card.getRequiredResourceSet()) {
                penalty -= requiredResourceSet.getRequiredAmount(ObtainableResource.WOOD);
                penalty -= requiredResourceSet.getRequiredAmount(ObtainableResource.STONE);
            }
        }

        RequiredResourceSet result = new RequiredResourceSet();
        result.setRequiredAmount(VICTORY_POINTS, penalty);

        return result;
    }

    @Override
    public String toString() {
        return "At the end of the game, you lose 1 Victory Point for every wood and stone on your Building Cards’ costs";
    }
}
