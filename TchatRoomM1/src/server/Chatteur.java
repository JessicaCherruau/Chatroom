package server;

public class Chatteur {
	
	private String name;
	private static final String DEFAULT_NAME = "invit�";
	
	public Chatteur(){
		this.name = DEFAULT_NAME;
	}
	public Chatteur(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

}
