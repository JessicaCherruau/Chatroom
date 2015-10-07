package server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Application Serveur
 * 
 * Cr�er un serversocket qui accepte des clients et initialise des sessions pour chaque client
 * Le server sauvegarde les utilisateurs ayant des identifiants
 * 
 * On peut quitter le serveur en tapant /quit
 * 
 * @author Jessica CHERRUAU
 *
 */

public class Server extends Thread{
	
	private static final int PORT_ECOUTE = 5050;
	private static final String subscribersFile = "subscribers.txt";	//chemin du fichier contenant la liste des inscrits
	private static int NB_USERS = 1;	
	private static ServerSocket serverSocket;
	private static ArrayList<User> subscribers;		//liste des inscrits
	private static ArrayList<ChatRoom> crooms;		//liste des chatrooms
	
	private Server(){
		//instanciation des variables
		crooms = new ArrayList<ChatRoom>();
		Server.createNewChatRoom("G�n�ral");
		subscribers = new ArrayList<User>();
		System.out.println("Initialisation de la liste des utilisateurs inscrits...");
		initSubscribers();
		System.out.println("Serveur d�marr�.");
		this.start();
		try {
			serverSocket = new ServerSocket(PORT_ECOUTE);
			
			while(true){
				synchronized(this){
					Socket clientSocket = serverSocket.accept();
					new Session(Server.NB_USERS++, clientSocket).start();
				}
			}
		} catch (IOException e) {
			//on entre dans le catch lorsqu'on a ferm� le serverSocket (voir run())
			//il faut fermer les sessions et enregistrer les user
			System.out.println("Enregistrement des inscrits...");
			saveSubscribers();
			
			//d�connexion de toutes les sessions de toutes les chatrooms
			for (ChatRoom cr : crooms) {
				cr.broadcast("Fermeture du serveur. D�connexion");
				cr.closeAllSessions();
			}
			System.out.println("Fermeture du serveur.");
			System.exit(0);
		}
	}
	
	/**
	 * Ce thread va v�rifier les saisies faites dans la console Server et permet de fermer le serveur en tapant "/quit"
	 */
	public void run(){
		Scanner sc = new Scanner(System.in);
		String chaine= "";
		System.out.println("Saisir /QUIT pour fermer le serveur");
		do{
			chaine = sc.nextLine();
			
		}while(!chaine.toLowerCase().equals("/quit"));
		
		// quand on saisit "/quit" dans la console Server, il doit s'arr�ter
		//on ferme la serverSocket, ce qui va entra�ner une exception dans le constructeur pour arr�ter le serveur
		try {
			sc.close();
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initialise la liste d'inscrits � partir d'un fichier
	 */
	@SuppressWarnings("unchecked")
	private void initSubscribers() {
		try {
			// retrouve les User pr�c�demment s�rialis�s dans le fichiers
			ObjectInputStream mFile = new ObjectInputStream(new FileInputStream(subscribersFile));
			Server.subscribers = (ArrayList<User>) mFile.readObject();
			mFile.close();
		} catch (IOException e) {
			System.out.println("Fichier subscribers est vide.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
//		User u1 = new User("Toto", "mdp");
//		User u2 = new User("Jess", "pwd");
//		Server.subscribers.add(u1);
//		Server.subscribers.add(u2);
	}
	
	/**
	 * Enregistre les users dans un fichier pour pouvoir les retrouver au prochain d�marrage
	 */
	private void saveSubscribers() {
		try {
			FileOutputStream sortie = new FileOutputStream(subscribersFile);
			ObjectOutputStream os = new ObjectOutputStream(sortie);
			os.writeObject(Server.subscribers);
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server();
	}
	
	/**
	 * Trouve un User � partir de son nom dans la liste des inscrits
	 * @param nameUser le nom du User � chercher
	 * @return le User correspondant
	 */
	public static User findUser(String nameUser){
		// cr�ation d'un it�rateur sur la liste
		Iterator<User> iterator = Server.subscribers.iterator();
		
		//conversion de la chaine de caract�res sans la casse
		nameUser = nameUser.toLowerCase();
		
		//parcours de la liste
		while(iterator.hasNext()){
			User u = iterator.next();
			//si le nom est trouv� on retourne le user
			if(u.getName().toLowerCase().equals(nameUser))
				return u;
		}
		
		// si le nom n'a pas �t� trouv� lors du parcours de l'it�rateur, le User de ce nom n'existe pas
		return null;
		
	}
	
	/**
	 * Ajoute un user � la liste des inscrits
	 * @param u le user � ajouter
	 */
	public static void addSubscriber(User u){
		if(u != null)
			Server.subscribers.add(u);
	}

	/**
	 * Obtenir la liste des chatrooms
	 * @return liste des chatrooms
	 */
	public static ArrayList<ChatRoom> getChatrooms() {
		return Server.crooms;
	}

	/**
	 * Cr�e une nouvelle chatroom dans la liste des chatrooms du serveur
	 * @param theme le theme � affecter � la chatroom
	 * @return l'index dans la liste de la chatroom cr��e
	 */
	public static int createNewChatRoom(String theme) {
		int index = -1;
		synchronized(Server.crooms){
			Server.crooms.add(new ChatRoom(theme));
			index = crooms.size()-1;
		}
		return index;
	}

	/**
	 * Affecte un chatteur � une chatroom donn�e
	 * @param numero le num�ro de la chatroom
	 * @param chatteur le chatteur � int�grer
	 */
	public static void affecterAChatroom(int numero, Chatteur chatteur) {
		synchronized(Server.crooms){
			Server.crooms.get(numero).addChatteur(chatteur);
		}
	}
	
	/**
	 * Permet de transmettre un message d'un client � la chatroom � laquelle il appartient
	 * @param idSession id de session du client
	 * @param elem message � transmettre
	 */
	public static synchronized void transmitToChatRoom(int idSession, String elem){
		//on parcourt toutes les chatrooms
		for (ChatRoom cr : Server.crooms) {
			if (cr.haveSession(idSession)) {
				System.out.println("Message transmis � " + cr.getTheme());
				//on ajoute le message dans la file de la chatroom
				cr.putInQueue(elem);
				break;
			}
		}
	}

}
