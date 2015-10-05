package me.thamma.serverutils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public abstract class Server implements Iterable<ServerConnection> {
	private int port;
	private List<ServerConnection> clients;
	private ServerSocket server;
	private Scanner sc;

	/**
	 * Server constructor to be launched by a terminal
	 * 
	 * @param port
	 *            The port to host the Server on
	 * @param size
	 *            The maximum amount of users to connect
	 * @throws IOException
	 *             If the connection could not be established or clients failed
	 *             to connect
	 */
	public Server(int port, int size) throws IOException {
		this.port = port;
		this.server = new ServerSocket(this.port);
		this.clients = new ArrayList<ServerConnection>();
		this.sc = new Scanner(System.in);
		registerUsers(size, getServerNewConnectionHandler());
		handleLocalInput(getServerInputHandler());
		handleClientInputs(getServerClientInputHandler());
	}

	/**
	 * Closes the ServerSocket connection
	 * 
	 * @throws IOException
	 *             If the connection could not be closed
	 */
	public void kill() throws IOException {
		server.close();
	}

	public ServerNewConnectionHandler getServerNewConnectionHandler() {
		return new ServerNewConnectionHandler() {
			@Override
			public void handle(Server server, ServerConnection connection) {
				// TODO Auto-generated method stub
			}
		};
	}

	public abstract ServerInputHandler getServerInputHandler();

	public abstract ServerClientInputHandler getServerClientInputHandler();

	/**
	 * Starts a thread which fetches the clients' input (in order)
	 * 
	 * @param inputHandler
	 *            The InputHandler interface to handle the String input
	 */
	private void handleClientInputs(ServerClientInputHandler inputHandler) {
		Thread mainLoop = new Thread(() -> {
			while (true) {
				for (ServerConnection user : clients) {
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

	public void message(String message, int id) {
		try {
			for (ServerConnection client : this) {
				if (client.getId() == id) {
					client.message(message);
				}
			}
		} catch (Exception e) {
		}
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
	public void message(String message) {
		try {
			for (ServerConnection client : this) {
				client.message(message);
			}
		} catch (Exception e) {
			System.out.println("Could not send message to client");
		}
	}

	/**
	 * 
	 * @return The List<ServerConnection> containing all clients
	 */
	public List<ServerConnection> getClients() {
		return this.clients;
	}

	/**
	 * Creates a thread that lets the main thread sleep until users.size()
	 * matches cap
	 * 
	 * @param cap
	 *            How many users to wait for
	 */
	public void registerUsers(int cap, ServerNewConnectionHandler handler) {
		Thread pollingNewPlayers = new Thread(() -> {
			System.out.println("[ServerUtils] Waiting for " + cap + " users to connect...");
			while (clients.size() != cap) {
				System.out.println("[ServerUtils] Waiting for clients! " + clients.size() + "/" + cap);
				try {
					registerUser(handler);
				} catch (Exception e) {
					System.out.println("[ServerUtils] Could not register user!");
					e.printStackTrace();
				}
			}
			System.out.println("[ServerUtils] User limit exceeded");
		});
		pollingNewPlayers.start();
		while (clients.size() != cap) {
		}
	}

	/**
	 * Pauses the thread waiting for the next client to connect
	 * 
	 * @throws IOException
	 */
	private void registerUser(ServerNewConnectionHandler handler) throws IOException {
		ServerConnection connection = new ServerConnection(clients.size(), server.accept());
		connection.message("" + connection.getId());
		clients.add(connection);
		handler.handle(this, connection);
	}

	/**
	 * The Iterator implemented by Iterable<ServerConnection>
	 * 
	 * @return Iterator<ServerConnection>
	 */
	@Override
	public Iterator<ServerConnection> iterator() {
		return this.clients.iterator();
	}
}