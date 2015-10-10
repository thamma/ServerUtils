package me.thamma.serverutils.handleres;

import me.thamma.serverutils.Server;
import me.thamma.serverutils.ServerConnection;

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
	void handle(me.thamma.serverutils.Server server, me.thamma.serverutils.ServerConnection connection);
}