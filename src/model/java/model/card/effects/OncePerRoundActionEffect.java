package model.card.effects;

import model.action.ActionType;
import model.card.effects.interfaces.OncePerRoundEffectInterface;

/**
 * This effect allows the player to perform one extra action per turn
 */
public class OncePerRoundActionEffect implements OncePerRoundEffectInterface {
    /**
     * The turn when the effect was last activated
     */
    private int lastActivatedAt = 0;

    private ActionType actionType;
    private int actionValue;

    public OncePerRoundActionEffect(ActionType actionType, int actionValue) {
        this.actionType = actionType;
        this.actionValue = actionValue;
    }

    /**
     * Returns the type of action that the player can perform.
     * N.B: The player can choose only one.
     *
     * @return the type of action that the player can perform
     */
    public ActionType getActionType() {
        return actionType;
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

    @Override
    public String toString() {
        return "Once per round perform a " + actionType + " with value " + actionValue + " without placing a Family Member.";
    }}
