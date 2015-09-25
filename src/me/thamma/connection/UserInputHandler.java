package me.thamma.connection;

/**
 * Lets the server handle inputs while referencing the user
 * 
 */
public interface UserInputHandler {
	void handle(String input, ServerConnection user);
}
