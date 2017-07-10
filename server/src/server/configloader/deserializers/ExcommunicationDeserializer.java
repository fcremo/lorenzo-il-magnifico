package server.configloader.deserializers;

import com.google.gson.*;
import model.Excommunication;
import model.card.effects.EffectsContainer;

import java.lang.reflect.Type;

/**
 * This class is responsible for deserializing excommunications
 */
public class ExcommunicationDeserializer implements JsonDeserializer<Excommunication> {
    @Override
    public Excommunication deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonExcommunicationObject = json.getAsJsonObject();

        int period = jsonExcommunicationObject.get("period").getAsInt();

        EffectsContainer effects = context.deserialize(jsonExcommunicationObject.getAsJsonArray("effects"), EffectsContainer.class);

        Excommunication excommunication = new Excommunication(period);
        excommunication.setEffectsContainer(effects);

        return excommunication;
    }
}
