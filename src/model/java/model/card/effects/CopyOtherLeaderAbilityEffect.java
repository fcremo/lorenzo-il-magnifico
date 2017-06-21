package model.card.effects;

import model.card.effects.interfaces.EffectInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * This effect allows the player to copy the ability of another Leader Card already played by another player
 */
public class CopyOtherLeaderAbilityEffect implements EffectInterface {
    private ArrayList<EffectInterface> clonedEffects = new ArrayList<>();

    public List<EffectInterface> getClonedEffects() {
        return clonedEffects;
    }

    public void setClonedEffects(List<EffectInterface> clonedEffects) {
        this.clonedEffects = new ArrayList<>(clonedEffects);
    }

    @Override
    public String toString() {
        return "Copy the ability of another Leader Card already played by another player. Once you decide the ability to copy, it can’t be changed.";
    }
}
