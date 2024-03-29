package server.configloader.deserializers;

import com.google.gson.*;
import model.card.development.BuildingCard;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import model.util.Tuple;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BuildingCardDeserializer implements JsonDeserializer<BuildingCard> {
    @Override
    public BuildingCard deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject buildingCardJsonObject = json.getAsJsonObject();

        String name = buildingCardJsonObject.get("name").getAsString();
        int period = buildingCardJsonObject.get("period").getAsInt();

        JsonObject requiredResourceSetJsonObject = buildingCardJsonObject.getAsJsonObject("requiredResourceSet");
        RequiredResourceSet requiredResourceSet = new RequiredResourceSetDeserializer().deserialize(requiredResourceSetJsonObject, RequiredResourceSet.class, context);

        ArrayList<Tuple<RequiredResourceSet, ObtainableResourceSet>> productions = new ArrayList<>();
        JsonArray productionsJsonArray = buildingCardJsonObject.getAsJsonArray("productions");
        for (JsonElement o : productionsJsonArray) {
            JsonObject priceJsonObject = o.getAsJsonObject().getAsJsonObject("price");
            RequiredResourceSet price = new RequiredResourceSetDeserializer().deserialize(priceJsonObject, RequiredResourceSet.class, context);

            JsonObject obtainedResourceSetJsonObject = o.getAsJsonObject().getAsJsonObject("resourceSet");
            ObtainableResourceSet obtainableResourceSet = new ObtainableResourceSetDeserializer().deserialize(obtainedResourceSetJsonObject, ObtainableResourceSet.class, context);

            productions.add(new Tuple<>(price, obtainableResourceSet));
        }


        int requiredValueForProduction = buildingCardJsonObject.get("requiredValueForProduction").getAsInt();

        return new BuildingCard(name, requiredResourceSet, period, productions, requiredValueForProduction);
    }
}
