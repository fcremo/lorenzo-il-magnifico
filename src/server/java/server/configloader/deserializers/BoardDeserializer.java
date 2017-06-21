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
import model.resource.ObtainedResourceSet;

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
        ObtainedResourceSet councilPalaceBonus = new ObtainedResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("councilPalace"), ObtainedResourceSet.class, context);
        ActionSpace councilPalace = new CouncilPalace(councilPalaceBonus, 1);

        ObtainedResourceSet smallProductionAreaBonus = new ObtainedResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("smallProductionArea"), ObtainedResourceSet.class, context);
        ActionSpace smallProductionArea = new SmallProductionHarvestArea(smallProductionAreaBonus, 1);
        ObtainedResourceSet smallHarvestAreaBonus = new ObtainedResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("smallHarvestArea"), ObtainedResourceSet.class, context);
        ActionSpace smallHarvestArea = new SmallProductionHarvestArea(smallHarvestAreaBonus, 1);

        ObtainedResourceSet bigProductionAreaBonus = new ObtainedResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("bigProductionArea"), ObtainedResourceSet.class, context);
        ActionSpace bigProductionArea = new BigProductionHarvestArea(bigProductionAreaBonus, 1);
        ObtainedResourceSet bigHarvestAreaBonus = new ObtainedResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("bigHarvestArea"), ObtainedResourceSet.class, context);
        ActionSpace bigHarvestArea = new BigProductionHarvestArea(bigHarvestAreaBonus, 1);

        ObtainedResourceSet marketGoldBonus = new ObtainedResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("marketGold"), ObtainedResourceSet.class, context);
        ActionSpace marketGold = new MarketActionSpace(marketGoldBonus, 1);
        ObtainedResourceSet marketServantsBonus = new ObtainedResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("marketServants"), ObtainedResourceSet.class, context);
        ActionSpace marketServants = new MarketActionSpace(marketServantsBonus, 1);
        ObtainedResourceSet marketMilitaryAndGoldBonus = new ObtainedResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("marketMilitaryAndGold"), ObtainedResourceSet.class, context);
        ActionSpace marketMilitaryAndGold = new MarketActionSpace(marketMilitaryAndGoldBonus, 1);
        ObtainedResourceSet marketCouncilPrivilegesBonus = new ObtainedResourceSetDeserializer().deserialize(jsonBoardObject.getAsJsonObject("marketCouncilPrivileges"), ObtainedResourceSet.class, context);
        ActionSpace marketCouncilPrivileges = new MarketActionSpace(marketCouncilPrivilegesBonus, 1);

        /* ---------------------------------------------------------
         * Step 3: Deserialize the faith track bonuses
         * --------------------------------------------------------- */
        ObtainedResourceSet[] faithTrackBonuses = context.deserialize(jsonBoardObject.getAsJsonArray("faithTrackBonus"), ObtainedResourceSet[].class);

        /* ---------------------------------------------------------
         * Step 4: Set the attributes of the board
         * --------------------------------------------------------- */
        Board board = new Board(null);
        board.setGreenTower(greenTower);
        board.setBlueTower(blueTower);
        board.setYellowTower(yellowTower);
        board.setPurpleTower(purpleTower);
        board.setCouncilPalace(councilPalace);
        board.setSmallProductionArea(smallProductionArea);
        board.setSmallHarvestArea(smallHarvestArea);
        board.setBigHarvestArea(bigHarvestArea);
        board.setBigProductionArea(bigProductionArea);
        board.setMarketGold(marketGold);
        board.setMarketServants(marketServants);
        board.setMarketMilitaryAndGold(marketMilitaryAndGold);
        board.setMarketCouncilPrivileges(marketCouncilPrivileges);
        board.setFaithTrackBonus(faithTrackBonuses);

        return board;

    }


}
