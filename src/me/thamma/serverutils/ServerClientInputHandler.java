package me.thamma.serverutils;

@FunctionalInterface
public interface ServerClientInputHandler {
	/**
	 * Handles the clients Input
	 * 
	 * @param server
	 *            The client to manage the input in
	 * @param input
	 *            The input string
	 * @param user
	 *            The ServerConnection that sent the input
	 */
	void handle(Server server, String input, ServerConnection user);
}
