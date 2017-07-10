package model.resource;

import model.player.Player;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a set of resources obtained by a player
 */
public class ObtainedResourceSet implements Serializable {
    /**
     * The player owning this resource set.
     * Needed when working with multipliers and requirements
     */
    private Player player;

    private Map<ObtainableResource, Integer> resources = new HashMap<>();

    public ObtainedResourceSet(Player player) {
        this.player = player;
    }

    public ObtainedResourceSet(Player player, Map<ObtainableResource, Integer> resources) {
        this.player = player;
        this.resources = new HashMap<>(resources);
    }

    /**
     * Clone constructor
     * Creates a new clone of the original object
     *
     * @param otherObtainedResourceSet
     */
    public ObtainedResourceSet(ObtainedResourceSet otherObtainedResourceSet) {
        this.player = otherObtainedResourceSet.player;
        this.resources = new HashMap<>(otherObtainedResourceSet.resources);
    }

    public int getAmount (ObtainableResource resource) {
        return resources.getOrDefault(resource, 0);
    }

    /**
     * Adds some quantity of a resource to the set
     * @param resource
     * @param qty
     */
    public void addResource(ObtainableResource resource, int qty) {
        if(resource.equals(ObtainableResource.COUNCIL_PRIVILEGES)) return;

        int newQty = resources.getOrDefault(resource, 0) + qty;
        resources.put(resource, newQty);
    }

    /**
     * Subtracts some quantity of a resource from the set
     * @param resource
     * @param qty
     */
    public void subtractResource(ObtainableResource resource, int qty) {
        if(resource.equals(ObtainableResource.COUNCIL_PRIVILEGES)) return;

        int newQty = resources.getOrDefault(resource, 0) - qty;
        resources.put(resource, newQty);
    }

    /**
     * Returns true if the set has at least the required quantity of the specified resource
     * @param qty
     * @param resource
     * @return true if the set has at least the required quantity of the specified resource
     */
    public boolean hasAtLeast(int qty, ObtainableResource resource) {
        return resources.getOrDefault(resource, 0) >= qty;
    }

    /**
     * Returns true if the set can be used to pay for the required resource set
     * @param requirement
     * @return
     */
    public boolean has(RequiredResourceSet requirement) {
        return divideBy(requirement) > 0;
    }

    /**
     * Adds an obtainable resource set to the current set
     * @param resources
     */
    public void addResources(ObtainableResourceSet resources) {
        // Copy of the original resources needed to perform calculations on the multipliers
        Map<ObtainableResource, Integer> originalResources = new HashMap<>(this.resources);

        // Add static resources
        addStaticResources(resources, 1);

        // For each RequiredResourceSet in the current ObtainedResourceSet we add the corresponding ObtainableResourceSet
        for(Map.Entry<RequiredResourceSet, ObtainableResourceSet> multiplier : resources.getResourceMultipliers().entrySet()) {
            int qtyToAdd = new ObtainedResourceSet(player, originalResources).divideBy(multiplier.getKey());
            ObtainableResourceSet obtainableResourceSet = multiplier.getValue();

            /*
             * Adding only static resources as multipliers covers any sane game configuration,
             * as nesting multipliers probably does not make much sense.
             * TODO: modify game configuration loader so that it is semantically impossible to nest multipliers
             */
            addStaticResources(obtainableResourceSet, qtyToAdd);
        }
    }

    /**
     * Add only the static resources in an obtainable resource set to this resource set
     * @param obtainableResourceSet
     */
    private void addStaticResources(ObtainableResourceSet obtainableResourceSet, int qtyToAdd) {
        for (ObtainableResource obtainableResource : obtainableResourceSet.getObtainedResources().keySet()) {
            if(obtainableResource.equals(ObtainableResource.COUNCIL_PRIVILEGES)) continue;
            addResource(obtainableResource, obtainableResourceSet.getObtainedAmount(obtainableResource) * qtyToAdd);
        }
    }

    /**
     * Sets a resource quantity
     * @param resource
     * @param qty
     */
    public void setResourceQty(ObtainableResource resource, int qty) {
        resources.put(resource, 0);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (Map.Entry<ObtainableResource, Integer> resource : resources.entrySet()) {
            string.append(resource.getValue())
                     .append(" ")
                     .append(resource.getKey())
                     .append(", ");
        }

        if (string.lastIndexOf(", ") != -1) {
            string.delete(string.lastIndexOf(", "), string.lastIndexOf(", ") + 2);
        }
        return string.toString();
    }

