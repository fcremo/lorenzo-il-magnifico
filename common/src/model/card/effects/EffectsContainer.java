package model.card.effects;

import model.card.effects.interfaces.EffectInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the data structure and accessor methods for storing effects.
 * It's handy because it allows to do something like this:
 * List<SomeEffectInterface> = effectsContainer.getEffectsImplementing(SomeEffectInterface);
 * and be sure that the returned objects will implement the given interface.
 * This way we can hide the the use of the isAssignableFrom() method and provide a nice interface for getting only
 * relevant effects from a card.
 * There's also a method for retrieving instances of a specific effect class, should it be needed.
 */
public class EffectsContainer implements Serializable {
    private ArrayList<EffectInterface> effects = new ArrayList<>();

    /**
     * Add an effect to the container
     *
     * @param effect the effect to add
     * @param <T>    the interface for the effect
     */
    public <T extends EffectInterface> void addEffect(T effect) {
        effects.add(effect);
    }

    /**
     * Returns all the effects
     * @return
     */
    public List<EffectInterface> getEffects() {
        return effects;
    }

    /**
     * Get all effects implementing a given interface, directly or indirectly.
     * That means that getEffectsImplementing(OncePerRoundEffectInterface.class) will return all effects which
     * can be activated once per round.
     *
     * @param effectInterface
     * @param <T>
     * @return
     */
    public <T extends EffectInterface> List<T> getEffectsImplementing(Class<T> effectInterface) {
        ArrayList<T> returnList = new ArrayList<>();
        for (EffectInterface effect : effects) {
            if (effectInterface.isAssignableFrom(effect.getClass())) {
                returnList.add((T) effect);
            }
        }
        return returnList;
    }

    /**
     * Get all the effects that are an instance of *exactly* the class provided.
     * Warning: if you pass an interface to this method you will always get an empty list,
     * as interfaces can't be instantiated.
     * You probably want to use the {@code getEffectsImplementing} method instead.
     * Example usage: effectsContainer.getInstancesOf(OncePerRoundGetResources.class)
     *
     * @param effectClass the class of the effects you want to retrieve
     * @param <T>         the class of the effects you want to retrieve
     * @return a list of effects that are instances of the class T (effectClass)
     */
    public <T extends EffectInterface> List<T> getInstancesOf(Class<T> effectClass) {
        ArrayList<T> returnList = new ArrayList<>();
        for (EffectInterface elem : effects) {
            if (elem.getClass() == effectClass) {
                returnList.add((T) elem);
            }
        }
        return returnList;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        for (EffectInterface effect : effects) {
            string.append(effect);
            string.append("\n");
            string.append("and ");
        }

        if (string.lastIndexOf("and ") != -1) {
            string.delete(string.lastIndexOf("and "), string.lastIndexOf("and ") + 4);
        }

        return string.toString();
    }
}
