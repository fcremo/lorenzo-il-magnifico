package server.socketserver;

import server.GameRoom;
import server.ServerController;

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
    public void run() {
        try {
            listenerSocket = new ServerSocket(port);
            // TODO: 5/19/17 find a way to disable the infinite loop inspection (is intellij bugged?)
            // until then, keep this ugly hack to shut it up
            while((true)){
                // accept a connection
                Socket s = listenerSocket.accept();
                SocketClientConnection clientConnection = new SocketClientConnection(s);

                // create a server controller
                ServerController controller = new ServerController(clientConnection, rooms);

                // start the client handler
                Thread handler = new Thread(clientConnection);
                handler.start();
            }
        } catch (IOException e) {
            LOGGER.severe("Error while trying to create the socket server: " + e.getMessage());
        }
    }
}
