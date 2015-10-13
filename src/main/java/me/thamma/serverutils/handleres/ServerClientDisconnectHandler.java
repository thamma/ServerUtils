package me.thamma.serverutils.handleres;

import me.thamma.serverutils.Server;
import me.thamma.serverutils.ServerConnection;

public interface ServerClientDisconnectHandler {

	public void handle(Server server, ServerConnection connection);
}
