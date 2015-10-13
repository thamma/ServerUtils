package me.thamma.serverutils;

import static me.thamma.serverutils.Utils.warning;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import me.thamma.serverutils.handleres.ServerClientDisconnectHandler;
import me.thamma.serverutils.handleres.ServerClientInputHandler;
import me.thamma.serverutils.handleres.ServerInputHandler;
import me.thamma.serverutils.handleres.ServerNewConnectionHandler;

public abstract class Server implements Iterable<ServerConnection> {
	private int port;
	private List<ServerConnection> connections;
	private ServerSocket server;
	private Scanner sc;
	private int nextId;

	//////////////////
	// Constructors //
	//////////////////

	public Server(int port) throws IOException {
		this.port = port;
		this.server = new ServerSocket(this.port);
		this.connections = new CopyOnWriteArrayList<ServerConnection>();
		this.sc = new Scanner(System.in);
		this.nextId = -1;
		registerUsers(getServerNewConnectionHandler());
		registerServerListener();
	}

	/////////////
	// methods //
	/////////////

	@Override
	public Iterator<ServerConnection> iterator() {
		return this.connections.iterator();
	}

	public void message(String message, int id) {
		try {
			for (ServerConnection client : this) {
				if (client.getId() == id) {
					client.sendMessage(message);
				}
			}
		} catch (Exception e) {
		}
	}

	public void message(String message) {
		try {
			for (ServerConnection client : this) {
				client.sendMessage(message);
			}
		} catch (Exception e) {
			// System.out.println("Could not send message to client");
		}
	}

	public void kill() throws IOException {
		this.sc.close();
		this.server.close();
	}

	//////////////////////
	// abstract methods //
	//////////////////////

	public abstract ServerNewConnectionHandler getServerNewConnectionHandler();

	public abstract ServerInputHandler getServerInputHandler();

	public abstract ServerClientInputHandler getServerClientInputHandler();

	public abstract ServerClientDisconnectHandler getServerClientDisconnectInputHandler();

	///////////////////////
	// register handlers //
	///////////////////////

	private void registerUsers(ServerNewConnectionHandler handler) {
		new Thread(() -> {
			while (true) {
				registerUser(handler);
			}
		}).start();
	}

	private void registerUser(ServerNewConnectionHandler handler) {
		try {
			ServerConnection connection = new ServerConnection(nextId(), server.accept());
			connection.sendMessage("" + connection.getId());
			this.connections.add(connection);
			getServerNewConnectionHandler().handle(this, connection);
			registerClientListener(connection);
		} catch (IOException e) {
			warning("Could not accept new ServerConnection!");
			return;
		}

	}

	private void registerClientListener(ServerConnection connection) {
		new Thread(() -> {
			while (connection.alive())
				if (connection.inputAvailable()) {
					String message = connection.getInput();
					if (message == null) {
						this.getServerClientDisconnectInputHandler().handle(this, connection);
						return;
					} else if (!message.equals(""))
						this.getServerClientInputHandler().handle(this, message, connection);
				}
			for (ServerConnection kill : this) {
				if (kill.getId() == connection.getId()) {
					getServerClientDisconnectInputHandler().handle(this, kill);
					kill.kill();
					this.connections.remove(kill);
				}
			}
			return;
		}).start();
	}

	private void registerServerListener() {
		new Thread(() -> {
			while (true)
				if (sc.hasNextLine()) {
					String message = sc.nextLine();
					if (!message.equals(""))
						this.getServerInputHandler().handle(this, message);
				}
		}).start();
	}

	private int nextId() {
		nextId++;
		return this.nextId;
	}

}
