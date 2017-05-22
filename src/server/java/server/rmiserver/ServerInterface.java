package server.rmiserver;

import server.ClientToServerInterface;
import client.ServerToClientInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    ClientToServerInterface getServerConnection(ServerToClientInterface client) throws RemoteException;
}
