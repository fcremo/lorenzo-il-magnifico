package model.card.effects;

import model.card.effects.interfaces.EffectInterface;

/**
 * This effect allows the player to occupy an action space even if it is already occupied
 */
public class SkipOccupationCheckEffect implements EffectInterface {
    @Override
    public String toString() {
        return "You can place your Family Members in occupied action spaces";
    }
}
