package me.thamma.server;

/**
 * Lets the server handle inputs while referencing the user
 * 
 * @author Dominic
 *
 */
public interface RemoteInputHandler {
	void handle(String input, User user);
}
