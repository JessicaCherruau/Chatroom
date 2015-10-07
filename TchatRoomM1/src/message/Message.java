package message;

/**
 * Classe contenant des constantes utiles lors des échanges entre le client et le serveur
 * 
 * @author Jessica CHERRUAU
 *
 */
public class Message {

	// Codes à l'identification
	public static final int CONNECTED = 0;
	public static final int UNKNOWN_USER = 1;
	public static final int WRONG_PWD = 2;
	public static final int SIGNIN_ABORT = 3;
	
	// textes correspondant aux codes d'erreur
	public static final String[] txtCodes = 
		{
			"Vous êtes connecté !",
			"Voulez-vous créer un compte ? O/N",
			"Mauvais mot de passe, déconnexion",
			"Abandon de la création de compte, déconnexion"
		};
}
