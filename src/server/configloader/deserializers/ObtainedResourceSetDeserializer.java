package server.configloader.deserializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.resource.ObtainableResource;
import model.resource.ObtainedResourceSet;
import model.resource.RequiredResourceSet;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * This class is responsible for deserializing an obtained resource set.
 * <p>
 * Having this custom deserializer allows a more compact representation of
 * the resource sets that don't have resources that get multiplied,
 * by letting you specify the static resources in the root of the object.
 * <p>
 * Example: a resource set that does not have multipliers is represented like this:
 * {"GOLD": 1} instead of {"resources": {"GOLD": 1}}.
 * A resource set that has (also) multipliers is represented like this:
 * {"GOLD": 1, "multipliers": {"requirement": {"buildings": 1}, "resources": {"GOLD": 1}}}
 */
public class ObtainedResourceSetDeserializer implements JsonDeserializer<ObtainedResourceSet> {
    @Override
    public ObtainedResourceSet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // The json we're deserializing
        JsonObject jsonObtainedResourceSet = json.getAsJsonObject();

        /* ----------------------------------------------------------------------------
         * Step 1: deserialize "static" resources, the ones that don't get multiplied
         * They are represented as a HashMap<ObtainableResource, Integer>
         * ----------------------------------------------------------------------------
         */

        /*
         * Create an intermediate object that
         * can be deserialized automatically as a HashMap<ObtainableResource, Integer>
         */
        JsonObject jsonStaticResourcesObject = new JsonObject();

        /*
         * Cycle on the types of static resources
        */
        for (ObtainableResource obtainableResource : ObtainableResource.values()) {
            // Get the number of resources of the current type
            JsonElement jsonObtainedResource = jsonObtainedResourceSet.get(obtainableResource.name());
            if (jsonObtainedResource != null) {
                int nObtained = jsonObtainedResource.getAsInt();
                jsonStaticResourcesObject.addProperty(obtainableResource.name(), nObtained);
            }
        }

        Type staticResourcesType = new TypeToken<HashMap<ObtainableResource, Integer>>() {
        }.getType();
        HashMap<ObtainableResource, Integer> staticResources = context.deserialize(jsonStaticResourcesObject, staticResourcesType);

        /* ----------------------------------------------------------------------------
         * Step 2: deserialize resources with multipliers
         * They are represented as a HashMap<RequiredResourceSet, Tuple<ObtainedResourceSet, Integer>>,
         * meaning the player gets Integer ObtainedResourceSets for each RequiredResourceSet he/she has
         * ----------------------------------------------------------------------------
         */
        HashMap<RequiredResourceSet, ObtainedResourceSet> multiplierResources = new HashMap<>();
        JsonArray jsonMultiplierResourcesArray = jsonObtainedResourceSet.getAsJsonArray("multipliers");
        if (jsonMultiplierResourcesArray != null) {
            for (JsonElement jsonMultiplier : jsonMultiplierResourcesArray) {
                JsonObject jsonRequirements = jsonMultiplier.getAsJsonObject().getAsJsonObject("requirements");
                JsonObject jsonResources = jsonMultiplier.getAsJsonObject().getAsJsonObject("resources");

                RequiredResourceSet requirements = context.deserialize(jsonRequirements, RequiredResourceSet.class);
                ObtainedResourceSet resources = context.deserialize(jsonResources, ObtainedResourceSet.class);

                multiplierResources.put(requirements, resources);
            }
        }

        return new ObtainedResourceSet(staticResources, multiplierResources);
    }
}
