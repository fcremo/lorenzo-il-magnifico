package ing.polimi.ingsw;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.card.effects.ObtainedResourceModifier;
import model.card.effects.interfaces.EffectInterface;
import org.junit.Test;
import server.configloader.deserializers.EffectDeserializer;

import static org.junit.Assert.*;


public class EffectDeserializerTest {
    @Test
    public void testObtainedResourceSetDeserialization(){
        // TODO: 5/26/17 move test cases to separate files
        final String testCase = "{\"effectType\": \"ObtainedResourceSetModifier\", \"resourceType\": \"MILITARY_POINTS\", \"quantity\": -1}";
        Gson gson = new GsonBuilder().registerTypeAdapter(EffectInterface.class, new EffectDeserializer()).create();
        EffectInterface effect = gson.fromJson(testCase, EffectInterface.class);

        assertTrue(ObtainedResourceModifier.class.isAssignableFrom(effect.getClass()));
    }
}
