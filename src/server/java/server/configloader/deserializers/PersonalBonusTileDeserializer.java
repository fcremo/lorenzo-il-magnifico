package server.configloader.deserializers;

import com.google.gson.*;
import model.player.bonustile.PersonalBonusTile;
import model.resource.ObtainedResourceSet;

import java.lang.reflect.Type;

public class PersonalBonusTileDeserializer implements JsonDeserializer<PersonalBonusTile> {
    @Override
    public PersonalBonusTile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonPersonalBonusTileObject = json.getAsJsonObject();
        ObtainedResourceSet obtainedResourceSet = new ObtainedResourceSetDeserializer().deserialize(jsonPersonalBonusTileObject.getAsJsonObject("production"), ObtainedResourceSet.class, context);
        return new PersonalBonusTile(obtainedResourceSet, null);
    }

}
