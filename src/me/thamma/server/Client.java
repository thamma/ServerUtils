package me.thamma.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

class Client {
	private Socket socket;
	private DataOutputStream dOut;
	private DataInputStream dIn;
	private Scanner sc;

	public Client(String ip, int port) throws UnknownHostException, IOException {
		this.socket = new Socket(ip, port);
		dOut = new DataOutputStream(socket.getOutputStream());
		dIn = new DataInputStream(socket.getInputStream());
		sc = new Scanner(System.in);
		startLocalInput();
		startRemoteInput((input) -> {
			System.out.println(input);
		});

	}

	private void startRemoteInput(InputHandler inputHandler) {
		Thread remoteInput = new Thread(() -> {
			while (true) {
				try {
					if (dIn.available() != 0) {
						String message = dIn.readUTF();
						if (message.equals(""))
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

	private void startLocalInput() {
		Thread localInput = new Thread(() -> {
			while (true) {
				if (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (!line.equals(""))
						pushMessage(line);
				}
			}
		});
		localInput.start();
	}

	public void pushMessage(String message) {
		try {
			dOut.writeUTF(message);
			dOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not send message to remote");
		}
	}
}