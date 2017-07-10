package model.card.effects;

import model.card.effects.interfaces.EffectInterface;

/**
 * This effect allows the player not to spend 3 coins when they place their
 * Family Members in a Tower that is already occupied.
 */
public class DoubleOccupationCostIgnoreEffect implements EffectInterface {
    @Override
    public String toString() {
        return "you don’t have to spend 3 coins when you place your Family Members in a Tower that is already occupied";
    }
}