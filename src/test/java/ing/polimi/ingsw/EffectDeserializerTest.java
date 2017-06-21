package ing.polimi.ingsw;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.board.Board;
import model.card.development.BuildingCard;
import model.card.effects.EffectsContainer;
import model.card.effects.ImmediateResourcesEffect;
import model.card.effects.ObtainedResourceSetModifierEffect;
import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainedResourceSet;
import model.resource.RequiredResourceSet;
import org.junit.BeforeClass;
import org.junit.Test;
import server.configloader.deserializers.*;

import static org.junit.Assert.assertTrue;


public class EffectDeserializerTest {
    private static Gson deserializer;

    @BeforeClass
    public static void setupObjects() {
        deserializer = new GsonBuilder()
                .registerTypeAdapter(EffectInterface.class, new EffectDeserializer())
                .registerTypeAdapter(ObtainedResourceSet.class, new ObtainedResourceSetDeserializer())
                .registerTypeAdapter(RequiredResourceSet.class, new RequiredResourceSetDeserializer())
                .create();
    }

    @Test
    public void testImmediateResourcesEffectDeserialization() {
        final String testCase = "{\"effectType\": \"ImmediateResources\", \"resources\": {\"VICTORY_POINTS\": 5}}";

        Gson gson = new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Board.class, new BoardDeserializer())
                .registerTypeAdapter(ObtainedResourceSet.class, new ObtainedResourceSetDeserializer())
                .registerTypeAdapter(RequiredResourceSet.class, new RequiredResourceSetDeserializer())
                .registerTypeAdapter(BuildingCard.class, new BuildingCardDeserializer())
                .registerTypeAdapter(EffectsContainer.class, new EffectsContainerDeserializer())
                .registerTypeAdapter(EffectInterface.class, new EffectDeserializer())
                .create();

        ImmediateResourcesEffect effect = deserializeEffect(testCase);
        assertTrue(effect.getObtainedResourceSet().equals(new ObtainedResourceSet(0, 0, 0, 0, 0, 0, 0, 5)));
    }


    @Test
    public void testObtainedResourceSetDeserialization() {
        final String testCase = "{\"effectType\": \"ObtainedResourceSetModifier\", \"resourceType\": \"MILITARY_POINTS\", \"quantity\": -1}";
        ObtainedResourceSetModifierEffect effect = deserializeEffect(testCase);

        // TODO: test something
    }

    /**
     * Tries to deserialize the provided string as a certain kind of effect
     *
     * @param serializedEffect
     * @param <T               extends EffectInterface> the kind of effect that should be deserialized
     * @return
     */
    private <T extends EffectInterface> T deserializeEffect(String serializedEffect) {
        EffectInterface deserializedEffect = deserializer.fromJson(serializedEffect, EffectInterface.class);

        return (T) deserializedEffect;
    }
}
