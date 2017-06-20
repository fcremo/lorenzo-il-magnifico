package model.card.effects;

import model.action.ActionType;
import model.card.effects.interfaces.EffectInterface;

import java.util.ArrayList;

/**
 * This effect allows the player to perform an extra action when taking a card,
 * without placing a family member
 */
public class ImmediateActionEffect implements EffectInterface {
    private ArrayList<ActionType> actionTypes;
    private int actionValue;

    public ImmediateActionEffect(ArrayList<ActionType> actionTypes, int actionValue) {
        this.actionTypes = actionTypes;
        this.actionValue = actionValue;
    }

    /**
     * Returns the types of actions that the player can perform.
     * N.B: The player can choose only one.
     *
     * @return the types of actions that the player can perform
     */
    public ArrayList<ActionType> getActions(){
        return actionTypes;
    }

    /**
     * @return the starting value of the action to perform
     */
    public int getActionValue() {
        return actionValue;
    }
}
