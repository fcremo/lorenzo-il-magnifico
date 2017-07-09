package ui.cli.contexts;

import server.exceptions.LoginException;
import client.exceptions.NetworkException;
import ui.cli.exceptions.InvalidCommandException;

import java.rmi.RemoteException;

public class LoginContext extends Context {
    private Callback callback;

    public LoginContext(UIContextInterface uiContextInterface, Callback callback) {
        super(uiContextInterface);
        this.callback = callback;
        this.addCommand("login", this::login, "<username> Login");
        this.uiContextInterface.println("Please login");
        this.printHelp(false);
    }

    private void login(String[] params) throws InvalidCommandException, NetworkException, RemoteException {
        if (params.length != 1) throw new InvalidCommandException("You have to specify a username");

        String username = params[0];
        try {
            callback.login(username);
        }
        catch (LoginException e) {
            uiContextInterface.println("Login failed! Try with a different username.");
        }
    }

    @FunctionalInterface
    public interface Callback {
        void login(String username) throws NetworkException, LoginException, RemoteException;
    }
}
