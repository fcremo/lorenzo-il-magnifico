package model.resource;

import java.io.Serializable;

public enum RequiredResource implements ResourceType, Serializable {
    REQUIRED_MILITARY_POINTS, REQUIRED_FAITH_POINTS, REQUIRED_VICTORY_POINTS,
    REQUIRED_GOLD, REQUIRED_WOOD, REQUIRED_STONE, REQUIRED_SERVANTS,
    REQUIRED_BUILDING_CARDS, REQUIRED_CHARACTER_CARDS, REQUIRED_TERRITORY_CARDS, REQUIRED_VENTURE_CARDS,
    REQUIRED_SAME_TYPE_CARDS,;

    @Override
    public String toString() {
        return this.name();
    }
}
