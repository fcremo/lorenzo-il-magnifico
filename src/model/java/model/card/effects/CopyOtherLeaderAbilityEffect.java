package model.card.effects;

import model.card.effects.interfaces.EffectInterface;

import java.util.ArrayList;

/**
 * This effect allows the player to copy the ability of another Leader Card already played by another player
 */
public class CopyOtherLeaderAbilityEffect implements EffectInterface {
    ArrayList<EffectInterface> clonedEffects = new ArrayList<>();

    public ArrayList<EffectInterface> getClonedEffects() {
        return clonedEffects;
    }

    public void setClonedEffects(ArrayList<EffectInterface> clonedEffects) {
        this.clonedEffects = clonedEffects;
    }
}
