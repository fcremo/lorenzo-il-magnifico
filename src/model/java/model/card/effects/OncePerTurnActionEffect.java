package model.card.effects;

import model.action.ActionType;
import model.card.effects.interfaces.OncePerTurnEffectInterface;

import java.util.ArrayList;

/**
 * This effect allows the player to perform one extra action per turn
 */
public class OncePerTurnActionEffect implements OncePerTurnEffectInterface {
    /**
     * The turn when the effect was last activated
     */
    private int lastActivatedAt = 0;

    private ArrayList<ActionType> actionTypes;
    private int actionValue;

    public OncePerTurnActionEffect(ArrayList<ActionType> actionTypes, int actionValue) {
        this.actionTypes = actionTypes;
        this.actionValue = actionValue;
    }

    /**
     * Returns the types of actions that the player can perform.
     * N.B: The player can choose only one.
     *
     * @return the types of actions that the player can perform
     */
    public ArrayList<ActionType> getActionTypes() {
        return actionTypes;
    }

    /**
     * @return the starting value of the action to perform
     */
    public int getActionValue() {
        return actionValue;
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
