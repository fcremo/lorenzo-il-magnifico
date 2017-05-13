package ui.cli.contexts;

import it.polimi.ingsw.lorenzo.client.ConnectionMethod;
import it.polimi.ingsw.lorenzo.client.exceptions.NetworkException;
import ui.cli.InvalidCommandException;

public class NetworkSettingsContext extends Context {
    private String hostname = "localhost";
    private int port = 8420;
    private ConnectionMethod connectionMethod = ConnectionMethod.SOCKET;

    private Callback callback;

    public NetworkSettingsContext(Callback callback) {
        this.callback = callback;
        this.addCommand("set-hostname", this::setHostname, "Set hostname");
        this.addCommand("set-port", this::setPort, "Set port");
        this.addCommand("set-method", this::setMethod, "Set connection method");
        this.addCommand("show-settings", this::showSettings, "Show current settings");
        this.addCommand("connect", this::connect, "Connect to the server");
    }

    private void setHostname(String[] params) throws InvalidCommandException{
        if(params.length != 1) throw new InvalidCommandException();

        this.hostname = params[0];
    }

    private void setPort(String[] params) throws InvalidCommandException {
        if(params.length != 1) throw new InvalidCommandException();

        try{
            this.port = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e){
            throw new InvalidCommandException();
        }

    }

    private void setMethod(String[] params) throws InvalidCommandException {
        if(params.length != 1) throw new InvalidCommandException();

        switch (params[0]){
            case "SOCKET":
            case "socket":
                this.connectionMethod = ConnectionMethod.SOCKET;
                break;
            case "RMI":
            case "rmi":
                this.connectionMethod = ConnectionMethod.RMI;
                break;
            default:
                throw new InvalidCommandException();
        }
    }

    private void showSettings(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException();

        System.out.println(String.format("Current settings: %s:%d (%s)", hostname, port, connectionMethod.name()));
    }

    private void connect(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException();

        try {
            callback.connect(connectionMethod, hostname, port);
        } catch (NetworkException e) {
            System.out.println("Connection error! Please double check the settings.");
        }
    }

    @FunctionalInterface
    public interface Callback {
        void connect(ConnectionMethod connectionMethod, String hostname, int port) throws NetworkException;
    }
}
