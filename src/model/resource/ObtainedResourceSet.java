package model.resource;

import java.io.Serializable;
import java.util.*;

/**
 * This class represents an obtained/obtainable set of obtainedResources
 */
public class ObtainedResourceSet implements Serializable {
    /**
     * The set of static obtainedResources
     */
    private HashMap<ObtainableResource, Integer> obtainedResources = new HashMap<>();

    /**
     * The set of multiplied obtainedResources.
     * The player gets an ObtainedResourceSet for each RequiredResourceSet he/she has
     */
    private HashMap<RequiredResourceSet, ObtainedResourceSet> resourceMultipliers = new HashMap<>();

    public ObtainedResourceSet(Map<ObtainableResource, Integer> obtainedResources) {
        this.obtainedResources = new HashMap<>(obtainedResources);
    }

    public ObtainedResourceSet(Map<ObtainableResource, Integer> obtainedResources, Map<RequiredResourceSet, ObtainedResourceSet> resourceMultipliers) {
        this.obtainedResources = new HashMap<>(obtainedResources);
        this.resourceMultipliers = new HashMap<>(resourceMultipliers);
    }

    /**
     * Empty constructor
     */
    public ObtainedResourceSet() {}

    /**
     * Copy constructor
     * @param obtainedResourceSet
     */
    public ObtainedResourceSet(ObtainedResourceSet obtainedResourceSet) {
        this.obtainedResources = new HashMap<>(obtainedResourceSet.obtainedResources);
        // TODO: 5/25/17 this works until nothing modifies multipliers. Implement proper deep cloning.
        this.resourceMultipliers = new HashMap<>(obtainedResourceSet.resourceMultipliers);
    }

    public ObtainedResourceSet(int gold, int wood, int stone, int servants, int councilPrivileges, int militaryPoints,
                               int faithPoints, int victoryPoints) {
        obtainedResources.put(ObtainableResource.GOLD, gold);
        obtainedResources.put(ObtainableResource.WOOD, wood);
        obtainedResources.put(ObtainableResource.STONE, stone);
        obtainedResources.put(ObtainableResource.SERVANTS, servants);
        obtainedResources.put(ObtainableResource.COUNCIL_PRIVILEGES, councilPrivileges);
        obtainedResources.put(ObtainableResource.MILITARY_POINTS, militaryPoints);
        obtainedResources.put(ObtainableResource.FAITH_POINTS, faithPoints);
        obtainedResources.put(ObtainableResource.VICTORY_POINTS, victoryPoints);
    }

    /**
     * Add qty obtainedResources of type resource to the current set and returns a new one.
     * N.B: qty can be negative, but the stored quantity will never go negative.
     * N.B: this method returns a deep copy of the current object, it does not modify it
     *
     * @param resource the type of resource you want to modify
     * @param qty      the quantity of the resource
     */
    public ObtainedResourceSet addResource(ObtainableResource resource, int qty) {
        ObtainedResourceSet newResourceSet = new ObtainedResourceSet(this);
        int currentQty = obtainedResources.getOrDefault(resource, 0);
        if (currentQty + qty >= 0) {
            newResourceSet.obtainedResources.put(resource, currentQty + qty);
        }
        return newResourceSet;
    }

    public boolean isEmpty() {
        return (obtainedResources.isEmpty() && resourceMultipliers.isEmpty());
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (Map.Entry<ObtainableResource, Integer> resource : obtainedResources.entrySet()) {
            string.append(resource.getValue())
                     .append(" ")
                     .append(resource.getKey())
                     .append(", ");
        }
        for (Map.Entry<RequiredResourceSet, ObtainedResourceSet> required : resourceMultipliers.entrySet()) {
            string.append(required.getValue())
                     .append(" for each ")
                     .append(required.getKey())
                     .append(", ");
        }
        if (string.lastIndexOf(", ") != -1) {
            string.delete(string.lastIndexOf(", "), string.lastIndexOf(", ") + 2);
        }
        return string.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ObtainedResourceSet)) return false;

        ObtainedResourceSet otherResourceSet = (ObtainedResourceSet) o;

        // Ensure static obtainedResources are equal
        Set<ObtainableResource> allResources = new HashSet<>(obtainedResources.keySet());
        allResources.addAll(otherResourceSet.obtainedResources.keySet());
        for (ObtainableResource r : allResources) {
            Integer qty1 = obtainedResources.getOrDefault(r, 0);
            Integer qty2 = otherResourceSet.obtainedResources.getOrDefault(r, 0);
            if (!qty1.equals(qty2)) {
                return false;
            }
        }

        // Check multipliers
        return this.resourceMultipliers.equals(otherResourceSet.resourceMultipliers);
    }

    public HashMap<ObtainableResource, Integer> getObtainedResources() {
        return obtainedResources;
    }

    public void setObtainedResources(HashMap<ObtainableResource, Integer> obtainedResources) {
        this.obtainedResources = obtainedResources;
    }

    public HashMap<RequiredResourceSet, ObtainedResourceSet> getResourceMultipliers() {
        return resourceMultipliers;
    }

    public void setResourceMultipliers(HashMap<RequiredResourceSet, ObtainedResourceSet> resourceMultipliers) {
        this.resourceMultipliers = resourceMultipliers;
    }

    public int getObtainedAmount(ResourceType resourceType) {
        return obtainedResources.getOrDefault(resourceType, 0);
    }

    public void setObtainedAmount(ObtainableResource resourceType, int requirement) {
        obtainedResources.put(resourceType, requirement);
    }

}
