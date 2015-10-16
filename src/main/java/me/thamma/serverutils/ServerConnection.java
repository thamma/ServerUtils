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
	private boolean alive;

	//////////////////
	// constructors //
	//////////////////

	public ServerConnection(int id, Socket socket) {
		this.socket = socket;
		this.id = id;
		this.alive = true;
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

	public boolean alive() {
		return this.alive;
		// if (!this.alive)
		// return false;
		// try {
		// getInputStream().available();
		// return true;
		// } catch (IOException e) {
		// return false;
		// }
		// return true;
	}

	public boolean inputAvailable() {
		try {
			return getInputStream().available() != 0;
		} catch (IOException e) {
			return false;
		}
	}

	public String getInput() {
		try {
			return this.getInputStream().readUTF();
		} catch (IOException e) {
			return "";
		}
	}

	public void sendMessage(String message) throws IOException {
		this.getOutputStream().writeUTF(message);
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
			this.alive = false;
			this.inputStream.close();
			this.outputStream.close();
			this.socket.close();
		} catch (IOException e) {
			warning("Could not properly close Socket!");
		}
	}

}
