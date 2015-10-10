package me.thamma.serverutils;

import java.io.IOException;
import static me.thamma.serverutils.Utils.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import me.thamma.serverutils.handleres.ClientInputHandler;
import me.thamma.serverutils.handleres.ClientServerInputHandler;

public abstract class Client extends ServerConnection {

	private Scanner sc;
	/////////////////
	// constructor //
	/////////////////

	public Client(String ip, int port) throws UnknownHostException, IOException {
		super(-1, new Socket(ip, port));
		this.sc = new Scanner(System.in);
		handleLocalInput(getClientInputHandler());
		handleRemoteInput(getClientServerInputHandler());
	}

	// methods

	public abstract ClientInputHandler getClientInputHandler();

	public abstract ClientServerInputHandler getClientServerInputHandler();

	private void handleRemoteInput(ClientServerInputHandler inputHandler) {
		Thread remoteInput = new Thread(() -> {
			while (true)
				if (super.inputAvailable()) {
					String message = super.getInput();
					if (!message.equals(""))
						if (super.getId() == -1) {
							try {
								super.setId(Integer.parseInt(message));
							} catch (Exception e) {
								warning("Shutting client down. Id was not properly provided. Please talk to your network administrator!");
								super.kill();
							}
						} else
							inputHandler.handle(this, message);
				}
		});
		remoteInput.start();
	}

	@Override
	public void kill() {
		super.kill();
		this.sc.close();
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
}
