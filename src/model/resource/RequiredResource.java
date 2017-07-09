package model.resource;

import java.io.Serializable;

public enum RequiredResource implements ResourceType, Serializable {
    REQUIRED_MILITARY_POINTS("MILITARY POINTS"),
    REQUIRED_FAITH_POINTS("FAITH POINTS"),
    REQUIRED_VICTORY_POINTS("VICTORY POINTS"),
    REQUIRED_GOLD("GOLD"),
    REQUIRED_WOOD("WOOD"),
    REQUIRED_STONE("STONE"),
    REQUIRED_SERVANTS("SERVANTS"),
    REQUIRED_BUILDING_CARDS("BUILDING CARDS"),
    REQUIRED_CHARACTER_CARDS("CHARACTER CARDS"),
    REQUIRED_TERRITORY_CARDS("TERRITORY CARDS"),
    REQUIRED_VENTURE_CARDS("VENTURE CARDS"),
    REQUIRED_SAME_TYPE_CARDS("SAME TYPE CARDS");

    String name;

    RequiredResource(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
