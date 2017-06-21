package ui.cli.contexts;

import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import ui.cli.InvalidCommandException;

import java.rmi.RemoteException;

public class LoginContext extends Context {
    private String username;

    private Callback callback;

    public LoginContext(Callback callback) {
        this.callback = callback;
        this.addCommand("login", this::login, "Login with [username]");
        System.out.println("Please login");
        this.printHelp();
    }

    private void login(String[] params) throws InvalidCommandException {
        if (params.length != 1) throw new InvalidCommandException();

        username = params[0];
        try {
            callback.login(username);
        } catch (NetworkException | RemoteException e) {
            // TODO: 5/19/17 how to handle network exceptions? 
            e.printStackTrace();
        } catch (LoginException e) {
            System.out.println("Login failed! Try with a different username.");
        }
    }

    @FunctionalInterface
    public interface Callback {
        void login(String username) throws NetworkException, LoginException, RemoteException;
    }
}
