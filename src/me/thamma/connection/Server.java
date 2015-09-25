package me.thamma.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
	private int port;
	private List<User> users;
	private ServerSocket server;
	private Scanner sc;
	private boolean waitingForUsers;

	/**
	 * Server constructor to be launched by a terminal
	 * 
	 * @param port
	 *            The port to host the Server on
	 * @param size
	 *            The maximum amount of users to connect
	 * @throws IOException
	 */
	public Server(int port, int size) throws IOException {
		this.port = port;
		this.server = new ServerSocket(this.port);
		this.users = new ArrayList<User>();
		this.waitingForUsers = true;
		registerUsers(size);

		handleClientInputs((input, user) -> {
			System.out.println(user.getId() + ": " + input);
		});
		handleLocalInput((input) -> {
			System.out.println("> " + input);
		});
	}

	/**
	 * Starts a thread which fetches the clients' input (in order)
	 * 
	 * @param inputHandler
	 *            The InputHandler interface to handle the String input
	 */
	private void handleClientInputs(UserInputHandler inputHandler) {
		Thread mainLoop = new Thread(() -> {
			while (true) {
				for (User user : users) {
					try {
						if (user.hasInput()) {
							String msg = user.getInputStream().readUTF();
							System.out.println(user.getId() + ": " + msg);
							if (!msg.equalsIgnoreCase("")) {
								inputHandler.handle(msg, user);
							}
						}
					} catch (Exception e) {
						System.out.println("Could not handle RemoteInput");
						e.printStackTrace();
					}
				}
			}
		});
		mainLoop.start();
	}

	/**
	 * Starts a thread which fetches the local input
	 * 
	 * @param inputHandler
	 *            The InputHandler interface to handle the String input
	 */
	private void handleLocalInput(InputHandler inputHandler) {
		Thread localInput = new Thread(() -> {
			while (true) {
				if (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (!line.equals(""))
						inputHandler.handle(line);
				}
			}
		});
		localInput.start();
	}

	/**
	 * Creates a thread that lets the main thread sleep until users.size()
	 * matches maxCount
	 * 
	 * @param maxCount
	 */
	public void registerUsers(int maxCount) {
		Thread pollingNewPlayers = new Thread(() -> {
			System.out.println("Waiting for " + maxCount + " users to connect...");
			while (users.size() != maxCount && waitingForUsers) {
				System.out.println("Waiting for users! " + users.size() + "/" + maxCount);
				try {
					registerUser();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("hit user limit");
		});
		pollingNewPlayers.start();
		while (users.size() != maxCount) {

		}
	}

	private void registerUser() throws IOException {
		users.add(new User(users.size(), server.accept()));
	}
}