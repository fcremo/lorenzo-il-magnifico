package client;

import gamecontroller.GameEventsInterface;
import model.player.PersonalBonusTile;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface specifies the server to client methods
 */
public interface ServerToClientInterface extends GameEventsInterface, Remote {
    void pingClient() throws RemoteException;

    void askToChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) throws RemoteException;
}
