package model.player;

import java.io.Serializable;

public enum FamilyMemberColor implements Serializable {
    BLACK, WHITE, ORANGE, NEUTRAL;

    @Override
    public String toString() {
        return this.name();
    }
}
