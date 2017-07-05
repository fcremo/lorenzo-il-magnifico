package model.card.effects;

import model.card.development.DevelopmentCard;
import model.card.effects.interfaces.EffectInterface;
import model.resource.RequiredResourceSet;
import model.resource.ResourceType;

import java.util.ArrayList;
import java.util.List;

/**
 * This effect modifies the set of resources necessary to take a development card
 */
public class DevelopmentCardRequiredResourceSetModifierEffect implements EffectInterface {
    private RequiredResourceSet discount;
    private Class<? extends DevelopmentCard> cardType;

    public DevelopmentCardRequiredResourceSetModifierEffect(RequiredResourceSet discount, Class<? extends DevelopmentCard> cardType) {
        this.discount = discount;
        this.cardType = cardType;
    }

    /**
     * Modify the set of resources needed to take a certain development card
     * @param currentRequiredResourceSets the current computed cost of the card
     * @param card the card being taken
     * @return the updated required resource set
     */
    public List<RequiredResourceSet> modifyResources(List<RequiredResourceSet> currentRequiredResourceSets, DevelopmentCard card) {
        if (card.getClass().equals(cardType)) {
            List<RequiredResourceSet> newResourceSets = new ArrayList<>();

            for (RequiredResourceSet requiredResourceSet : currentRequiredResourceSets) {
                RequiredResourceSet newRequiredResourceSet = new RequiredResourceSet(requiredResourceSet);

                for (ResourceType resource : newRequiredResourceSet.getRequiredResources().keySet()) {
                    int newValue = newRequiredResourceSet.getRequiredAmount(resource) + discount.getRequiredAmount(resource);
                    newRequiredResourceSet.setRequiredAmount(resource, newValue);
                }

                newResourceSets.add(newRequiredResourceSet);
            }

            return newResourceSets;
        }
        return currentRequiredResourceSets;
    }

    @Override
    public String toString() {
        return "You get a discount of " + discount + " when you take a " + cardType + ".";
    }
}
