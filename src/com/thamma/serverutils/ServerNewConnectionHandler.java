package com.thamma.serverutils;

@FunctionalInterface
public interface ServerNewConnectionHandler {
	/**
	 * Is called upon a new Client connecting to the ServerSocket
	 * 
	 * @param server
	 *            The server to handle the client
	 * @param connection
	 *            The ServerConnection that just connected
	 */
	void handle(Server server, ServerConnection connection);
}