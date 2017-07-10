package model.card.effects;

import model.card.effects.interfaces.FamilyMemberValueSetterEffectInterface;
import model.card.effects.interfaces.OncePerRoundEffectInterface;
import model.player.FamilyMemberColor;

import java.util.ArrayList;
import java.util.List;

/**
 * This effect allows the player to set the value of one family member to a certain value once per round
 */
public class OncePerRoundFamilyMemberValueSetterEffect implements OncePerRoundEffectInterface, FamilyMemberValueSetterEffectInterface {
    private int lastActivatedAt = 0;

    private FamilyMemberColor familyMemberColor;

    private ArrayList<FamilyMemberColor> allowedFamilyMemberColor;
    private int value;

    public OncePerRoundFamilyMemberValueSetterEffect(List<FamilyMemberColor> allowedFamilyMemberColor, int value) {
        this.allowedFamilyMemberColor = new ArrayList<>(allowedFamilyMemberColor);
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
     *
     * @param currentValue      the current value
     * @param familyMemberColor the family member used
     * @return the new value of the family member
     */
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
    public boolean isAlreadyActivated(int currentTurn) {
        return currentTurn <= lastActivatedAt;
    }

    @Override
    public void markActivated(int currentTurn) {
        lastActivatedAt = currentTurn;
    }

    @Override
    public String toString() {
        return "once per round choose one of the " + allowedFamilyMemberColor.toString() + " Family Members to have value of " + value + ", regardless of its die";
    }
}
