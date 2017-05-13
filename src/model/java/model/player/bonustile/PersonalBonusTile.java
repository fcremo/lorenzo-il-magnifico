package model.player.bonustile;

import model.resource.ObtainedResourceSet;
import model.resource.RequiredResourceSet;

public class PersonalBonusTile {
    ObtainedResourceSet obtainedResourceSet;
    RequiredResourceSet requiredResourceSet;

    public PersonalBonusTile(ObtainedResourceSet obtainedResourceSet, RequiredResourceSet requiredResourceSet) {
        this.obtainedResourceSet = obtainedResourceSet;
        this.requiredResourceSet = requiredResourceSet;
    }

    public ObtainedResourceSet getObtainedResourceSet() {
        return obtainedResourceSet;
    }

    public void setObtainedResourceSet(ObtainedResourceSet obtainedResourceSet) {
        this.obtainedResourceSet = obtainedResourceSet;
    }

    public RequiredResourceSet getRequiredResourceSet() {
        return requiredResourceSet;
    }

    public void setRequiredResourceSet(RequiredResourceSet requiredResourceSet) {
        this.requiredResourceSet = requiredResourceSet;
    }
}
