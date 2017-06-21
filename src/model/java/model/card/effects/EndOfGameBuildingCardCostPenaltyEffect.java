package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.player.Player;
import model.resource.RequiredResourceSet;

/**
 * This effect gives the player a penalty of 1 victory point for each wood or stone
 * paid to obtain his building cards.
 * From the rules:
 * "At the end of the game, you lose 1 Victory Point for every wood and stone on your Building Cards’ costs.
 * For example, if all your Building Cards cost 7 wood and 6 stone, you lose 13 Victory Points"
 */
public class EndOfGameBuildingCardCostPenaltyEffect implements EffectInterface {
    public RequiredResourceSet computePenalty(Player player) {
        // TODO
        return null;
    }

    @Override
    public String toString() {
        return "At the end of the game, you lose 1 Victory Point for every wood and stone on your Building Cards’ costs.";
    }
}
