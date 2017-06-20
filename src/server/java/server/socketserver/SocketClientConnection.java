package server.socketserver;

import gamecontroller.GameState;
import model.player.PersonalBonusTile;
import server.ClientConnection;
import server.GameRoom;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for handling communication with a single client.
 * It reads the actions from the
 */
public class SocketClientConnection extends ClientConnection implements Runnable {
    private Socket connection;
    private BufferedReader input;
    private BufferedWriter output;

    public SocketClientConnection(ArrayList<GameRoom> gameRooms, Socket connection) throws IOException {
        super(gameRooms);
        this.connection = connection;
        input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
    }

    @Override
    public void run() {
        // TODO: 5/19/17 find a way to disable the infinite loop inspection
        while ((true)) {
            try {
                String in = input.readLine();

            } catch (IOException e) {
            }
        }
    }

    @Override
    public void pingClient() throws RemoteException {

    }

    @Override
    public void onGameStateChange(GameState gameState) {

    }

    @Override
    public void askToChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) throws RemoteException {

    }
}
