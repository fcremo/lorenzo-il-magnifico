package model.resource;

import java.io.Serializable;

public enum ObtainableResource implements ResourceType, Serializable {
    GOLD, WOOD, STONE, SERVANTS, COUNCIL_PRIVILEGES,
    MILITARY_POINTS, FAITH_POINTS, VICTORY_POINTS,;

    @Override
    public String toString() {
        return this.name();
    }
}
