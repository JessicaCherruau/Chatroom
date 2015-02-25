package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
	
	private static final int PORT_ECOUTE = 5050;
	private static int NB_USERS = 0;
	private static ArrayList<User> subscribers;
	private static ArrayList<ChatRoom> crooms;
	
	private Server(){
		//instanciation des variables
		crooms = new ArrayList<ChatRoom>();
		subscribers = new ArrayList<User>();
		System.out.println("Initialisation de la liste des utilisateurs inscrits...");
		initSubscribers();
		System.out.println("Serveur démarré.");
		
		try {
			ServerSocket serverSocket = new ServerSocket(PORT_ECOUTE);
			
			while(true){
				Socket clientSocket = serverSocket.accept();
				new Session(clientSocket).start();
			}
		} catch (IOException e) {
			System.err.println("Erreur d'entrée sortie : " + e.getMessage());
		}
	}

	/**
	 * Initialise la liste d'inscrits à partir de...
	 */
	private void initSubscribers() {
		User u1 = new User("Toto", "mdp");
		User u2 = new User("Jess", "pwd");
		Server.subscribers.add(u1);
		Server.subscribers.add(u2);
	}

	public static void main(String[] args) {
		new Server();
	}
	
	/**
	 * Trouve un User à partir de son nom dans la liste des inscrits
	 * @param nameUser le nom du User à chercher
	 * @return le User correspondant
	 */
	public static User findUser(String nameUser){
		// création d'un itérateur sur la liste
		Iterator<User> iterator = Server.subscribers.iterator();
		
		//conversion de la chaine de caractères sans la casse
		nameUser = nameUser.toLowerCase();
		
		//parcours de la liste
		while(iterator.hasNext()){
			User u = iterator.next();
			//si le nom est trouvé on retourne le user
			if(u.getName().toLowerCase().equals(nameUser))
				return u;
		}
		
		// si le nom n'a pas été trouvé lors du parcours de l'itérateur, le User de ce nom n'existe pas
		return null;
		
	}
	
	/**
	 * Ajoute un user à la liste des inscrits
	 * @param u le user à ajouter
	 */
	public static void addSubscriber(User u){
		if(u != null)
			Server.subscribers.add(u);
	}

}
