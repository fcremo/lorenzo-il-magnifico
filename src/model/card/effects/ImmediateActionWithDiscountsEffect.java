package model.card.effects;

import model.action.ActionType;
import model.card.effects.interfaces.EffectInterface;
import model.resource.RequiredResourceSet;
import model.resource.ResourceType;

import java.util.ArrayList;
import java.util.List;

/**
 * This effect allows the player to perform an extra action when taking a card,
 * with a discount on the resources needed to perform the action
 * (e.g. -3 gold on the cost of taking a development card)
 */
public class ImmediateActionWithDiscountsEffect extends ImmediateActionEffect implements EffectInterface {
    private ArrayList<RequiredResourceSet> discounts;

    public ImmediateActionWithDiscountsEffect(List<ActionType> actionTypes, int actionValue, List<RequiredResourceSet> discounts) {
        super(actionTypes, actionValue);
        this.discounts = new ArrayList<>(discounts);
    }

    /**
     * Modify the required resource sets for the performed action
     *
     * @param currentRequiredResourceSets
     * @return the discounted resource set
     */
    public List<RequiredResourceSet> modifyRequiredResourceSet(List<RequiredResourceSet> currentRequiredResourceSets) {
        List<RequiredResourceSet> newResourceSets = new ArrayList<>();

        for (RequiredResourceSet requiredResourceSet : currentRequiredResourceSets) {
            RequiredResourceSet newRequiredResourceSet = new RequiredResourceSet(requiredResourceSet);

            for (RequiredResourceSet discount : discounts) {
                for (ResourceType resource : newRequiredResourceSet.getRequiredResources().keySet()) {
                    int newValue = newRequiredResourceSet.getRequiredAmount(resource) + discount.getRequiredAmount(resource);
                    newRequiredResourceSet.setRequiredAmount(resource, newValue);
                }

                newResourceSets.add(newRequiredResourceSet);
            }
        }

        return newResourceSets;
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
