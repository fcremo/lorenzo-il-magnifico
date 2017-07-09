package model.resource;

import java.io.Serializable;

public enum ObtainableResource implements ResourceType, Serializable {
    GOLD("GOLD"),
    WOOD("WOOD"),
    STONE("STONE"),
    SERVANTS("SERVANTS"),
    COUNCIL_PRIVILEGES("COUNCIL PRIVILEGES"),
    MILITARY_POINTS("MILITARY POINTS"),
    FAITH_POINTS("FAITH POINTS"),
    VICTORY_POINTS("VICTORY POINTS");

    String name;

    ObtainableResource(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