    /**
     * Subtracts some resources from this resource set
     * @param resources
     */
    public void subtractResources(RequiredResourceSet resources) {
        for(ResourceType resourceType : resources.getRequiredResources().keySet()){
            // We don't subtract requirements, only costs
            if(resourceType instanceof ObtainableResource) {
                ObtainableResource resource = (ObtainableResource) resourceType;
                if(resource.equals(ObtainableResource.COUNCIL_PRIVILEGES)) continue;
                int newQty = this.resources.getOrDefault(resource, 0) - resources.getRequiredAmount(resource);
                this.resources.put(resource, newQty);
            }
        }
    }

    /**
     * Computes how many times the requirement can be fulfilled by this set
     * @param requirement
     * @return
     */
    private int divideBy(RequiredResourceSet requirement) {
        int quotient = Integer.MAX_VALUE;

        for(ResourceType requiredResource : requirement.getRequiredResources().keySet()){
            int tmpQuotient = divideBy(requiredResource, requirement.getRequiredAmount(requiredResource));
            if (tmpQuotient < quotient) {
                quotient = tmpQuotient;
            }
        }
        return quotient;
    }

    /**
     * Computes how many times the requirement can be fulfilled by this set
     * @param requirement
     * @param qty
     * @return
     */
    private int divideBy(ResourceType requirement, int qty) {
        int resourceQty;

        // If the resource is just requirement we need to translate to the corresponding resource
        if(requirement instanceof RequiredResource) {
            RequiredResource requiredResourceType = (RequiredResource) requirement;
            if (requiredResourceType == RequiredResource.REQUIRED_MILITARY_POINTS) {
                resourceQty = resources.getOrDefault(ObtainableResource.MILITARY_POINTS, 0);
            }
            else if (requiredResourceType == RequiredResource.REQUIRED_FAITH_POINTS) {
                resourceQty = resources.getOrDefault(ObtainableResource.FAITH_POINTS, 0);
            }
            else if (requiredResourceType == RequiredResource.REQUIRED_VICTORY_POINTS) {
                resourceQty = resources.getOrDefault(ObtainableResource.VICTORY_POINTS, 0);
            }
            else if (requiredResourceType == RequiredResource.REQUIRED_GOLD) {
                resourceQty = resources.getOrDefault(ObtainableResource.GOLD, 0);
            }
            else if (requiredResourceType == RequiredResource.REQUIRED_WOOD) {
                resourceQty = resources.getOrDefault(ObtainableResource.WOOD, 0);
            }
            else if (requiredResourceType == RequiredResource.REQUIRED_STONE) {
                resourceQty = resources.getOrDefault(ObtainableResource.STONE, 0);
            }
            else if (requiredResourceType == RequiredResource.REQUIRED_SERVANTS) {
                resourceQty = resources.getOrDefault(ObtainableResource.SERVANTS, 0);
            }
            else if (requiredResourceType == RequiredResource.REQUIRED_BUILDING_CARDS) {
                resourceQty = player.getBuildings().size();
            }
            else if (requiredResourceType == RequiredResource.REQUIRED_CHARACTER_CARDS) {
                resourceQty = player.getCharacters().size();
            }
            else if (requiredResourceType == RequiredResource.REQUIRED_TERRITORY_CARDS) {
                resourceQty = player.getTerritories().size();
            }
            else if (requiredResourceType == RequiredResource.REQUIRED_VENTURE_CARDS) {
                resourceQty = player.getVentures().size();
            }
            else if (requiredResourceType == RequiredResource.REQUIRED_SAME_TYPE_CARDS) {
                resourceQty = Arrays.asList(player.getBuildings().size(),
                        player.getCharacters().size(),
                        player.getTerritories().size(),
                        player.getVentures().size())
                                    .stream()
                                    .max(Comparator.naturalOrder())
                                    .orElse(0);
            }
            else {
                resourceQty = 0;
            }
        }
        else {
            resourceQty = resources.getOrDefault(requirement, 0);
        }

        return resourceQty / qty;
    }


}
