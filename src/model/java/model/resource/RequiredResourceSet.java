package model.resource;

import java.io.Serializable;
import java.util.HashMap;

public class RequiredResourceSet implements Serializable {
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

    public boolean isEmpty() {
        return (requiredResources.isEmpty());
    }

    @Override
    public String toString() {
        StringBuilder printable = new StringBuilder();
        for (ResourceType resource : requiredResources.keySet()) {
            printable.append(requiredResources.get(resource).toString())
                    .append(" ")
                    .append(resource.toString())
                    .append(", ");
        }
        if (printable.lastIndexOf(", ") != -1) {
            printable.delete(printable.lastIndexOf(", "), printable.lastIndexOf(", ") + 2);
        }
        return printable.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RequiredResourceSet)) return false;

        RequiredResourceSet otherResourceSet = (RequiredResourceSet) o;

        return requiredResources.equals(otherResourceSet.requiredResources);
    }
}
