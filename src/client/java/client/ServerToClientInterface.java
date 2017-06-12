package client;

import model.Game;
import model.player.bonustile.PersonalBonusTile;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerToClientInterface extends Remote {
    void pingClient() throws RemoteException;

    void setConfiguration (Game game);

    PersonalBonusTile choosePersonalBonusTile (List<PersonalBonusTile> personalBonusTiles) throws RemoteException;
}
