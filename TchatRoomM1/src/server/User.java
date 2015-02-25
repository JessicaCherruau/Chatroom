package server;

public class User extends Chatteur {

	private String password;
	
	public User(String name, String password){
		super(name);
		this.password = password;
	}
	
	public boolean checkPassword(String givenPassword){
		return this.password.equals(givenPassword);
	}
}
