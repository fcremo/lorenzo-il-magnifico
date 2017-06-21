package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainableResource;
import model.resource.ObtainedResourceSet;

import java.util.ArrayList;
import java.util.List;

/**
 * This effect modifies the quantity of a resource every time it is obtained by any mean
 */
public class ObtainedResourceSetModifierEffect implements EffectInterface {
    private ObtainableResource resourceToModify;
    private int modifier;

    public ObtainedResourceSetModifierEffect(ObtainableResource resourceToModify, int modifier) {
        this.resourceToModify = resourceToModify;
        this.modifier = modifier;
    }

    /**
     * Compute the list of resource sets that the player can choose from
     *
     * @param currentResources the current list of resource sets that the player can receive
     * @return the modified list of resource sets the player can choose from
     */
    public List<ObtainedResourceSet> modifyResources(List<ObtainedResourceSet> currentResources) {
        ArrayList<ObtainedResourceSet> newResourceSets = new ArrayList<>();
        for (ObtainedResourceSet set : currentResources) {
            newResourceSets.add(set.addResource(resourceToModify, modifier));
        }
        return newResourceSets;
    }

    @Override
    public String toString() {
        if (modifier > 0) {
            return "Each time you receive " + resourceToModify + ", you receive " + modifier + " more.";
        }
        else {
            return "Each time you receive " + resourceToModify + ", you receive " + modifier + " less.";
        }
    }
}
