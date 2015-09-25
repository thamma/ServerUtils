package me.thamma.connection;

public class User {

	static final char split = ';';
	private int id;
	private String nick;

	public User(int id) {
		setId(id);
		setName("Player(" + id + ")");
	}

	public User(int id, String nick) {
		this(id);
		this.nick = nick;
	}

	public static User fromResource(String src) {
		String[] args = src.split("" + split);
		return new User(Integer.valueOf(args[0]), args[1].replaceAll(";", ""));
	}

	public void setName(String nick) {
		this.nick = nick;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.nick;
	}

	public int getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return this.id + split + this.getName();
	}

}