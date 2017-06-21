package server.socketserver;

import server.GameRoom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SocketServer implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("Server");

    private int port;

    private ArrayList<GameRoom> rooms;

    private ServerSocket listenerSocket;

    public SocketServer(int port, ArrayList<GameRoom> rooms) {
        this.port = port;
        this.rooms = rooms;
    }

    @Override
    @SuppressWarnings("squid:S2189") // Disable infinite loop warning
    public void run() {
        try {
            listenerSocket = new ServerSocket(port);
            while (true) {
                // accept a connection
                Socket s = listenerSocket.accept();
                SocketClientConnection clientConnection = new SocketClientConnection(rooms, s);

                // create a server controller
                //ServerController controller = new ServerController(clientConnection, rooms);

                // start the client handler
                Thread handler = new Thread(clientConnection);
                handler.start();
            }
        }
        catch (IOException e) {
            LOGGER.severe("Error while trying to create the socket server: " + e.getMessage());
        }
    }
}
