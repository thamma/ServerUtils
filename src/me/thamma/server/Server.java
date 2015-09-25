package me.thamma.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Server {
	private int port;
	private List<User> users;
	private ServerSocket server;

	public Server(int port) throws IOException {
		this.port = port;
		int size = 2;
		server = new ServerSocket(this.port);
		users = new ArrayList<User>();
		registerUsers(2);

		Thread mainLoop = new Thread(() -> {
			loop: while (true) {
				for (User u : users) {
					try {
						if (u.getInputStream().available() != 0) {
							String msg = u.getInputStream().readUTF();
							System.out.println(u.getId() + ": " + msg);
							if (msg.equalsIgnoreCase("kill")) {
								for (User tokill : users) {
									tokill.alert("DIE!");
									tokill.getOutputStream().writeUTF("You should die now");
									// tokill.kill();
								}
								break loop;
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		mainLoop.start();
		Scanner sc = new Scanner(System.in);
		Thread localInput = new Thread(() -> {
			while (true) {
				if (sc.hasNextLine()) {
					String msg = sc.nextLine();
					for (User u : users) {
						try {
							u.alert(msg);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		localInput.start();
		// server.close();
	}

	public void registerUsers(int maxCount) {
		Thread pollingNewPlayers = new Thread(() -> {
			System.out.println("Waiting for " + maxCount + " users to connect...");
			while (users.size() != maxCount) {
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

class User {
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private int id;

	public User(int id, Socket socket) throws IOException {
		this.socket = socket;
		this.id = id;
		this.input = new DataInputStream(socket.getInputStream());
		this.output = new DataOutputStream(socket.getOutputStream());
	}

	public int getId() {
		return this.id;
	}

	public void kill() throws IOException {
		this.socket.close();
		alert("Warning: connection lost!");
	}

	public DataOutputStream getOutputStream() {
		return this.output;
	}

	public DataInputStream getInputStream() {
		return this.input;
	}

	public void sendEvent() {

	}

	public void alert(String message) throws IOException {
		this.getOutputStream().writeUTF(message);
	}

}