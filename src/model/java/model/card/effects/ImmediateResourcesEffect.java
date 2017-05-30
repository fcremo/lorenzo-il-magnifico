package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainedResourceSet;

import java.util.ArrayList;

public class ImmediateResourcesEffect implements EffectInterface {
    private ArrayList<ObtainedResourceSet> obtainedResourceSets;

    public ImmediateResourcesEffect(ArrayList<ObtainedResourceSet> obtainedResourceSets) {
        this.obtainedResourceSets = obtainedResourceSets;
    }

    public ArrayList<ObtainedResourceSet> getObtainedResourceSets() {
        return obtainedResourceSets;
    }

    public void setObtainedResourceSets(ArrayList<ObtainedResourceSet> obtainedResourceSets) {
        this.obtainedResourceSets = obtainedResourceSets;
    }
}
