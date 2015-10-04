package me.thamma.serverutils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection {
	public Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private int id;

	/**
	 * ServerConnection constructor
	 * 
	 * @param id
	 *            The (invariant unique) id of the Connection
	 * @param socket
	 *            The Socket this client is connected to
	 * @throws IOException
	 *             If either the Input- or OutputStream is not available
	 */
	public ServerConnection(int id, Socket socket) throws IOException {
		this.socket = socket;
		this.id = id;
		this.input = new DataInputStream(socket.getInputStream());
		this.output = new DataOutputStream(socket.getOutputStream());
	}

	public int getId() {
		return this.id;
	}

	public DataOutputStream getOutputStream() {
		return this.output;
	}

	public DataInputStream getInputStream() {
		return this.input;
	}

	public boolean hasInput() {
		try {
			return getInputStream().available() != 0;
		} catch (IOException e) {
			System.out.println("Could not fetch getInputStream().available()");
			return false;
		}
	}

	public void message(String message) throws IOException {
		this.getOutputStream().writeUTF(message);
	}

}