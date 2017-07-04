package server.configloader.deserializers;

import com.google.gson.*;
import model.board.Tower;
import model.board.actionspace.Floor;
import model.card.development.DevelopmentCard;
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

        ArrayList<Floor> floors = new ArrayList<>();
        for (int i = 0; i < obtainedResourceSets.size(); i++) {
            Floor floor = new Floor(obtainedResourceSets.get(i), i * 2 + 1, tower, null);
            floors.add(floor);
        }
        tower.setFloors(floors);

        return tower;
    }
}
