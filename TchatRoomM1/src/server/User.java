package server;

/**
 * Cette classe repr�sente un utilisateur poss�dant des identifiants sur l'application
 * 
 * @author Jessica CHERRUAU
 *
 */
public class User extends Chatteur {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1821422506408228509L;
	private String password;
	
	public User(String name, String password){
		super(name);
		this.password = password;
	}
	
	/**
	 * Permet la v�rification du mot de passe
	 * @param givenPassword le mot de passe � comparer avec le fichier
	 * @return true si identique, false sinon
	 */
	public boolean checkPassword(String givenPassword){
		return this.password.equals(givenPassword);
	}
}
