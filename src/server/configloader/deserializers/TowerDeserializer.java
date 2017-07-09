package server.configloader.deserializers;

import com.google.gson.*;
import model.board.Tower;
import model.board.actionspace.Floor;
import model.card.development.DevelopmentCard;
import model.resource.ObtainableResourceSet;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TowerDeserializer <T extends DevelopmentCard> implements JsonDeserializer<Tower<T>> {
    @Override
    public Tower<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // The object we will return
        Tower<T> tower = new Tower<>();

        // The object we're deserializing
        JsonArray jsonTowerArray = json.getAsJsonArray();

        // Get a list of objects from a list of elements
        ArrayList<ObtainableResourceSet> obtainableResourceSets = new ArrayList<>();
        for (JsonElement e : jsonTowerArray) {
            obtainableResourceSets.add(new ObtainableResourceSetDeserializer().deserialize(e, ObtainableResourceSet.class, context));
        }

        ArrayList<Floor<T>> floors = new ArrayList<>();
        for (int i = 0; i < obtainableResourceSets.size(); i++) {
            Floor<T> floor = new Floor<>(obtainableResourceSets.get(i), i * 2 + 1, tower, null);
            floors.add(floor);
        }
        tower.setFloors(floors);

        return tower;
    }
}
