package server.configloader.deserializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.board.Tower;
import model.board.actionspace.Floor;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.DevelopmentCard;
import model.card.development.TerritoryCard;
import model.resource.ObtainedResourceSet;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TowerDeserializer implements JsonDeserializer<Tower> {
    @Override
    public Tower<? extends DevelopmentCard> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // The object we will return
        Tower tower = new Tower<>();

        // The object we're deserializing
        JsonArray jsonTowerArray = json.getAsJsonArray();

        // Get a list of objects from a list of elements
        ArrayList<ObtainedResourceSet> obtainedResourceSets = new ArrayList<>();
        for (JsonElement e : jsonTowerArray) {
            obtainedResourceSets.add(new ObtainedResourceSetDeserializer().deserialize(e, ObtainedResourceSet.class, context));
        }

        // Get the prefix for the id
        String prefix = "";
        if(typeOfT.equals(new TypeToken<Tower<TerritoryCard>>(){}.getType())) prefix = "tower-territory-";
        else if(typeOfT.equals(new TypeToken<Tower<CharacterCard>>(){}.getType())) prefix = "tower-character-";
        else if(typeOfT.equals(new TypeToken<Tower<BuildingCard>>(){}.getType())) prefix = "tower-building-";
        else prefix = "tower-venture-";

        ArrayList<Floor> floors = new ArrayList<>();
        for (int i = 0; i < obtainedResourceSets.size(); i++) {
            Floor floor = new Floor(obtainedResourceSets.get(i), i * 2 + 1, tower, null, prefix + Integer.toString(i));
            floors.add(floor);
        }
        tower.setFloors(floors);

        return tower;
    }
}
