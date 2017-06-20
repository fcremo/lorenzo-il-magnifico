package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.player.FamilyMemberColor;

/**
 * This effect sets the value of a family member
 */
public class FamilyMemberValueSetterEffect implements EffectInterface {
    private FamilyMemberColor familyMemberColor;
    private int value;

    public FamilyMemberValueSetterEffect(FamilyMemberColor familyMemberColor, int value) {
        this.familyMemberColor = familyMemberColor;
        this.value = value;
    }

    public int setFamilyMemberValue(FamilyMemberColor familyMember, int currentValue){
        if(familyMemberColor == familyMember){
            return value;
        }
        else {
            return currentValue;
        }
    }
}
