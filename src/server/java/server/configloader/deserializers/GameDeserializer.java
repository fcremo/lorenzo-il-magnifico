package server.configloader.deserializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.Excommunication;
import model.Game;
import model.board.Board;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.card.leader.LeaderCard;
import model.player.bonustile.PersonalBonusTile;
import model.resource.ObtainedResourceSet;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * This class is responsible for deserializing a game object containing the game configuration
 */
public class GameDeserializer implements JsonDeserializer<Game> {
    @Override
    public Game deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // This is the object that will be returned
        Game game = new Game();

        JsonObject jsonGameObject = json.getAsJsonObject();

        /* -----------------------------------------------------------
         * Step 1: Deserialize personal bonus tiles
         * ----------------------------------------------------------- */
        // Get and deserialize the personal bonus tiles
        ArrayList<PersonalBonusTile> personalBonusTiles = context.deserialize(jsonGameObject.getAsJsonArray("personalBonusTile"), new TypeToken<ArrayList<PersonalBonusTile>>(){}.getType());
        game.setAvailablePersonalBonusTiles(personalBonusTiles);

        /* -----------------------------------------------------------
         * Step 2: Deserialize development cards
         * ----------------------------------------------------------- */
        JsonObject jsonDevelopmentCardsObject = jsonGameObject.getAsJsonObject("developmentCards");
        JsonArray jsonTerritoryCardsArray = jsonDevelopmentCardsObject.getAsJsonArray("territoryCards");
        JsonArray jsonBuildingCardsArray = jsonDevelopmentCardsObject.getAsJsonArray("buildingCards");
        JsonArray jsonCharacterCardsArray = jsonDevelopmentCardsObject.getAsJsonArray("characterCards");
        JsonArray jsonVentureCardsArray = jsonDevelopmentCardsObject.getAsJsonArray("ventureCards");

        ArrayList<TerritoryCard> territoryCards = context.deserialize(jsonTerritoryCardsArray, new TypeToken<ArrayList<TerritoryCard>>(){}.getType());
        ArrayList<BuildingCard> buildingCards = context.deserialize(jsonBuildingCardsArray, new TypeToken<ArrayList<BuildingCard>>(){}.getType());
        ArrayList<CharacterCard> characterCards = context.deserialize(jsonCharacterCardsArray, new TypeToken<ArrayList<CharacterCard>>(){}.getType());
        ArrayList<VentureCard> ventureCards = context.deserialize(jsonVentureCardsArray, new TypeToken<ArrayList<VentureCard>>(){}.getType());

        game.setAvailableTerritoryCards(territoryCards);
        game.setAvailableBuildingCards(buildingCards);
        game.setAvailableCharacterCards(characterCards);
        game.setAvailableVentureCards(ventureCards);

        /* -----------------------------------------------------------
         * Step 3: Deserialize leader cards
         * ----------------------------------------------------------- */
        JsonArray jsonLeaderCardsArray = jsonGameObject.getAsJsonArray("leaderCards");
        ArrayList<LeaderCard> leaderCards = context.deserialize(jsonLeaderCardsArray, new TypeToken<ArrayList<LeaderCard>>(){}.getType());
        game.setAvailableLeaderCards(leaderCards);

        /* -----------------------------------------------------------
         * Step 4: Deserialize excommunications
         * ----------------------------------------------------------- */
        JsonArray jsonExcommunicationsArray = jsonGameObject.getAsJsonArray("excommunications");
        ArrayList<Excommunication> excommunications = context.deserialize(jsonExcommunicationsArray, new TypeToken<ArrayList<Excommunication>>(){}.getType());
        game.setAvailableExcommunications(excommunications);

        /* -----------------------------------------------------------
         * Step 5: Deserialize council privileges
         * ----------------------------------------------------------- */
        JsonArray jsonCouncilPrivilegesArray = jsonGameObject.getAsJsonArray("councilPrivileges");
        ArrayList<ObtainedResourceSet> councilPrivileges = context.deserialize(jsonCouncilPrivilegesArray, new TypeToken<ArrayList<ObtainedResourceSet>>(){}.getType());
        game.setCouncilPrivileges(councilPrivileges);

        /* -----------------------------------------------------------
         * Step 6: Deserialize game board
         * ----------------------------------------------------------- */
        JsonObject jsonBoard = jsonGameObject.getAsJsonObject("board");
        Board board = context.deserialize(jsonBoard, Board.class);
        game.setBoard(board);

        return game;
    }
}
