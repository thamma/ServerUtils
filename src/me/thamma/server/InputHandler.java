package me.thamma.server;

/**
 * Lets the client handle input received from remote or local
 * @author Dominic
 *
 */
public interface InputHandler {
	void handle(String input);
}