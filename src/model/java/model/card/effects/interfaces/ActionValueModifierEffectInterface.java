package model.card.effects.interfaces;

import model.action.Action;

/**
 * This is the interface for the effects that modify the value of the dices for some or for all actions
 */
public interface ActionValueModifierEffectInterface extends EffectInterface {
    /**
     * Compute the modified value for the specified action
     *
     * @param currentValue
     * @param action
     * @return
     */
    int modifyValue(int currentValue, Action action);
}
