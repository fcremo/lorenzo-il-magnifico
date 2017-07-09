package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainableResource;
import model.resource.ObtainableResourceSet;

import java.util.ArrayList;
import java.util.List;

/**
 * This effect modifies the quantity of a resource every time it is obtained by any mean
 */
public class ObtainableResourceSetModifierEffect implements EffectInterface {
    private ObtainableResource resourceToModify;
    private int modifier;

    public ObtainableResourceSetModifierEffect(ObtainableResource resourceToModify, int modifier) {
        this.resourceToModify = resourceToModify;
        this.modifier = modifier;
    }

    /**
     * Compute the list of resource sets that the player can choose from
     *
     * @param currentResources the current list of resource sets that the player can receive
     * @return the modified list of resource sets the player can choose from
     */
    public List<ObtainableResourceSet> modifyResources(List<ObtainableResourceSet> currentResources) {
        ArrayList<ObtainableResourceSet> newResourceSets = new ArrayList<>();

        for (ObtainableResourceSet originalSet : currentResources) {
            ObtainableResourceSet clonedResourceSet = new ObtainableResourceSet(originalSet);
            clonedResourceSet.addResource(resourceToModify, modifier);
            newResourceSets.add(clonedResourceSet);
        }

        return newResourceSets;
    }

    /**
     * Compute the modified resource set
     *
     * @param currentResources the current list of resource sets that the player can receive
     * @return the modified list of resource sets the player can choose from
     */
    public ObtainableResourceSet modifyResources(ObtainableResourceSet currentResources) {
        ObtainableResourceSet clonedResourceSet = new ObtainableResourceSet(currentResources);
        clonedResourceSet.addResource(resourceToModify, modifier);
        return clonedResourceSet;
    }

    @Override
    public String toString() {
        if (modifier > 0) {
            return "Each time you receive " + resourceToModify + ", you receive " + modifier + " more";
        }
        else {
            return "Each time you receive " + resourceToModify + ", you receive " + modifier + " less";
        }
    }
}
