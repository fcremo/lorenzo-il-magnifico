package model.resource;

/**
 * This enum represents the various council privileges a player can get
 */
public enum CouncilPrivilege {
    STONE_WOOD(new ObtainedResourceSet(0, 1, 1, 0, 0, 0, 0, 0)),
    SERVANTS(new ObtainedResourceSet(0, 0, 0, 2, 0, 0, 0, 0)),
    GOLD(new ObtainedResourceSet(2, 0, 0, 0, 0, 0, 0, 0)),
    MILITARY_POINTS(new ObtainedResourceSet(0, 0, 0, 0, 0, 2, 0, 0)),
    FAITH_POINT(new ObtainedResourceSet(0, 0, 0, 0, 0, 0, 1, 0));

    private ObtainedResourceSet resourceSet;

    CouncilPrivilege(ObtainedResourceSet resourceSet) {
        this.resourceSet = resourceSet;
    }

    public ObtainedResourceSet getResourceSet() {
        return resourceSet;
    }
}
