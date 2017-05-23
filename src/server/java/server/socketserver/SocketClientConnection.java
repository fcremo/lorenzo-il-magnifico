package server.socketserver;

import server.ClientConnection;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * This class is responsible for handling communication with a single client.
 * It reads the actions from the
 */
public class SocketClientConnection extends ClientConnection implements Runnable {
    private Socket connection;
    private BufferedReader input;
    private BufferedWriter output;

    public SocketClientConnection(Socket connection) throws IOException {
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
}
