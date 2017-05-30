package model.card.effects;

import model.card.effects.interfaces.ObtainedResourceSetModifierEffectInterface;
import model.resource.ObtainableResource;
import model.resource.ObtainedResourceSet;

import java.util.ArrayList;
import java.util.List;

public class ObtainedResourceModifier implements ObtainedResourceSetModifierEffectInterface {
    private ObtainableResource resourceToModify;
    private int modifier;

    public ObtainedResourceModifier(ObtainableResource resourceToModify, int modifier) {
        this.resourceToModify = resourceToModify;
        this.modifier = modifier;
    }

    @Override
    public List<ObtainedResourceSet> modifyResources(List<ObtainedResourceSet> currentResources) {
        ArrayList<ObtainedResourceSet> newResourceSets = new ArrayList<>();
        for(ObtainedResourceSet set: currentResources){
            newResourceSets.add(set.addResource(resourceToModify, modifier));
        }
        return newResourceSets;
    }
}
