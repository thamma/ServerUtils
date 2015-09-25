package me.thamma.commands;

import java.util.HashMap;

import me.thamma.connection.Client;

/**
 * Command Factory
 * 
 * @author Wikipedia
 * @see https://en.wikipedia.org/wiki/Command_pattern#Java_8
 */
public class CommandFactory {
	private final HashMap<String, Command> commands;

	private CommandFactory() {
		this.commands = new HashMap<>();
	}

	public void addCommand(String name, Command command) {
		this.commands.put(name, command);
	}

	public void executeCommand(String name, Client client, String... args) {
		if (this.commands.containsKey(name)) {
			this.commands.get(name).apply(client, args);
		}
	}

	public void listCommands() {
		System.out.println("Commands enabled :");
		this.commands.keySet().stream().forEach(System.out::println);
	}

	/* Factory pattern */
	public static CommandFactory init() {
		CommandFactory cf = new CommandFactory();
		cf.addCommand("Light on", (client, args) -> System.out.println("Light turned on"));
		cf.addCommand("Light off", (client, args) -> {
			System.out.println("Light turned off");
		});
		cf.addCommand("Message", (client, args) -> {
			if (args.length == 1) {
				client.pushMessage(args[0]);
			}
		});
		return cf;
	}

}
