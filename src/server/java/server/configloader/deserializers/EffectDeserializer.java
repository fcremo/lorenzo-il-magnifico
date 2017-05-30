package server.configloader.deserializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.action.ActionType;
import model.card.effects.ActionValueModifierEffect;
import model.card.effects.ImmediateResourcesEffect;
import model.card.effects.ObtainedResourceModifier;
import model.card.effects.OncePerTurnActionEffect;
import model.card.effects.interfaces.EffectInterface;
import model.card.effects.interfaces.EndOfGameResourcesEffect;
import model.resource.ObtainableResource;
import model.resource.ObtainedResourceSet;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EffectDeserializer implements JsonDeserializer<EffectInterface> {
    @Override
    public EffectInterface deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonEffect = json.getAsJsonObject();
        String effectType = jsonEffect.get("effectType").getAsString();

        EffectInterface effect;

        // TODO: 5/25/17 check if it's possible to use reflection instead of a switch-case construct
        // Curly braces are needed to create a new scope for variables declared in each case
        switch (effectType){
            case "ObtainedResourceSetModifier": {
                ObtainableResource resource = context.deserialize(jsonEffect.get("resource"), ObtainableResource.class);
                int qty = jsonEffect.get("quantity").getAsInt();
                effect = new ObtainedResourceModifier(resource, qty);
                break;
            }
            case "ImmediateResources": {
                ArrayList<ObtainedResourceSet> obtainedResourceSets = context.deserialize(jsonEffect.get("resources"), new TypeToken<ArrayList<ObtainedResourceSet>>() {
                }.getType());
                effect = new ImmediateResourcesEffect(obtainedResourceSets);
                break;
            }
            case "ActionValueModifier": {
                ActionType actionType = context.deserialize(jsonEffect.getAsJsonPrimitive("actionType"), ActionType.class);
                int value = jsonEffect.getAsJsonPrimitive("value").getAsInt();
                effect = new ActionValueModifierEffect(actionType, value);
                break;
            }
            case "EndOfGameResources": {
                ObtainedResourceSet obtainedResourceSet = context.deserialize(jsonEffect.get("resources"), ObtainedResourceSet.class);
                effect = new EndOfGameResourcesEffect(obtainedResourceSet);
                break;
            }
            case "OncePerTurnAction": {
                ActionType actionType = context.deserialize(jsonEffect.getAsJsonPrimitive("actionType"), ActionType.class);
                int value = jsonEffect.getAsJsonPrimitive("value").getAsInt();
                effect = new OncePerTurnActionEffect(actionType, value);
                break;
            }
            default:
                throw new JsonParseException(effectType + " effect type not recognized!");
        }
        return effect;
    }
}
