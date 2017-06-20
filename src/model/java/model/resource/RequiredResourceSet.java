package model.resource;

import java.util.HashMap;

public class RequiredResourceSet {
    private HashMap<ResourceType, Integer> requiredResources = new HashMap<>();

    public RequiredResourceSet(HashMap<ResourceType, Integer> requiredResources) {
        this.requiredResources = requiredResources;
    }

    public HashMap<ResourceType, Integer> getRequiredResources() {
        return requiredResources;
    }

    public void setRequiredResources(HashMap<ResourceType, Integer> requiredResources) {
        this.requiredResources = requiredResources;
    }

    public int getResourceRequirement(ResourceType resourceType){
        return requiredResources.getOrDefault(resourceType, 0);
    }

    public void setResourceRequirement(ResourceType resourceType, int requirement){
        requiredResources.put(resourceType, requirement);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RequiredResourceSet)) return false;

        RequiredResourceSet otherResourceSet = (RequiredResourceSet) o;

        return requiredResources.equals(otherResourceSet.requiredResources);
    }
}
