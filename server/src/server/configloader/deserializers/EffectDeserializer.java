package server.configloader.deserializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.action.ActionType;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.MarketActionSpace;
import model.card.development.*;
import model.card.effects.*;
import model.card.effects.interfaces.EffectInterface;
import model.player.FamilyMemberColor;
import model.resource.ObtainableResource;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EffectDeserializer implements JsonDeserializer<EffectInterface> {
    @Override
    public EffectInterface deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonEffect = json.getAsJsonObject();
        String effectType = jsonEffect.get("effectType").getAsString();

        EffectInterface effect;

        // TODO: 5/25/17 check if it's possible to use reflection instead of a switch-case construct
        // Curly braces are used to create a new scope for variables declared in each case
        switch (effectType) {
            case "ImmediateResources": {
                ObtainableResourceSet obtainableResourceSet = context.deserialize(jsonEffect.getAsJsonObject("resources"), ObtainableResourceSet.class);
                effect = new ImmediateResourcesEffect(obtainableResourceSet);
                break;
            }
            case "ActionValueModifier": {
                ActionType actionType = context.deserialize(jsonEffect.getAsJsonPrimitive("actionType"), ActionType.class);
                int value = jsonEffect.getAsJsonPrimitive("value").getAsInt();
                effect = new ActionValueModifierEffect(actionType, value);
                break;
            }
            case "FloorBonusResourcesSetter": {
                ObtainableResourceSet obtainableResourceSet = context.deserialize(jsonEffect.getAsJsonObject("resources"), ObtainableResourceSet.class);
                effect = new FloorBonusResourcesSetterEffect(obtainableResourceSet);
                break;
            }
            case "ImmediateAction": {
                ArrayList<ActionType> actionTypes;
                if (jsonEffect.get("actionType").isJsonArray()) {
                    actionTypes = context.deserialize(jsonEffect.getAsJsonArray("actionType"), new TypeToken<ArrayList<ActionType>>() {
                    }.getType());
                }
                else {
                    actionTypes = new ArrayList<>();
                    actionTypes.add(context.deserialize(jsonEffect.get("actionType"), ActionType.class));
                }
                int actionValue = jsonEffect.get("value").getAsInt();

                effect = new ImmediateActionEffect(actionTypes, actionValue);
                break;
            }
            case "ImmediateActionWithDiscounts": {
                ArrayList<ActionType> actionTypes;
                if (jsonEffect.get("actionType").isJsonArray()) {
                    actionTypes = context.deserialize(jsonEffect.getAsJsonArray("actionType"), new TypeToken<ArrayList<ActionType>>() {
                    }.getType());
                }
                else {
                    actionTypes = new ArrayList<>();
                    actionTypes.add(context.deserialize(jsonEffect.get("actionType"), ActionType.class));
                }
                int actionValue = jsonEffect.get("value").getAsInt();

                ArrayList<RequiredResourceSet> discounts = context.deserialize(jsonEffect.getAsJsonArray("discounts"), new TypeToken<ArrayList<RequiredResourceSet>>() {
                }.getType());

                effect = new ImmediateActionWithDiscountsEffect(actionTypes, actionValue, discounts);
                break;
            }
            case "EndOfGameResources": {
                ObtainableResourceSet obtainableResourceSet = context.deserialize(jsonEffect.get("resources"), ObtainableResourceSet.class);
                effect = new EndOfGameResourcesEffect(obtainableResourceSet);
                break;
            }
            case "DoubleOccupationCostIgnore": {
                effect = new DoubleOccupationCostIgnoreEffect();
                break;
            }
            case "ChurchSupportBonusResources": {
                ObtainableResourceSet obtainableResourceSet = context.deserialize(jsonEffect.get("resources"), ObtainableResourceSet.class);
                effect = new ChurchSupportBonusResourcesEffect(obtainableResourceSet);
                break;
            }
            case "CopyOtherLeaderAbility": {
                effect = new CopyOtherLeaderAbilityEffect();
                break;
            }
            case "DevelopmentCardImmediateResourcesMultiplier": {
                ObtainableResource resourceType = context.deserialize(jsonEffect.get("resourceType"), ObtainableResource.class);
                int multiplier = jsonEffect.get("multiplier").getAsInt();
                effect = new DevelopmentCardImmediateResourcesMultiplierEffect(multiplier, resourceType);
                break;
            }
            case "DevelopmentCardRequiredResourceSetModifier": {
                RequiredResourceSet resources = context.deserialize(jsonEffect.get("resources"), RequiredResourceSet.class);
                String cardType = jsonEffect.get("cardType").getAsString();
                Class<? extends DevelopmentCard> cardClass;
                switch (cardType) {
                    case "BUILDING":
                        cardClass = BuildingCard.class;
                        break;
                    case "CHARACTER":
                        cardClass = CharacterCard.class;
                        break;
                    case "TERRITORY":
                        cardClass = TerritoryCard.class;
                        break;
                    case "VENTURE":
                        cardClass = VentureCard.class;
                        break;
                    default:
                        throw new JsonParseException(String.format("Development card type %s not supported", cardType));
                }
                effect = new DevelopmentCardRequiredResourceSetModifierEffect(resources, cardClass);
                break;
            }
            case "FamilyMemberValueModifier": {
                int modifier = jsonEffect.get("value").getAsInt();
                FamilyMemberColor familyMemberColor = context.deserialize(jsonEffect.get("familyMemberColor"), FamilyMemberColor.class);
                effect = new FamilyMemberValueModifierEffect(familyMemberColor, modifier);
                break;
            }
            case "FamilyMemberValueSetter": {
                int value = jsonEffect.get("value").getAsInt();
                FamilyMemberColor familyMemberColor = context.deserialize(jsonEffect.get("familyMemberColor"), FamilyMemberColor.class);
                effect = new FamilyMemberValueSetterEffect(familyMemberColor, value);
                break;
            }
            case "OncePerRoundAction": {
                ActionType actionType = context.deserialize(jsonEffect.get("actionType"), ActionType.class);
                int value = jsonEffect.getAsJsonPrimitive("value").getAsInt();
                effect = new OncePerRoundActionEffect(actionType, value);
                break;
            }
            case "OncePerRoundBonusResources": {
                ObtainableResourceSet obtainableResourceSet = context.deserialize(jsonEffect.get("resources"), ObtainableResourceSet.class);
                effect = new OncePerRoundBonusResourcesEffect(obtainableResourceSet);
                break;
            }
            case "OncePerRoundFamilyMemberValueSetter": {
                ArrayList<FamilyMemberColor> allowedFamilyMemberColors = context.deserialize(jsonEffect.getAsJsonArray("allowedFamilyMemberColor"), new TypeToken<ArrayList<FamilyMemberColor>>() {
                }.getType());
                int value = jsonEffect.get("value").getAsInt();
                effect = new OncePerRoundFamilyMemberValueSetterEffect(allowedFamilyMemberColors, value);
                break;
            }
            case "SkipMilitaryPointsRequirement": {
                effect = new SkipMilitaryPointsRequirementEffect();
                break;
            }
            case "SkipOccupationChecks": {
                effect = new SkipOccupationCheckEffect();
                break;
            }
            case "SkipFirstTurn": {
                effect = new SkipFirstTurnEffect();
                break;
            }
            case "InhibitActionSpace": {
                String actionSpaceType = jsonEffect.get("actionSpace").getAsString();
                Class<? extends ActionSpace> actionSpaceClass;
                // TODO: implement other action space types
                switch (actionSpaceType) {
                    case "Market":
                        actionSpaceClass = MarketActionSpace.class;
                        break;
                    default:
                        throw new JsonParseException("ActionSpace type not supported");
                }
                effect = new InhibitActionSpaceEffect(actionSpaceClass);
                break;
            }
            case "ServantsValueMultiplier": {
                double multiplier = jsonEffect.get("multiplier").getAsDouble();
                effect = new ServantsValueMultiplierEffect(multiplier);
                break;
            }
            case "IgnoreEndOfGameVictoryPointsFromDevelopmentCards": {
                String developmentCardType = jsonEffect.get("cardType").getAsString();
                Class<? extends DevelopmentCard> developmentCardClass;
                switch (developmentCardType) {
                    case "VENTURE":
                        developmentCardClass = VentureCard.class;
                        break;
                    case "CHARACTER":
                        developmentCardClass = CharacterCard.class;
                        break;
                    case "TERRITORY":
                        developmentCardClass = TerritoryCard.class;
                        break;
                    default:
                        throw new JsonParseException("Card type not supported");
                }
                effect = new IgnoreEndOfGameVictoryPointsFromDevelopmentCardsEffect(developmentCardClass);
                break;
            }
            case "EndOfGameBuildingCardCostPenalty": {
                effect = new EndOfGameBuildingCardCostPenaltyEffect();
                break;
            }
            case "ObtainedResourceSetModifier": {
                ObtainableResource resource = context.deserialize(jsonEffect.get("resource"), ObtainableResource.class);
                int qty = jsonEffect.get("quantity").getAsInt();
                effect = new ObtainableResourceSetModifierEffect(resource, qty);
                break;
            }
            default:
                throw new JsonParseException(effectType + " effect type not recognized!");
        }
        return effect;
    }
}
