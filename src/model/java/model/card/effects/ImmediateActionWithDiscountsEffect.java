package model.card.effects;

import model.action.ActionType;
import model.card.effects.interfaces.EffectInterface;
import model.resource.RequiredResourceSet;

import java.util.ArrayList;

/**
 * This effect allows the player to perform an extra action when taking a card,
 * with a discount on the resources needed to perform the action
 * (e.g. -3 gold on the cost of taking a development card)
 */
public class ImmediateActionWithDiscountsEffect extends ImmediateActionEffect implements EffectInterface {
    private ArrayList<RequiredResourceSet> discounts;

    public ImmediateActionWithDiscountsEffect(ArrayList<ActionType> actionTypes, int actionValue, ArrayList<RequiredResourceSet> discounts) {
        super(actionTypes, actionValue);
        this.discounts = discounts;
    }

    /**
     * Modify the required resource sets for the performed action
     * @param currentRequiredResourceSets
     * @return the discounted resource set
     */
    public ArrayList<RequiredResourceSet> modifyRequiredResourceSet(ArrayList<RequiredResourceSet> currentRequiredResourceSets) {
        // TODO
        return null;
    }

    @Override
    public String toString() {
        StringBuilder effects = new StringBuilder("Perform a ");
        for (ActionType action : actionTypes) {
            effects.append(action)
                    .append(" or ");
        }
        effects.delete(effects.lastIndexOf(" or "), effects.lastIndexOf(" or ") + 4);
        effects.append(" with value ")
                .append(actionValue)
                .append(" without placing a Family Member. In addition, the cost of the card you take is reduced by ")
                .append(discounts)
                .append(".");
        return effects.toString();
    }
}
