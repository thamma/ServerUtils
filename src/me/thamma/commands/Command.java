package me.thamma.commands;

import me.thamma.connection.Client;

@FunctionalInterface
public interface Command {
	public void apply(Client c, String... args);
}