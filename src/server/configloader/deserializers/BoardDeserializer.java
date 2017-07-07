package server.configloader.deserializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.board.Board;
import model.board.Tower;
import model.board.actionspace.*;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.resource.ObtainableResourceSet;

import java.lang.reflect.Type;

/**
 * This class is responsible for deserializing a board object
 */
public class BoardDeserializer implements JsonDeserializer<Board> {
    @Override
    public Board deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonBoardObject = json.getAsJsonObject();

        /* ---------------------------------------------------------
         * Step 1: Deserialize towers
         * --------------------------------------------------------- */
        Tower greenTower = new TowerDeserializer().deserialize(jsonBoardObject.get("greenTower"), new TypeToken<Tower<TerritoryCard>>() {
        }.getType(), context);
        Tower blueTower = new TowerDeserializer().deserialize(jsonBoardObject.get("blueTower"), new TypeToken<Tower<CharacterCard>>() {
        }.getType(), context);
        Tower yellowTower = new TowerDeserializer().deserialize(jsonBoardObject.get("yellowTower"), new TypeToken<Tower<BuildingCard>>() {
        }.getType(), context);
        Tower purpleTower = new TowerDeserializer().deserialize(jsonBoardObject.get("purpleTower"), new TypeToken<Tower<VentureCard>>() {
        }.getType(), context);

        /* ---------------------------------------------------------
         * Step 2: Deserialize the other action spaces
         * --------------------------------------------------------- */
        ObtainableResourceSet councilPalaceBonus = new ObtainableResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("councilPalace"), ObtainableResourceSet.class, context);
        ActionSpace councilPalace = new CouncilPalace(councilPalaceBonus, 1, "palace");

        ObtainableResourceSet smallProductionAreaBonus = new ObtainableResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("smallProductionArea"), ObtainableResourceSet.class, context);
        ActionSpace smallProductionArea = new SmallProductionArea(smallProductionAreaBonus, 1, "small-production");
        ObtainableResourceSet smallHarvestAreaBonus = new ObtainableResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("smallHarvestArea"), ObtainableResourceSet.class, context);
        ActionSpace smallHarvestArea = new SmallHarvestArea(smallHarvestAreaBonus, 1, "small-harvest");

        ObtainableResourceSet bigProductionAreaBonus = new ObtainableResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("bigProductionArea"), ObtainableResourceSet.class, context);
        ActionSpace bigProductionArea = new BigProductionArea(bigProductionAreaBonus, 1, "big-production");
        ObtainableResourceSet bigHarvestAreaBonus = new ObtainableResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("bigHarvestArea"), ObtainableResourceSet.class, context);
        ActionSpace bigHarvestArea = new BigHarvestArea(bigHarvestAreaBonus, 1, "big-harvest");

        ObtainableResourceSet market1Bonus = new ObtainableResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("market1"), ObtainableResourceSet.class, context);
        ActionSpace market1 = new MarketActionSpace(market1Bonus, 1, "market1");
        ObtainableResourceSet market2Bonus = new ObtainableResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("market2"), ObtainableResourceSet.class, context);
        ActionSpace market2 = new MarketActionSpace(market2Bonus, 1, "market2");
        ObtainableResourceSet market3Bonus = new ObtainableResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("market3"), ObtainableResourceSet.class, context);
        ActionSpace market3 = new MarketActionSpace(market3Bonus, 1, "market3");
        ObtainableResourceSet market4Bonus = new ObtainableResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("market4"), ObtainableResourceSet.class, context);
        ActionSpace market4 = new MarketActionSpace(market4Bonus, 1, "market4");

        /* ---------------------------------------------------------
         * Step 3: Deserialize the faith track bonuses
         * --------------------------------------------------------- */
        ObtainableResourceSet[] faithTrackBonuses = context.deserialize(jsonBoardObject.getAsJsonArray("faithTrackBonus"), ObtainableResourceSet[].class);

        /* ---------------------------------------------------------
         * Step 4: Set the attributes of the board
         * --------------------------------------------------------- */
        Board board = new Board(null);
        board.setTerritoryTower(greenTower);
        board.setCharacterTower(blueTower);
        board.setBuildingTower(yellowTower);
        board.setVentureTower(purpleTower);
        board.setCouncilPalace(councilPalace);
        board.setSmallProductionArea(smallProductionArea);
        board.setSmallHarvestArea(smallHarvestArea);
        board.setBigHarvestArea(bigHarvestArea);
        board.setBigProductionArea(bigProductionArea);
        board.setMarket1(market1);
        board.setMarket2(market2);
        board.setMarket3(market3);
        board.setMarket4(market4);
        board.setFaithTrackBonus(faithTrackBonuses);

        return board;

    }


}
