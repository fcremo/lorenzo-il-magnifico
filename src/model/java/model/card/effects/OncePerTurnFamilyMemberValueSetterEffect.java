package model.card.effects;

import model.card.effects.interfaces.FamilyMemberValueSetterEffectInterface;
import model.card.effects.interfaces.OncePerTurnEffectInterface;
import model.player.FamilyMemberColor;

import java.util.ArrayList;
import java.util.List;

/**
 * This effect allows the player to set the value of one family member to a certain value once per turn
 */
public class OncePerTurnFamilyMemberValueSetterEffect implements OncePerTurnEffectInterface, FamilyMemberValueSetterEffectInterface {
    private int lastActivatedAt = 0;

    private FamilyMemberColor familyMemberColor;

    private ArrayList<FamilyMemberColor> allowedFamilyMemberColor;
    private int value;

    public OncePerTurnFamilyMemberValueSetterEffect(ArrayList<FamilyMemberColor> allowedFamilyMemberColor, int value) {
        this.allowedFamilyMemberColor = allowedFamilyMemberColor;
        this.value = value;
    }

    public List<FamilyMemberColor> getAllowedFamilyMemberColor() {
        return allowedFamilyMemberColor;
    }

    public void setFamilyMemberColor(FamilyMemberColor familyMemberColor) {
        this.familyMemberColor = familyMemberColor;
    }

    /**
     * Set the value of the family member
     * @param currentValue      the current value
     * @param familyMemberColor the family member used
     * @return the new value of the family member
     */
    @Override
    public int setValue(int currentValue, FamilyMemberColor familyMemberColor) {
        if(this.familyMemberColor == familyMemberColor){
            return value;
        }
        else {
            return currentValue;
        }
    }

    @Override
    public boolean isAlreadyActivated(int currentTurn) {
        return currentTurn <= lastActivatedAt;
    }

    @Override
    public void markActivated(int currentTurn) {
        lastActivatedAt = currentTurn;
    }
}
