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
	 * @param localInput
	 *            The InputHandler to handle the local input
	 * @param serverClientInput
	 *            The ClientInputHandler to handle the remote output
	 * @throws IOException
	 *             If the connection could not be established or clients failed
	 *             to connect
	 */
	public Server(int port, int size, ServerInputHandler localInput, ServerClientInputHandler serverClientInput)
			throws IOException {
		this.port = port;
		this.server = new ServerSocket(this.port);
		this.users = new ArrayList<ServerConnection>();
		this.waitingForClients = true;
		this.sc = new Scanner(System.in);
		registerUsers(size);
		handleClientInputs(serverClientInput);
		handleLocalInput(localInput);
	}

	/**
	 * Starts a thread which fetches the clients' input (in order)
	 * 
	 * @param inputHandler
	 *            The InputHandler interface to handle the String input
	 */
	private void handleClientInputs(ServerClientInputHandler inputHandler) {
		Thread mainLoop = new Thread(() -> {
			while (true) {
				for (ServerConnection user : users) {
					try {
						if (user.hasInput()) {
							String msg = user.getInputStream().readUTF();
							System.out.println(user.getId() + ": " + msg);
							if (!msg.equalsIgnoreCase("")) {
								inputHandler.handle(this, msg, user);
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
	 * 
	 * @param wait
	 *            whether to wait
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
	private void handleLocalInput(ServerInputHandler inputHandler) {
		Thread localInput = new Thread(() -> {
			while (true) {
				if (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (!line.equals(""))
						inputHandler.handle(this, line);
				}
			}
		});
		localInput.start();
	}

	/**
	 * Sends a message to all remote clients
	 * 
	 * @param message
	 *            The message to be sent
	 * @throws IOException
	 *             If the message wasn't sent successfully
	 */
	public void message(String message) throws IOException {
		try {
			for (ServerConnection client : this.users) {
				client.message(message);
			}
		} catch (Exception e) {
			System.out.println("Could not send message to client");
		}
	}

	/**
	 * Creates a thread that lets the main thread sleep until users.size()
	 * matches cap
	 * 
	 * @param cap
	 *            How many users to wait for
	 */
	public void registerUsers(int cap) {
		Thread pollingNewPlayers = new Thread(() -> {
			System.out.println("[ServerUtils] Waiting for " + cap + " users to connect...");
			while (users.size() != cap && waitingForClients) {
				System.out.println("[ServerUtils] Waiting for clients! " + users.size() + "/" + cap);
				try {
					registerUser();
				} catch (Exception e) {
					System.out.println("[ServerUtils] Could not register user!");
					e.printStackTrace();
				}
			}
			System.out.println("[ServerUtils] User limit exceeded");
		});
		pollingNewPlayers.start();
		while (users.size() != cap) {
		}
	}

	private void registerUser() throws IOException {
		users.add(new ServerConnection(users.size(), server.accept()));
	}
}