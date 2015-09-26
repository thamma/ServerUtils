package me.thamma.serverutils;

@FunctionalInterface
public interface InputHandler {
	void handle(Server server, String input);
}