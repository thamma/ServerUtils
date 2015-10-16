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
	private boolean alive;

	//////////////////
	// Constructors //
	//////////////////

	/**
	 * Client constructor. Upon call, Client will try to connect to the
	 * according Server.
	 * 
	 * @param ip
	 *            The IP adress to connect to
	 * @param port
	 *            The port to connect to
	 * @throws UnknownHostException
	 *             If the IP adress turned out to be invalid
	 * @throws IOException
	 */
	public Client(String ip, int port) throws UnknownHostException, IOException {
		this(ip, port, false);
	}

	/**
	 * Client constructor. Upon call, Client will try to connect to the
	 * according Server.
	 * 
	 * @param ip
	 *            The IP adress to connect to
	 * @param port
	 *            The port to connect to
	 * @param scanner
	 *            Whether or not to use a new Scanner(System.in)
	 * @throws UnknownHostException
	 *             If the IP adress turned out to be invalid
	 * @throws IOException
	 */
	public Client(String ip, int port, boolean scanner) throws UnknownHostException, IOException {
		super(-1, new Socket(ip, port));
		this.alive = true;
		if (scanner) {
			this.sc = new Scanner(System.in);
			handleLocalInput(getClientInputHandler());
		}
		handleRemoteInput(getClientServerInputHandler());
	}

	////////////////////
	// Public methods //
	////////////////////

	/**
	 * 
	 */
	@Override
	public void kill() {
		alive = false;
		super.kill();
	}

	///////////////////////
	// Register handlers //
	////////////////////////

	private void handleRemoteInput(ClientServerInputHandler inputHandler) {
		Thread remoteInput = new Thread(() -> {
			while (true && alive)
				if (super.inputAvailable()) {
					String message = super.getInput();
					if (message == null)
						return;
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
			return;
		});
		remoteInput.start();
	}

	private void handleLocalInput(ClientInputHandler inputHandler) {
		new Thread(() -> {
			while (true && alive) {
				if (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (!line.equals(""))
						inputHandler.handle(this, line);
				}
			}
		}).start();
	}

	//////////////////////
	// Abstract methods //
	//////////////////////

	public abstract ClientInputHandler getClientInputHandler();

	public abstract ClientServerInputHandler getClientServerInputHandler();
}
