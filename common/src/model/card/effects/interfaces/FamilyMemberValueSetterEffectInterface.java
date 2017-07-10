package model.card.effects.interfaces;

import model.player.FamilyMemberColor;

/**
 * This is the interface for the effects that set the value of a family member
 */
public interface FamilyMemberValueSetterEffectInterface extends EffectInterface {
    /**
     * Compute the new family member value
     *
     * @param currentValue      the current value
     * @param familyMemberColor the family member used
     * @return the new value
     */
    int setValue(int currentValue, FamilyMemberColor familyMemberColor);
}