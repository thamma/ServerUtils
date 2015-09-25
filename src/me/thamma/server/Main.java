package me.thamma.server;

public class Main {
	/**
	 * Parses the command line to decide whether to launch a client or server
	 * 
	 * @param args
	 *            The command line arguments
	 */
	public static void main(String[] args) {
		try {
			if (args.length == 2) {
				String type = args[0];
				int port = Integer.valueOf(args[1]);
				if (type.equalsIgnoreCase("server")) {
					new Server(port, 2);
				} else if (type.equalsIgnoreCase("client")) {
					new Client("localhost", port);
				}
			}
		} catch (Exception e) {

		}
	}
}
