package me.thamma.serverutils;

@FunctionalInterface
public interface ClientInputHandler {
	void handle(String input, ServerConnection user);
}
