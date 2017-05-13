package model.resource;

import model.util.Tuple;

import java.util.HashMap;

public class ObtainedResourceSet {
    private HashMap<ObtainableResource, Integer> resources = new HashMap<>();
    private HashMap<RequiredResource, Tuple<ObtainableResource, Integer>> resourceMultipliers = new HashMap<>();

    public ObtainedResourceSet() {
    }

    public ObtainedResourceSet(HashMap<ObtainableResource, Integer> resources) {
        this.resources = resources;
    }

    public ObtainedResourceSet(int gold, int wood, int stone, int servants, int councilFavors, int militaryPoints,
                               int faithPoints, int victoryPoints) {
        resources.put(ObtainableResource.GOLD, gold);
        resources.put(ObtainableResource.WOOD, wood);
        resources.put(ObtainableResource.STONE, stone);
        resources.put(ObtainableResource.SERVANTS, servants);
        resources.put(ObtainableResource.COUNCIL_FAVORS, councilFavors);
        resources.put(ObtainableResource.MILITARY_POINTS, militaryPoints);
        resources.put(ObtainableResource.FAITH_POINTS, faithPoints);
        resources.put(ObtainableResource.VICTORY_POINTS, victoryPoints);
    }
}
