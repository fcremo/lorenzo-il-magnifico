package model.card.effects;

import model.card.effects.interfaces.FamilyMemberValueSetterEffectInterface;
import model.player.FamilyMemberColor;

/**
 * This effect sets the value of a family member
 */
public class FamilyMemberValueSetterEffect implements FamilyMemberValueSetterEffectInterface {
    private FamilyMemberColor familyMemberColor;
    private int value;

    public FamilyMemberValueSetterEffect(FamilyMemberColor familyMemberColor, int value) {
        this.familyMemberColor = familyMemberColor;
        this.value = value;
    }

    @Override
    public int setValue(int currentValue, FamilyMemberColor familyMemberColor) {
        if (this.familyMemberColor == familyMemberColor) {
            return value;
        }
        else {
            return currentValue;
        }
    }

    @Override
    public String toString() {
        return "your " + familyMemberColor + " Family Member value starts from " + value + ", regardless of the die";
    }
}
