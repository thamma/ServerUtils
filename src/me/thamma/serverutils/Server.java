package me.thamma.serverutils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
	private int port;
	private List<ServerConnection> users;
	private ServerSocket server;
	private Scanner sc;
	private boolean waitingForClients;

	/**
	 * Server constructor to be launched by a terminal
	 * 
	 * @param port
	 *            The port to host the Server on
	 * @param size
	 *            The maximum amount of users to connect
	 * @throws IOException
	 */
	public Server(int port, int size, InputHandler localInput, ClientInputHandler remoteInput) throws IOException {
		this.port = port;
		this.server = new ServerSocket(this.port);
		this.users = new ArrayList<ServerConnection>();
		this.waitingForClients = true;
		registerUsers(size);
		handleClientInputs(remoteInput);
		handleLocalInput(localInput);
	}

	/**
	 * Starts a thread which fetches the clients' input (in order)
	 * 
	 * @param inputHandler
	 *            The InputHandler interface to handle the String input
	 */
	private void handleClientInputs(ClientInputHandler inputHandler) {
		Thread mainLoop = new Thread(() -> {
			while (true) {
				for (ServerConnection user : users) {
					try {
						if (user.hasInput()) {
							String msg = user.getInputStream().readUTF();
							System.out.println(user.getId() + ": " + msg);
							if (!msg.equalsIgnoreCase("")) {
								inputHandler.handle(msg, user);
							}
						}
					} catch (Exception e) {
						System.out.println("[ServerUtils] Could not handle RemoteInput");
						e.printStackTrace();
					}
				}
			}
		});
		mainLoop.start();
	}
	
	/**
	 * Sets whether or not so wait for further clients to connect
	 * @param wait whether to wait
	 */
	public void setWaitingForClients(boolean wait) {
		this.waitingForClients = wait;
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
			while (users.size() != maxCount && waitingForClients) {
				System.out.println("Waiting for clients! " + users.size() + "/" + maxCount);
				try {
					registerUser();
				} catch (Exception e) {
					System.out.println("Could not register user!");
					e.printStackTrace();
				}
			}
			System.out.println("[ServerUtils] User limit exceeded");
		});
		pollingNewPlayers.start();
		while (users.size() != maxCount) {

		}
	}

	private void registerUser() throws IOException {
		users.add(new ServerConnection(users.size(), server.accept()));
	}
}