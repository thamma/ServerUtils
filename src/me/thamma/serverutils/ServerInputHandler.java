package me.thamma.serverutils;

@FunctionalInterface
public interface ServerInputHandler {
	void handle(Server server, String input, ServerConnection user);
}
