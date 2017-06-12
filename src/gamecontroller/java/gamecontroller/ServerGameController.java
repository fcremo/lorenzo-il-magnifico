package gamecontroller;

import model.Excommunication;
import model.player.Player;
import model.player.PlayerColor;
import model.player.bonustile.PersonalBonusTile;
import server.GameRoom;
import server.ServerController;
import server.configloader.deserializers.ConfigLoader;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ServerGameController extends GameController {

    private GameRoom gameRoom;

    public ServerGameController(GameRoom gameRoom) {
        super(gameRoom);
        this.gameRoom = gameRoom;
    }

    public void startGame() {
        // Load game configuration from files
        loadConfiguration();
        // Create players and set colors
        PlayerColor colors[] = PlayerColor.values();
        int i = 0;
        for (ServerController controller : gameRoom.getControllers()) {
            Player player = new Player(controller.getUsername());

            // Set player color
            player.setColor(colors[i]);
            i++;

            this.getGame().addPlayer(player);
            controller.setPlayer(player);
        }

        // Extract excommunications
        drawExcommunications();

        // Set turn order
        Collections.shuffle(this.getGame().getPlayers());

        // Personal bonus tile draft
        for(int p = this.getGame().getPlayers().size()-1; p>=0; p--) {
            Player player = this.getGame().getPlayers().get(p);
            ServerController controller = gameRoom.getControllerForPlayer(player);
            PersonalBonusTile chosenPersonalBonusTile = controller.getClientConnection().choosePersonalBonusTile(this.getGame().getAvailablePersonalBonusTiles());
            // If the bonus tile chosen by the player is not available, the first available one is used
            if (!this.getGame().getAvailablePersonalBonusTiles().contains(chosenPersonalBonusTile)) {
                chosenPersonalBonusTile = this.getGame().getAvailablePersonalBonusTiles().get(0);
            }
            player.setBonusTile(chosenPersonalBonusTile);
            this.getGame().getAvailablePersonalBonusTiles().remove(chosenPersonalBonusTile);
        }

        // TODO: 5/30/17 pass configuration to clients and go on with the game preparation phase

    }

    /**
     * Load the game configuration from the files
     */
    public void loadConfiguration() {
        ConfigLoader configLoader = new ConfigLoader("configuration/test0.json");
        try {
            configLoader.loadConfiguration();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        super.setGame(configLoader.getGame());
    }

    public void drawExcommunications(){
        // First period
        List<Excommunication> firstPeriodExcommunications = getGame().getAvailableExcommunications()
                .stream()
                .filter(e -> e.getPeriod() == 1)
                .collect(Collectors.toList());
        Excommunication firstPeriodExcommunication = firstPeriodExcommunications.get(new Random().nextInt(firstPeriodExcommunications.size()));

        // Second period
        List<Excommunication> secondPeriodExcommunications = getGame().getAvailableExcommunications()
                .stream()
                .filter(e -> e.getPeriod() == 2)
                .collect(Collectors.toList());
        Excommunication secondPeriodExcommunication = secondPeriodExcommunications.get(new Random().nextInt(secondPeriodExcommunications.size()));

        // Third period
        List<Excommunication> thirdPeriodExcommunications = getGame().getAvailableExcommunications()
                .stream()
                .filter(e -> e.getPeriod() == 3)
                .collect(Collectors.toList());
        Excommunication thirdPeriodExcommunication = thirdPeriodExcommunications.get(new Random().nextInt(thirdPeriodExcommunications.size()));

        // Set chosen excommunications in the game
        Excommunication[] chosenExcommunications = {firstPeriodExcommunication, secondPeriodExcommunication, thirdPeriodExcommunication};
        getGame().getBoard().setExcommunications(chosenExcommunications);
    }

}
