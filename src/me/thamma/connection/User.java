package me.thamma.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class User {
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

	public boolean hasInput() throws IOException {
		return getInputStream().available() != 0;
	}

	public void alert(String message) throws IOException {
		this.getOutputStream().writeUTF(message);
	}

}