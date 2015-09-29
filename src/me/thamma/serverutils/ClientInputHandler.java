package me.thamma.serverutils;

@FunctionalInterface
public interface ClientInputHandler {
	/**
	 * Handles the local client input referencing the client object
	 * @param client
	 *            The client to handle the input
	 * @param input
	 *            The input String
	 */
	void handle(Client client, String input);
}
