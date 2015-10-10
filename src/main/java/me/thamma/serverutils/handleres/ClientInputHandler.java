package me.thamma.serverutils.handleres;

import me.thamma.serverutils.ServerConnection;

@FunctionalInterface
public interface ClientInputHandler {
	/**
	 * Handles the local client input referencing the client object
	 * 
	 * @param client
	 *            The client to handle the input
	 * @param input
	 *            The input String
	 */
	void handle(ServerConnection client, String input);
}
