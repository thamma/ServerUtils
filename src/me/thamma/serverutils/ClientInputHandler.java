package me.thamma.serverutils;

@FunctionalInterface
public interface ClientInputHandler {
	void handle(Client client, String input);
}
