package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerToClientInterface extends Remote {
    void pingClient() throws RemoteException;
}
