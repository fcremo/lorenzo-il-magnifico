package model.action;

public enum ActionType {
    TAKE_ANY_CARD,
    TAKE_BUILDING_CARD,
    TAKE_TERRITORY_CARD,
    TAKE_CHARACTER_CARD,
    TAKE_VENTURE_CARD,
    MARKET,
    PRODUCTION,
    HARVEST;

    @Override
    public String toString() {
        return this.name();
    }
}
