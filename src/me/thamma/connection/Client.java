package me.thamma.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.thamma.commands.CommandFactory;

public class Client extends User {
	private Socket socket;
	private DataOutputStream dataOut;
	private DataInputStream dataIn;
	private Scanner sc;
	private CommandFactory commandFactory;

	/**
	 * Client constructor to be launched by a terminal
	 * 
	 * @param ip
	 *            The remote IP to connect to
	 * @param port
	 *            The remote port to connect to
	 * @throws UnknownHostException,
	 *             IOException If Socket connection cannot be established
	 */
	public Client(String ip, int port) throws UnknownHostException, IOException {
		super(-1);
		this.socket = new Socket(ip, port);
		commandFactory = CommandFactory.init();
		dataOut = new DataOutputStream(socket.getOutputStream());
		dataIn = new DataInputStream(socket.getInputStream());
		sc = new Scanner(System.in);
		handleLocalInput((input) -> {
			if (input.matches("[{](.*)[}]")) {
				System.out.println("Cmd detected");
				command(input);
			} else if (input.startsWith("msg ")) { 
				pushMessage(input.replaceFirst("msg ", ""));
			} else
				System.out.println("> " + input);
		});
		handleRemoteInput((input) -> {
			System.out.println(input);
		});

	}

	/**
	 * Handles the presence of a command matching the pattern {[name],[args...]}
	 * 
	 * @param input
	 */
	private void command(String input) {
		Matcher matcher = Pattern.compile("[{](.*)[}]").matcher(input);
		if (matcher.matches()) {
			System.out.println("Cmd matches");
			String[] split = matcher.group(1).split(",");
			String[] args = new String[split.length - 1];
			for (int i = 1; i < split.length; i++) {
				args[i - 1] = split[i];
			}
			for (String s : args)
				System.out.println(s);
			commandFactory.executeCommand(split[0], this, args);
		}
	}

	/**
	 * Starts a thread which fetches the remote input
	 * 
	 * @param inputHandler
	 *            The InputHandler interface to handle the String input
	 */
	private void handleRemoteInput(InputHandler inputHandler) {
		Thread remoteInput = new Thread(() -> {
			while (true) {
				try {
					if (dataIn.available() != 0) {
						String message = dataIn.readUTF();
						if (!message.equals(""))
							inputHandler.handle(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Could not recieve message from remote");
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
	private void handleLocalInput(InputHandler inputHandler) {
		Thread localInput = new Thread(() -> {
			while (true) {
				if (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (!line.equals(""))
						inputHandler.handle(line);
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
			System.out.println("Could not send message to remote");
		}
	}
}