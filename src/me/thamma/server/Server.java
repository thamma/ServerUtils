package me.thamma.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Server {
	private int port;
	private List<User> users;
	private ServerSocket server;

	public Server(int port) throws IOException {
		this.port = port;
		int size = 2;
		server = new ServerSocket(this.port);
		users = new ArrayList<User>();

		while (users.size() != size) {
			System.out.println("Waiting for users! " + users.size() + "/" + size);
			int id = users.size();
			registerUser();
		}
		System.out.println("User limit exceeded");
		loop: while (true) {
			for (User u : users) {
				if (u.getInputStream().available() != 0) {
					String msg = u.getInputStream().readUTF();
					System.out.println(u.getId() + ": " + msg);
					if (msg.equalsIgnoreCase("kill")) {
						for (User tokill : users) {
							tokill.getOutputStream().writeUTF("You should die now");
//							tokill.kill();
						}
						break loop;
					}
				}
			}
		}
		server.close();
	}

	static int count = 0;

	private void registerUser() throws IOException {
		users.add(new User(count, server.accept()));
		count++;

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
	}

	public DataOutputStream getOutputStream() {
		return this.output;
	}

	public DataInputStream getInputStream() {
		return this.input;
	}

}