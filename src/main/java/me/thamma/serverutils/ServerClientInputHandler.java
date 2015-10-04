package me.thamma.serverutils;

@FunctionalInterface
public interface ServerClientInputHandler {
	/**
	 * Handles the input the server received from the client
	 * 
	 * @param server
	 *            The server receiving the input
	 * @param input
	 *            The input String
	 * @param user
	 *            The ServerConnection that sent the input
	 */
	void handle(Server server, String input, ServerConnection user);
}
