package server.configloader.deserializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Game;
import model.board.Board;
import model.board.Tower;
import model.card.effects.EffectsContainer;
import model.card.effects.interfaces.EffectInterface;
import model.player.bonustile.PersonalBonusTile;
import model.resource.ObtainedResourceSet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

/**
 * This is the helper class used for loading configuration from json files
 */
public class ConfigLoader {
    private String configFilePath;

    private Game game;

    public ConfigLoader(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public void loadConfiguration() throws FileNotFoundException {
        InputStreamReader configFileReader = new InputStreamReader(new FileInputStream(configFilePath));
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Game.class, new GameDeserializer())
                .registerTypeAdapter(Board.class, new BoardDeserializer())
                .registerTypeAdapter(Tower.class, new TowerDeserializer())
                .registerTypeAdapter(ObtainedResourceSet.class, new ObtainedResourceSetDeserializer())
                .registerTypeAdapter(PersonalBonusTile.class, new PersonalBonusTileDeserializer())
                .registerTypeAdapter(EffectsContainer.class, new EffectsContainerDeserializer())
                .registerTypeAdapter(EffectInterface.class, new EffectDeserializer())
                .create();

        game = gson.fromJson(configFileReader, Game.class);
    }


    public String getConfigFilePath() {
        return configFilePath;
    }

    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public Game getGame() {
        return game;
    }
}
