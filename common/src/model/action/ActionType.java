package model.action;

public enum ActionType {
    TAKE_ANY_CARD("TAKE ANY CARD"),
    TAKE_BUILDING_CARD("TAKE BUILDING CARD"),
    TAKE_TERRITORY_CARD("TAKE TERRITORY CARD"),
    TAKE_CHARACTER_CARD("TAKE CHARACTER CARD"),
    TAKE_VENTURE_CARD("TAKE VENTURE CARD"),
    PRODUCTION("PRODUCTION"),
    HARVEST("HARVEST");

    String name;

    ActionType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
