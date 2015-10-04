package me.thamma.serverutils;

@FunctionalInterface
public interface ClientServerInputHandler {
	/**
	 * Handles the input the client received from the server
	 * 
	 * @param client
	 *            The client receiving the input
	 * @param input
	 *            The input String
	 */
	void handle(Client client, String input);
}
