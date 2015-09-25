package me.thamma.commands;

import me.thamma.server.Client;

@FunctionalInterface
public interface Command {
	public void apply(Client c, String... args);
}