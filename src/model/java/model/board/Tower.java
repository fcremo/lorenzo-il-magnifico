package model.board;

import model.board.actionspace.Floor;
import model.card.development.DevelopmentCard;
import model.player.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the state of one tower
 */
public class Tower<T extends DevelopmentCard> {
    private ArrayList<Floor<T>> floors = new ArrayList<>();

    /**
     * Sets the floors
     */
    public void setFloors(List<Floor<T>> floors) {
        // TODO: 5/9/17 perform checks (e.g. for the correct number of floors)
        throw new NotImplementedException();
    }

    /**
     * Returns the card on the specified floor
     *
     * @param floor the floor
     * @returns the card
     */
    public T getCard(int floor) {
        // TODO: 5/9/17
        throw new NotImplementedException();
    }

    public void setCards(List<T> developmentCards) {
        // TODO: 4/13/17
        throw new NotImplementedException();
    }

    public boolean isOccupiedBy(Player player) {
        // TODO: 4/13/17
        throw new NotImplementedException();
    }

    public boolean isOccupied() {
        // TODO: 4/13/17
        throw new NotImplementedException();
    }
}
