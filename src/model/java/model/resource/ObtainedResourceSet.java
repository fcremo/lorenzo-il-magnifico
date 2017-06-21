package model.resource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents an obtained/obtainable set of resources
 */
public class ObtainedResourceSet implements Serializable {
    /**
     * The set of static resources
     */
    private HashMap<ObtainableResource, Integer> resources = new HashMap<>();
    /**
     * The set of multiplied resources.
     * The player gets an ObtainedResourceSet for each RequiredResourceSet he/she has
     */
    private HashMap<RequiredResourceSet, ObtainedResourceSet> resourceMultipliers = new HashMap<>();

    public ObtainedResourceSet() {
    }

    public ObtainedResourceSet(HashMap<ObtainableResource, Integer> resources) {
        this.resources = resources;
    }

    public ObtainedResourceSet(HashMap<ObtainableResource, Integer> resources, HashMap<RequiredResourceSet, ObtainedResourceSet> resourceMultipliers) {
        this.resources = resources;
        this.resourceMultipliers = resourceMultipliers;
    }

    /**
     * Copy constructor
     *
     * @param obtainedResourceSet
     */
    public ObtainedResourceSet(ObtainedResourceSet obtainedResourceSet) {
        this.resources = new HashMap<>(obtainedResourceSet.resources);
        // TODO: 5/25/17 this works until nothing modifies multipliers. Implement proper deep cloning.
        this.resourceMultipliers = new HashMap<>(obtainedResourceSet.resourceMultipliers);
    }

    public ObtainedResourceSet(int gold, int wood, int stone, int servants, int councilPrivileges, int militaryPoints,
                               int faithPoints, int victoryPoints) {
        resources.put(ObtainableResource.GOLD, gold);
        resources.put(ObtainableResource.WOOD, wood);
        resources.put(ObtainableResource.STONE, stone);
        resources.put(ObtainableResource.SERVANTS, servants);
        resources.put(ObtainableResource.COUNCIL_PRIVILEGES, councilPrivileges);
        resources.put(ObtainableResource.MILITARY_POINTS, militaryPoints);
        resources.put(ObtainableResource.FAITH_POINTS, faithPoints);
        resources.put(ObtainableResource.VICTORY_POINTS, victoryPoints);
    }

    /**
     * Add qty resources of type resource to the current set and returns a new one.
     * N.B: qty can be negative, but the stored quantity will never go negative.
     * N.B: this method returns a deep copy of the current object, it does not modify it
     *
     * @param resource the type of resource you want to modify
     * @param qty      the quantity of the resource
     */
    public ObtainedResourceSet addResource(ObtainableResource resource, int qty) {
        ObtainedResourceSet newResourceSet = new ObtainedResourceSet(this);
        int currentQty = resources.getOrDefault(resource, 0);
        if (currentQty + qty >= 0) {
            newResourceSet.resources.put(resource, currentQty + qty);
        }
        return newResourceSet;
    }

    public boolean isEmpty() {
        return (resources.isEmpty() && resourceMultipliers.isEmpty());
    }

    @Override
    public String toString() {
        StringBuilder printable = new StringBuilder();
        for (ObtainableResource resource : resources.keySet()) {
            printable.append(resources.get(resource).toString())
                     .append(" ")
                     .append(resource.toString())
                     .append(", ");
        }
        for (RequiredResourceSet required : resourceMultipliers.keySet()) {
            printable.append(resourceMultipliers.get(required).toString())
                     .append(" --> ")
                     .append(required.toString())
                     .append(", ");
        }
        if (printable.lastIndexOf(", ") != -1) {
            printable.delete(printable.lastIndexOf(", "), printable.lastIndexOf(", ") + 2);
        }
        return printable.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ObtainedResourceSet)) return false;

        ObtainedResourceSet otherResourceSet = (ObtainedResourceSet) o;

        // Ensure static resources are equal
        Set<ObtainableResource> allResources = new HashSet<>(resources.keySet());
        allResources.addAll(otherResourceSet.resources.keySet());
        for (ObtainableResource r : allResources) {
            if (resources.getOrDefault(r, 0) != otherResourceSet.resources.getOrDefault(r, 0)) {
                return false;
            }
        }

        // Check multipliers
        return this.resourceMultipliers.equals(otherResourceSet.resourceMultipliers);
    }
}
