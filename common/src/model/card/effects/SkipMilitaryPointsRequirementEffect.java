package model.card.effects;

import model.card.effects.interfaces.EffectInterface;

/**
 * This effect allows the player to take territory cards regardless of
 * how many military points they have
 */
public class SkipMilitaryPointsRequirementEffect implements EffectInterface {
    @Override
    public String toString() {
        return "You don’t need to satisfy the Military Points requirement when you take Territory Cards";
    }
}
