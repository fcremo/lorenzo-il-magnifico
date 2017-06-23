package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.player.FamilyMemberColor;

/**
 * This effect modifies the value of a family member
 */
public class FamilyMemberValueModifierEffect implements EffectInterface {
    private FamilyMemberColor familyMember;
    private int modifier;

    public FamilyMemberValueModifierEffect(FamilyMemberColor familyMember, int modifier) {
        this.familyMember = familyMember;
        this.modifier = modifier;
    }

    /**
     * Modify the value of the family member, if it is targeted by this effect
     *
     * @param familyMember the family member color
     * @param currentValue the current value of the family member
     * @return the new family member value
     */
    public int modifyFamilyMemberValue(FamilyMemberColor familyMember, int currentValue) {
        if (this.familyMember == familyMember) {
            return currentValue + modifier;
        }
        else {
            return currentValue;
        }
    }

    @Override
    public String toString() {
        if (modifier > 0) {
            return "Increase your " + familyMember + " Family Member value by " + modifier + ".";
        }
        else {
            return "Decrease your " + familyMember + " Family Member value by " + Math.abs(modifier) + ".";
        }
    }
}
