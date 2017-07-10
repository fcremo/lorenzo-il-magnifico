package network;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface is used by RMI clients to obtain a connection object from the server
 */
public interface RMIServerInterface extends Remote {
    ClientToServerInterface getServerConnection(ServerToClientInterface client) throws RemoteException;
}
