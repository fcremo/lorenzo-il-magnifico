package model.player;

public enum FamilyMemberColor {
    BLACK, WHITE, ORANGE, NEUTRAL;

    @Override
    public String toString() {
        return this.name();
    }
}
