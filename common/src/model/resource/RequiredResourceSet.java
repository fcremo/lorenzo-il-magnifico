package model.resource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This interface represents a set of required resources
 */
public class RequiredResourceSet implements Serializable {
    private Map<ResourceType, Integer> requiredResources = new HashMap<>();

    public RequiredResourceSet(Map<ResourceType, Integer> requiredResources) {
        this.requiredResources = new HashMap<>(requiredResources);
    }

    /**
     * Empty constructor
     */
    public RequiredResourceSet() {}

    /**
     * Copy constructor
     */
    public RequiredResourceSet(RequiredResourceSet requiredResourceSet) {
        this.requiredResources = new HashMap<>(requiredResourceSet.requiredResources);
        // TODO: 5/25/17 this works until nothing modifies multipliers. Implement proper deep cloning.
    }

    /**
     * @return true if the resource set is empty
     */
    public boolean isEmpty() {
        return requiredResources.values().stream().allMatch(val -> val.equals(0));
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (ResourceType resource : requiredResources.keySet()) {
            string.append(requiredResources.get(resource).toString())
                     .append(" ")
                     .append(resource.toString())
                     .append(", ");
        }
        if (string.lastIndexOf(", ") != -1) {
            string.delete(string.lastIndexOf(", "), string.lastIndexOf(", ") + 2);
        }
        return string.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RequiredResourceSet)) return false;

        RequiredResourceSet otherResourceSet = (RequiredResourceSet) o;

        return requiredResources.equals(otherResourceSet.requiredResources);
    }

    public Map<ResourceType, Integer> getRequiredResources() {
        return requiredResources;
    }

    public void setRequiredResources(Map<ResourceType, Integer> requiredResources) {
        this.requiredResources = new HashMap<>(requiredResources);
    }

    public int getRequiredAmount(ResourceType resourceType) {
        return requiredResources.getOrDefault(resourceType, 0);
    }

    public void setRequiredAmount(ResourceType resourceType, int requirement) {
        requiredResources.put(resourceType, requirement);
    }
}
