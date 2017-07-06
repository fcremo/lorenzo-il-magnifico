package model.resource;

import java.io.Serializable;
import java.util.*;

/**
 * This class represents an obtainable set of obtainedResources
 */
public class ObtainableResourceSet implements Serializable {
    /**
     * The set of static resources
     */
    private Map<ObtainableResource, Integer> obtainedResources = new HashMap<>();

    /**
     * The set of multiplied obtainedResources.
     * The player gets an ObtainableResourceSet for each RequiredResourceSet he/she has
     */
    private HashMap<RequiredResourceSet, ObtainableResourceSet> resourceMultipliers = new HashMap<>();

    public ObtainableResourceSet(Map<ObtainableResource, Integer> obtainedResources) {
        this.obtainedResources = new HashMap<>(obtainedResources);
    }

    public ObtainableResourceSet(Map<ObtainableResource, Integer> obtainedResources, Map<RequiredResourceSet, ObtainableResourceSet> resourceMultipliers) {
        this.obtainedResources = new HashMap<>(obtainedResources);
        this.resourceMultipliers = new HashMap<>(resourceMultipliers);
    }

    /**
     * Empty constructor
     */
    public ObtainableResourceSet() {}

    /**
     * Copy constructor
     * @param obtainableResourceSet
     */
    public ObtainableResourceSet(ObtainableResourceSet obtainableResourceSet) {
        this.obtainedResources = new HashMap<>(obtainableResourceSet.obtainedResources);
        // TODO: 5/25/17 this works until nothing modifies multipliers. Implement proper deep cloning.
        this.resourceMultipliers = new HashMap<>(obtainableResourceSet.resourceMultipliers);
    }

    public ObtainableResourceSet(int gold, int wood, int stone, int servants, int councilPrivileges, int militaryPoints,
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

    public boolean isEmpty() {
        return (obtainedResources.isEmpty() && resourceMultipliers.isEmpty());
    }

    @Override
    public String toString() {
        StringBuilder printable = new StringBuilder();
        for (Map.Entry<ObtainableResource, Integer> resource : obtainedResources.entrySet()) {
            printable.append(resource.getValue())
                     .append(" ")
                     .append(resource.getKey())
                     .append(", ");
        }
        for (Map.Entry<RequiredResourceSet, ObtainableResourceSet> required : resourceMultipliers.entrySet()) {
            printable.append(required.getKey())
                     .append(" --> ")
                     .append(required.getValue())
                     .append(", ");
        }
        if (printable.lastIndexOf(", ") != -1) {
            printable.delete(printable.lastIndexOf(", "), printable.lastIndexOf(", ") + 2);
        }
        return printable.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ObtainableResourceSet)) return false;

        ObtainableResourceSet otherResourceSet = (ObtainableResourceSet) o;

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

    public Map<ObtainableResource, Integer> getObtainedResources() {
        return obtainedResources;
    }

    public void setObtainedResources(Map<ObtainableResource, Integer> obtainedResources) {
        this.obtainedResources = new HashMap<>(obtainedResources);
    }

    public Map<RequiredResourceSet, ObtainableResourceSet> getResourceMultipliers() {
        return resourceMultipliers;
    }

    public void setResourceMultipliers(Map<RequiredResourceSet, ObtainableResourceSet> resourceMultipliers) {
        // TODO: use deep cloning so that once set the originating parameter can be modified by the caller
        this.resourceMultipliers = new HashMap<>(resourceMultipliers);
    }

    public int getObtainedAmount(ResourceType resourceType) {
        return obtainedResources.getOrDefault(resourceType, 0);
    }

    public void setObtainedAmount(ObtainableResource resourceType, int requirement) {
        obtainedResources.put(resourceType, requirement);
    }

    /**
     * Adds some quantity of a resource to this set
     * @param resource
     * @param qty
     */
    public void addResource(ObtainableResource resource, int qty) {
        int newQty = obtainedResources.getOrDefault(resource, 0) + qty;
        obtainedResources.put(resource, newQty);
    }

    /**
     * Subtracts some quantity of a resource from the set
     * @param resource
     * @param qty
     */
    public void subtractResource(ObtainableResource resource, int qty) {
        int newQty = obtainedResources.getOrDefault(resource, 0) - qty;
        obtainedResources.put(resource, newQty);
    }

    /**
     * Adds an obtainable resource set to this set
     * @param resources
     */
    public void addResources(ObtainableResourceSet resources) {
        // Add static resources
        for(ObtainableResource obtainableResource : resources.getObtainedResources().keySet()) {
            addResource(obtainableResource, resources.getObtainedAmount(obtainableResource));
        }

        // Add multipliers
        for(Map.Entry<RequiredResourceSet, ObtainableResourceSet> multiplier : resources.getResourceMultipliers().entrySet()) {
            this.resourceMultipliers.put(multiplier.getKey(), multiplier.getValue());
        }
    }

    /**
     * Subtracts some resources from this resource set
     * @param resources
     */
    public void subtractResources(RequiredResourceSet resources) {
        // Subtract static resources
        for(ResourceType resourceType : resources.getRequiredResources().keySet()) {
            if(resourceType instanceof ObtainableResource){
                ObtainableResource obtainableResource = (ObtainableResource) resourceType;
                subtractResource(obtainableResource, resources.getRequiredAmount(obtainableResource));
            }
        }

        // TODO: Remove matching multipliers
    }
}
