package model.resource;

import java.util.HashMap;

public class RequiredResourceSet {
    HashMap<ResourceType, Integer> requiredResources;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RequiredResourceSet)) return false;

        RequiredResourceSet otherResourceSet = (RequiredResourceSet) o;

        return requiredResources.equals(otherResourceSet.requiredResources);
    }
}
