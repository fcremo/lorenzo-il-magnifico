package server.configloader.deserializers;

import com.google.gson.*;
import model.card.effects.EffectsContainer;
import model.card.effects.interfaces.EffectInterface;

import java.lang.reflect.Type;

public class EffectsContainerDeserializer implements JsonDeserializer<EffectsContainer> {
    @Override
    public EffectsContainer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray effectsJsonArray = json.getAsJsonArray();

        EffectDeserializer effectDeserializer = new EffectDeserializer();

        EffectsContainer container = new EffectsContainer();

        for (JsonElement e : effectsJsonArray) {
            container.addEffect(effectDeserializer.deserialize(e, EffectInterface.class, context));
        }

        return container;
    }
}
