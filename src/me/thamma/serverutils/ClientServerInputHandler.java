package me.thamma.serverutils;

@FunctionalInterface
public interface ClientServerInputHandler {
	/**
	 * Handles the clients Input
	 * 
	 * @param client
	 *            The client to manage the input in
	 * @param input
	 *            The input string
	 */
	void handle(Client client, String input);
}
