package model.resource;

public enum RequiredResource implements ResourceType {
    REQUIRED_MILITYARY_POINTS, REQUIRED_FAITH_POINTS, REQUIRED_VICTORY_POINTS,
    REQUIRED_GOLD, REQUIRED_WOOD, REQUIRED_STONE,
    REQUIRED_BUILDINGS, REQUIRED_CHARACTERS, REQUIRED_TERRITORIES, REQUIRED_VENTURES,
    REQUIRED_SAME_TYPE_CARDS,;

    @Override
    public String toString() {
        return this.name();
    }
}
