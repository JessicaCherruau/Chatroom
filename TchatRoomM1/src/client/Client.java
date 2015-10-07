package client;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

import message.Message;
/**
 * Application cliente
 * 
 * A lancer après l'application Server
 * Un client s'identifie, choisit une chatroom et peut communiquer avec les clients connectés à cette chatroom
 * 
 * @author Jessica CHERRUAU
 *
 */
public class Client extends Thread{

	private static final int PORT_ECOUTE = 5050;
	private static final String HOST = "localhost";
	
	private Socket socket;				//socket cliente
	private ObjectInputStream reader;	//stream entrant
	private ObjectOutputStream writer;	//stream sortant
	private boolean running;			// permet d'interrompre le thread proprement

	public Client(){
		running = true;
		this.socket = null;
		try {
			socket = new Socket(HOST, PORT_ECOUTE);
			// instanciation du flux entrant
			reader = new ObjectInputStream(socket.getInputStream());
			// instanciation du flux de sortie, avec autoflush à true
			writer = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException ioe) {

			System.err.println("Connection failed");
		}
	}
	
	/**
	 * Affiche en permanence ce que le client reçoit
	 */
	public void run(){
		while(running){
			System.out.println(receive());
		}
	}

	public static void main(String[] args) {
		Client c = new Client();
		
		//instanciation du scanner
		Scanner sc = new Scanner(System.in);
		
		try {
			if(c.identification(sc)){
				// A partir de ce point le user est identifié
				// affectation à une chat room
				try {
					if(c.chooseChatroom(sc)){
						c.start();
						c.communicate(sc);
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sc.close();
		c.disconnect();
		System.exit(0);
	}

	/**
	 * Envoie à la session les messages saisie par l'utilisateur
	 * @param sc Scanner
	 */
	private void communicate(Scanner sc) {
		String saisie = "";
		do{
			saisie = sc.nextLine();
			send(saisie);
		}while(!saisie.equals("/QUIT") && running);
	}

	/**
	 * Permet à l'utilisateur de choisir une chatroom
	 * @param sc le clavier
	 * @return true si l'utilisateur a choisi un chatroom, false sinon
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public boolean chooseChatroom(Scanner sc) throws IOException, ClassNotFoundException {
		// tout d'abord on récupère la liste des chatrooms 
		// la liste retournée est une HashMap (clé valeur) ou la clé est l'index dans le 
		// tableau des chatrooms du serveur et la valeur est le thème choisi
		@SuppressWarnings("unchecked")
		HashMap<Integer, String> crooms = receiveChatroomList();
		
		
		// le client donne le numéro de la chatroom qu'il souhaite
		String indexChatroom = "";
		String theme = "";
		int numero = -1;
		do{
			System.out.println("Quel chatroom souhaiter vous rejoindre ? Envoyez le numéro ou NEW pour créer une nouvelle");
			// on affiche les choix au client
			for (Entry<Integer, String> pair : crooms.entrySet()) {
				System.out.println(pair.getKey() + " : " + pair.getValue());
			}
			
			indexChatroom = sc.nextLine();
			//le client peut quitter au moment de choisir une chatroom
			if(indexChatroom.equalsIgnoreCase("/QUIT"))
				return false;
			// le client peut créer une chatroom
			else if(indexChatroom.equalsIgnoreCase("NEW")){
				System.out.println("Quel titre ?");
				theme = sc.nextLine();
				theme = theme.trim();
				if(theme.equals("")){
					System.out.println("Titre incorrect.");
				}
			}
			//on vérifie si le numéro saisi est correct
			else{
				try{
					numero = Integer.parseInt(indexChatroom);
					if(numero > crooms.size() - 1 || numero < 0 ){
						System.out.println("Mauvais numéro");
						numero = -1;
					}
				}catch(NumberFormatException e){
					//on affiche le message d'erreur
					numero = -1;
					System.out.println("Mauvais numéro");
				}
			}
		}while(numero == -1 && theme.equals(""));
		
		// on envoie le numéro et le theme, la création où l'affectation à une chatroom
		// sera traité par le serveur
		writer.writeObject(numero);
		writer.writeObject(theme);
		
		// on reçoit la réponse du serveur
		int response = (Integer) reader.readObject();
		return (response == Message.CONNECTED);
	}
	
	public boolean chooseChatroom(int numero) throws IOException, ClassNotFoundException {
		// on envoie le numéro et le theme, la création où l'affectation à une chatroom
		// sera traité par le serveur
		writer.writeObject(numero);
		writer.writeObject("");
		
		// on reçoit la réponse du serveur
		int response = (Integer) reader.readObject();
		return (response == Message.CONNECTED);
	}

	public HashMap<Integer, String> receiveChatroomList() {
		try {
			return (HashMap<Integer, String>) reader.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Déconnecte un client : ferme la socket et les flux
	 */
	public void disconnect() {
		running = false;
		System.out.println("Déconnexion");
		try {
			socket.close();
		} catch (IOException e) {
			//la socket est déjà fermée
		}
	}

	/**
	 * Permet à un client de s'identifier, il doit communiquer au serveur son nom et 
	 * son mot de passe. For the console application
	 * Si le nom n'est pas connu on lui propose de créer un compte
	 * @param sc Scanner pour les saisies claviers
	 * @return true si le client est identifié, false si mot de passe incorrect ou si
	 * l'utilisateur a choisi de ne pas créer de compte.
	 * @throws IOException 
	 */
	public boolean identification(Scanner sc) throws IOException{
		
		//l'échange commence par une demande d'authentification : demande du nom
		String reply = null;
		System.out.println("Votre nom ?");
		reply = sc.nextLine();
		writer.writeObject(reply); // client name
		
		//demande du mot de passe
		System.out.println("Votre mot de passe ?");
		reply = sc.nextLine();
		writer.writeObject(reply);
		
		int codeRetour = Message.WRONG_PWD;
		do{
			//attente de la réponse d'authentification
			try {
				//reception d'un code correspondant au résultat de l'identification
				codeRetour = (Integer) reader.readObject();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			System.out.println(Message.txtCodes[codeRetour]);
			switch(codeRetour){
				case Message.CONNECTED :
					return true;
				case Message.WRONG_PWD :
				case Message.SIGNIN_ABORT:
					return false;
				case Message.UNKNOWN_USER :
					reply = sc.nextLine();
					writer.writeObject(reply);
					// on va refaire un tour de boucle, nous avons besoin de savoir le traitement suivant (si l'utilsateur a décidé de s'enregistrer)
					break;
			}
		} while(codeRetour == Message.UNKNOWN_USER);
		return true;
	}
	
	/**
	 * Permet à un client de s'identifier, il doit communiquer au serveur son nom et 
	 * son mot de passe. For the GUI application
	 * @param nom user login 
	 * @param mdp user password
	 * @return return code message if the user has been authentified
	 */
	public int identification(String nom, String mdp){
		try {
			writer.writeObject(nom);		 // client name
			writer.writeObject(mdp);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		int codeRetour = Message.WRONG_PWD;
			//attente de la réponse d'authentification
			try {
				//reception d'un code correspondant au résultat de l'identification
				codeRetour = (Integer) reader.readObject();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return codeRetour;
	}
	
	public int accountCreation(String ret){
		int codeRetour = -1;
		try {
			writer.writeObject(ret);
		
			//reception d'un code correspondant au résultat de l'identification
			codeRetour = (Integer) reader.readObject();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return codeRetour;
	}
	
	/**
	 * Lis dans le stream des strings provenant de la session
	 * @return le string lu
	 */
	public String receive(){
		try {
			String s =  (String) reader.readObject();
			
			if(s.equals("Fermeture du serveur. Déconnexion")){
				running = false;
			}
			return s;
		} catch (IOException e) {
			running = false;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * Envoie des messages à la session
	 * @param s le string à envoyer
	 */
	public void send(String s){
		try {
			writer.writeObject(s);
		} catch (IOException e) {
			running = false;
		}
	}
}
