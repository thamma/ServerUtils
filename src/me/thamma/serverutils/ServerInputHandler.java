package me.thamma.serverutils;

@FunctionalInterface
public interface ServerInputHandler {
	/**
	 * Handles the local server input referencing the server object
	 * 
	 * @param server
	 *            The server to handle the input
	 * @param input
	 *            The input String
	 */
	void handle(Server server, String input);
}