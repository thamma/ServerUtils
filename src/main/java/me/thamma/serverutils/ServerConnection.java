package me.thamma.serverutils;

import static me.thamma.serverutils.Utils.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection {

	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private int id;

	//////////////////
	// constructors //
	//////////////////

	public ServerConnection(int id, Socket socket) {
		this.socket = socket;
		this.id = id;
		try {
			this.inputStream = new DataInputStream(socket.getInputStream());
			this.outputStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			warning("Could not fetch ServerConnection's input or output stream");
		}
	}

	/////////////
	// methods //
	/////////////

	public boolean inputAvailable() {
		try {
			return getInputStream().available() != 0;
		} catch (IOException e) {
			warning("Could not read input.available()!");
			return false;
		}
	}

	public String getInput() {
		try {
			return this.getInputStream().readUTF();
		} catch (IOException e) {
			warning("Could not read input.readUTF()!");
			return "";
		}
	}

	public void sendMessage(String message) {
		try {
			this.getOutputStream().writeUTF(message);
		} catch (IOException e) {
			warning("Could not send message to remote!");
		}
	}

	/////////////////////////
	// getters and setters //
	/////////////////////////

	public Socket getSocket() {
		return this.socket;
	}

	public void setId(int id) {
		if (this.id == -1) {
			this.id = id;
		} else {
			warning("The ServerConnections id cannot be changed after it has been set.");
		}
	}

	public int getId() {
		return this.id;
	}

	public DataOutputStream getOutputStream() {
		return this.outputStream;
	}

	public DataInputStream getInputStream() {
		return this.inputStream;
	}

	public void kill() {
		try {
			this.inputStream.close();
			this.outputStream.close();
			this.socket.close();
		} catch (IOException e) {
			warning("Could not properly close Socket!");
		}
	}

}
