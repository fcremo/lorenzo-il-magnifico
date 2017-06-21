package model.card.effects;

import model.card.development.DevelopmentCard;
import model.card.effects.interfaces.EffectInterface;
import model.resource.RequiredResourceSet;

import java.util.ArrayList;

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
     * @param currentResourceSets the current computed cost of the card
     * @param card the card being taken
     * @return
     */
    public ArrayList<RequiredResourceSet> modifyResources(ArrayList<RequiredResourceSet> currentResourceSets, DevelopmentCard card){
        // TODO
        return  null;
    }

    @Override
    public String toString() {
        return "You get a discount of " + discount + " when you take a " + cardType + ".";
    }
}
