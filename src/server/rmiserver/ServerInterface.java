package server.rmiserver;

import client.ServerToClientInterface;
import server.ClientToServerInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    ClientToServerInterface getServerConnection(ServerToClientInterface client) throws RemoteException;
}
