package ui.cli.contexts;

import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import ui.cli.exceptions.InvalidCommandException;

import java.rmi.RemoteException;

public class LoginContext extends Context {
    private Callback callback;

    public LoginContext(Callback callback) {
        this.callback = callback;
        this.addCommand("login", this::login, "Login with [username]");
        System.out.println("Please login");
        this.printHelp();
    }

    private void login(String[] params) throws InvalidCommandException, NetworkException, RemoteException {
        if (params.length != 1) throw new InvalidCommandException("You have to specify a username");

        String username = params[0];
        try {
            callback.login(username);
        }
        catch (LoginException e) {
            System.out.println("Login failed! Try with a different username.");
        }
    }

    @FunctionalInterface
    public interface Callback {
        void login(String username) throws NetworkException, LoginException, RemoteException;
    }
}
