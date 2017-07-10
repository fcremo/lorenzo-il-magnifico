package ui.cli.contexts;

import model.util.Choosable;

public class ChoosableItem<T> implements Choosable<T> {
    private String shortDescription;
    private T item;

    public ChoosableItem(String shortDescription, T item) {
        this.shortDescription = shortDescription;
        this.item = item;
    }

    @Override
    public String getShortDescriptionForChoosing() {
        return shortDescription;
    }

    @Override
    public T getSelf() {
        return item;
    }
}