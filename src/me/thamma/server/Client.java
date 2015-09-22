package me.thamma.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

class Client {
	private Socket socket;

	public Client(String ip, int port) throws UnknownHostException, IOException {
		this.socket = new Socket(ip, port);

		DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
		DataInputStream dIn = new DataInputStream(socket.getInputStream());
		Scanner sc = new Scanner(System.in);
		while (true) {
			if (sc.hasNextLine()) {
				dOut.writeUTF(sc.nextLine());
				dOut.flush();
			}
			if (dIn.available() != 0) {
				String msg = dIn.readUTF();
				System.out.println("Server responded: " + msg);
			}
		}
	}

}