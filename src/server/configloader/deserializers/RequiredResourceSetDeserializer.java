package server.configloader.deserializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.resource.ObtainableResource;
import model.resource.RequiredResource;
import model.resource.RequiredResourceSet;
import model.resource.ResourceType;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * This class is responsible for deserializing a required resource set.
 */
public class RequiredResourceSetDeserializer implements JsonDeserializer<RequiredResourceSet> {
    @Override
    public RequiredResourceSet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // The json we're deserializing
        JsonObject jsonObtainedResourceSet = json.getAsJsonObject();

        HashMap<ResourceType, Integer> resources = new HashMap<>();

        /*
         * Create an intermediate object that
         * can be deserialized automatically as a HashMap<ObtainableResource, Integer>
         */
        JsonObject jsonStaticResourcesObject = new JsonObject();

        /*
         * Cycle on the types of obtainable resources
        */
        for (ObtainableResource resourceType : ObtainableResource.values()) {
            // Get the number of resources of the current type
            JsonElement jsonObtainedResource = jsonObtainedResourceSet.get(resourceType.name());
            if (jsonObtainedResource != null) {
                int nObtained = jsonObtainedResource.getAsInt();
                jsonStaticResourcesObject.addProperty(resourceType.name(), nObtained);
            }
        }

        Type obtainableResourcesType = new TypeToken<HashMap<ObtainableResource, Integer>>() {
        }.getType();
        resources.putAll(context.deserialize(jsonStaticResourcesObject, obtainableResourcesType));


        /*
         * Create an intermediate object that
         * can be deserialized automatically as a HashMap<RequiredResource, Integer>
         */
        JsonObject jsonRequiredResourcesObject = new JsonObject();

        /*
         * Cycle on the types of required resources
        */
        for (RequiredResource resourceType : RequiredResource.values()) {
            // Get the number of resources of the current type
            JsonElement jsonObtainedResource = jsonObtainedResourceSet.get(resourceType.name());
            if (jsonObtainedResource != null) {
                int nObtained = jsonObtainedResource.getAsInt();
                jsonRequiredResourcesObject.addProperty(resourceType.name(), nObtained);
            }
        }

        Type requiredResourcesType = new TypeToken<HashMap<RequiredResource, Integer>>() {
        }.getType();
        resources.putAll(context.deserialize(jsonRequiredResourcesObject, requiredResourcesType));

        return new RequiredResourceSet(resources);
    }
}
