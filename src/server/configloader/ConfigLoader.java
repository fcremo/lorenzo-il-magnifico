package server.configloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import model.Excommunication;
import model.Game;
import model.board.Board;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.card.effects.EffectsContainer;
import model.card.effects.interfaces.EffectInterface;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import server.configloader.deserializers.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * This is the helper class used for loading configuration from json files
 */
public class ConfigLoader {
    private String configDirectory;

    private int gameStartTimeout;
    private Game game;

    public ConfigLoader(String configDirectory) {
        this.configDirectory = configDirectory;
    }

    public void loadConfiguration() throws IOException {
        loadTimeouts();

        game = new Game();

        Gson gson = new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Board.class, new BoardDeserializer())
                .registerTypeAdapter(ObtainableResourceSet.class, new ObtainedResourceSetDeserializer())
                .registerTypeAdapter(RequiredResourceSet.class, new RequiredResourceSetDeserializer())
                .registerTypeAdapter(BuildingCard.class, new BuildingCardDeserializer())
                .registerTypeAdapter(EffectsContainer.class, new EffectsContainerDeserializer())
                .registerTypeAdapter(EffectInterface.class, new EffectDeserializer())
                .create();

        /* -----------------------------------------------------------
         * Step 1: Deserialize personal bonus tiles
         * ----------------------------------------------------------- */
        InputStreamReader bonusTilesFileReader = new InputStreamReader(new FileInputStream(configDirectory + "/bonusTiles.json"));
        ArrayList<PersonalBonusTile> personalBonusTiles = gson.fromJson(bonusTilesFileReader, new TypeToken<ArrayList<PersonalBonusTile>>() {
        }.getType());
        game.setAvailablePersonalBonusTiles(personalBonusTiles);
        bonusTilesFileReader.close();


        /* -----------------------------------------------------------
         * Step 2: Deserialize development cards
         * ----------------------------------------------------------- */
        InputStreamReader territoryCardsFileReader = new InputStreamReader(new FileInputStream(configDirectory + "/territoryCards.json"));
        ArrayList<TerritoryCard> territoryCards = gson.fromJson(territoryCardsFileReader, new TypeToken<ArrayList<TerritoryCard>>() {
        }.getType());
        territoryCardsFileReader.close();

        InputStreamReader buildingCardsFileReader = new InputStreamReader(new FileInputStream(configDirectory + "/buildingCards.json"));
        ArrayList<BuildingCard> buildingCards = gson.fromJson(buildingCardsFileReader, new TypeToken<ArrayList<BuildingCard>>() {
        }.getType());
        buildingCardsFileReader.close();

        InputStreamReader characterCardsFileReader = new InputStreamReader(new FileInputStream(configDirectory + "/characterCards.json"));
        ArrayList<CharacterCard> characterCards = gson.fromJson(characterCardsFileReader, new TypeToken<ArrayList<CharacterCard>>() {
        }.getType());
        characterCardsFileReader.close();

        InputStreamReader ventureCardsFileReader = new InputStreamReader(new FileInputStream(configDirectory + "/ventureCards.json"));
        ArrayList<VentureCard> ventureCards = gson.fromJson(ventureCardsFileReader, new TypeToken<ArrayList<VentureCard>>() {
        }.getType());
        ventureCardsFileReader.close();

        game.setAvailableTerritoryCards(territoryCards);
        game.setAvailableBuildingCards(buildingCards);
        game.setAvailableCharacterCards(characterCards);
        game.setAvailableVentureCards(ventureCards);

        /* -----------------------------------------------------------
         * Step 3: Deserialize leader cards
         * ----------------------------------------------------------- */
        InputStreamReader leaderCardsFileReader = new InputStreamReader(new FileInputStream(configDirectory + "/leaderCards.json"));
        ArrayList<LeaderCard> leaderCards = gson.fromJson(leaderCardsFileReader, new TypeToken<ArrayList<LeaderCard>>() {
        }.getType());
        game.setAvailableLeaderCards(leaderCards);

        /* -----------------------------------------------------------
         * Step 4: Deserialize excommunications
         * ----------------------------------------------------------- */
        InputStreamReader excommunicationsFileReader = new InputStreamReader(new FileInputStream(configDirectory + "/excommunications.json"));
        ArrayList<Excommunication> excommunications = gson.fromJson(excommunicationsFileReader, new TypeToken<ArrayList<Excommunication>>() {
        }.getType());
        game.setAvailableExcommunications(excommunications);

        /* -----------------------------------------------------------
         * Step 5: Deserialize council privileges
         * ----------------------------------------------------------- */
        InputStreamReader councilPrivilegesFileReader = new InputStreamReader(new FileInputStream(configDirectory + "/councilPrivileges.json"));
        ArrayList<ObtainableResourceSet> councilPrivileges = gson.fromJson(councilPrivilegesFileReader, new TypeToken<ArrayList<ObtainableResourceSet>>() {
        }.getType());
        game.setCouncilPrivileges(councilPrivileges);

        /* -----------------------------------------------------------
         * Step 6: Deserialize game board
         * ----------------------------------------------------------- */
        InputStreamReader boardFileReader = new InputStreamReader(new FileInputStream(configDirectory + "/board.json"));
        Board board = gson.fromJson(boardFileReader, Board.class);
        game.setBoard(board);
    }

    private void loadTimeouts() throws IOException {
        InputStreamReader timeoutFileReader = new InputStreamReader(new FileInputStream(configDirectory + "/timeouts.json"));
        JsonReader reader = new JsonReader(timeoutFileReader);
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            String val = reader.nextString();
            if ("startTimeout".equals(key)) {
                this.gameStartTimeout = Integer.parseInt(val) * 1000;
            }
        }
        reader.close();
        timeoutFileReader.close();
    }

    public int getGameStartTimeout() {
        return gameStartTimeout;
    }

    public Game getGame() {
        return game;
    }
}
