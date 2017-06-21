package ui.cli.contexts;

import client.ConnectionMethod;
import client.exceptions.NetworkException;
import ui.cli.exceptions.InvalidCommandException;

import java.rmi.RemoteException;

public class NetworkSettingsContext extends Context {
    private String hostname = "localhost";
    private int port = 1099;
    private ConnectionMethod connectionMethod = ConnectionMethod.RMI;

    private Callback callback;

    public NetworkSettingsContext(Callback callback) {
        this.callback = callback;
        this.addCommand("set-hostname", this::setHostname, "Set hostname");
        this.addCommand("set-port", this::setPort, "Set port");
        this.addCommand("set-method", this::setMethod, "Set connection method");
        this.addCommand("show-settings", this::showSettings, "Show current settings");
        this.addCommand("connect", this::connect, "Connect to the server");
        System.out.println("Network settings");
        printHelp();
        System.out.println();
        this.handleInput("show-settings");
    }

    private void setHostname(String[] params) throws InvalidCommandException {
        if (params.length != 1) throw new InvalidCommandException("You have to specify a hostname");

        this.hostname = params[0];
    }

    private void setPort(String[] params) throws InvalidCommandException {
        if (params.length != 1) throw new InvalidCommandException("You have to specify the port");

        int port;

        try {
            port = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid port number");
        }

        if (port < 1 || port > 65535) {
            throw new InvalidCommandException("Port number must be between 1 and 65535");
        }

        this.port = port;
    }

    private void setMethod(String[] params) throws InvalidCommandException {
        if (params.length != 1) throw new InvalidCommandException("You have to specify a connection method");

        switch (params[0].toLowerCase()) {
            case "socket":
                this.connectionMethod = ConnectionMethod.SOCKET;
                break;
            case "rmi":
                this.connectionMethod = ConnectionMethod.RMI;
                break;
            default:
                throw new InvalidCommandException("Invalid connection method");
        }
    }

    private void showSettings(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException("This command takes no arguments");

        System.out.println(String.format("Current settings: %s:%d (%s)", hostname, port, connectionMethod.name()));
    }

    @SuppressWarnings("squid:S1166") // Silence "Exception handlers should preserve the original exceptions" warning
    private void connect(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException("This command takes no arguments");

        try {
            callback.connect(connectionMethod, hostname, port);
        }
        catch (NetworkException | RemoteException e) {
            System.out.println("Connection error! Please double check the settings.");
        }
    }

    @FunctionalInterface
    public interface Callback {
        void connect(ConnectionMethod connectionMethod, String hostname, int port) throws NetworkException, RemoteException;
    }
}
