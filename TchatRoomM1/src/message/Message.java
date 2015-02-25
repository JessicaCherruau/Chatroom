package message;

public class Message {

	// Codes � l'identification
	public static final int CONNECTED = 0;
	public static final int UNKNOWN_USER = 1;
	public static final int WRONG_PWD = 2;
	public static final int SIGNIN_ABORT = 3;
	
	public static final String[] txtCodes = 
		{
			"Vous �tes connect� !",
			"Voulez-vous cr�er un compte ? O/N",
			"Mauvais mot de passe, d�connexion",
			"Abandon de la cr�ation de compte, d�connexion"
		};
	public Message(){
		
	}
}
