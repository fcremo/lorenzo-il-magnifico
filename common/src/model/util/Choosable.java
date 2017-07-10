package model.util;

public interface Choosable<T> {
    String getShortDescriptionForChoosing();

    default T getSelf() {
        return (T) this;
    }
}
