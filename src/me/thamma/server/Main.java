package me.thamma.server;

public class Main {
	public static void main(String[] args) {
		try {
			if (args.length == 2) {
				String type = args[0];
				int port = Integer.valueOf(args[1]);
				if (type.equalsIgnoreCase("server")) {
					new Server(port);
				} else if (type.equalsIgnoreCase("client")) {
					Client c = new Client("127.0.0.1", port);
				}
			}
		} catch (Exception e) {

		}
	}
}
