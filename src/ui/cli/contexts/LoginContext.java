package ui.cli.contexts;

import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import ui.cli.exceptions.InvalidCommandException;

import java.rmi.RemoteException;

public class LoginContext extends Context {
    private Callback callback;

    public LoginContext(PrintInterface printInterface, Callback callback) {
        super(printInterface);
        this.callback = callback;
        this.addCommand("login", this::login, "Login with [username]");
        printer.println("Please login");
        this.printHelp(false);
    }

    private void login(String[] params) throws InvalidCommandException, NetworkException, RemoteException {
        if (params.length != 1) throw new InvalidCommandException("You have to specify a username");

        String username = params[0];
        try {
            callback.login(username);
        }
        catch (LoginException e) {
            printer.println("Login failed! Try with a different username.");
        }
    }

    @FunctionalInterface
    public interface Callback {
        void login(String username) throws NetworkException, LoginException, RemoteException;
    }
}
