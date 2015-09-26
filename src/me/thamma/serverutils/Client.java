package me.thamma.serverutils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	private Socket socket;
	private DataOutputStream dataOut;
	private DataInputStream dataIn;
	private Scanner sc;

	/**
	 * Client constructor to be launched by a terminal
	 * 
	 * @param ip
	 *            The remote IP to connect to
	 * @param port
	 *            The remote port to connect to
	 * @param localInput
	 *            The InputHandler to handle the local input
	 * @param remoteInput
	 *            The InputHandler to handle the remote output
	 * @throws UnknownHostException
	 *             If Socket connection cannot be established
	 * @throws IOException
	 *             If Socket connection cannot be established
	 */
	public Client(String ip, int port, ClientInputHandler localInput, ClientInputHandler remoteInput)
			throws UnknownHostException, IOException {
		this.socket = new Socket(ip, port);
		this.dataOut = new DataOutputStream(socket.getOutputStream());
		this.dataIn = new DataInputStream(socket.getInputStream());
		this.sc = new Scanner(System.in);
		handleLocalInput(localInput);
		handleRemoteInput(remoteInput);
	}

	/**
	 * Starts a thread which fetches the remote input
	 * 
	 * @param inputHandler
	 *            The InputHandler interface to handle the String input
	 */
	private void handleRemoteInput(ClientInputHandler inputHandler) {
		Thread remoteInput = new Thread(() -> {
			while (true) {

				try {
					if (dataIn.available() != 0) {
						String message = dataIn.readUTF();
						if (!message.equals(""))
							inputHandler.handle(this, message);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("[ServerUtils] Could not recieve message from remote");
				}
			}
		});
		remoteInput.start();
	}

	/**
	 * Starts a thread which fetches the local input
	 * 
	 * @param inputHandler
	 *            The InputHandler interface to handle the String input
	 */
	private void handleLocalInput(ClientInputHandler inputHandler) {
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
	 * Sends a message to the remote server
	 * 
	 * @param message
	 *            The String to send
	 */
	public void pushMessage(String message) {
		try {
			dataOut.writeUTF(message);
			dataOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[ServerUtils] Could not send message to remote");
		}
	}
}