package me.thamma.serverutils;

@FunctionalInterface
public interface ClientInputHandler {
	void handle(Client server, String input);
}
