package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Une chatroom est gérée par le serveur et contient plusieurs clients
 * Au sein de celle-ci sont ajouté dans une pile les messages à broadcaster à tous les participants 
 * 
 * @author Jessica CHERRUAU
 *
 */
public class ChatRoom extends Thread{
	
	private ArrayList<Chatteur> chatters;
	private String theme;
	private ConcurrentLinkedQueue<String> queue;	//pile thread-safe contenant les messages à broadcast
	private boolean running;	//permet de contrôler l'exécution du thread
	
	/**
	 * Constructeur sans paramètre
	 * On initialise une liste de chatteurs et une file à vide
	 */
	public ChatRoom(){
		chatters = new ArrayList<Chatteur>();
		queue = new ConcurrentLinkedQueue<String>();
		theme = "General topic";
		running = true;
		this.start();
	}
	
	/**
	 * Constructeur
	 * On initialise une liste de chatteurs et une file à vide
	 * @param t le thème de la chatroom
	 */
	public ChatRoom(String t){
		this();
		theme = t;
	}

	/**
	 * Le thread va lire en continue le contenu de la file
	 * La file contient les messages à transmettre
	 */
	public void run(){
		while(running){
			while(!queue.isEmpty()){
				String msg = queue.poll();
				System.out.println("Broadcast : "+msg);
				broadcast(msg);
			}
		}

	}
	
	/**
	 * Obtenir le thème de la chatroom
	 * @return le thème
	 */
	public String getTheme(){
		return theme;
	}
	
	/**
	 * Obtenir le nombre de participants à la chatroom
	 * @return nombre de participants
	 */
	public int getNbChatteurs(){
		return chatters.size();
	}

	/**
	 * Ajouter un chatteur à la chatroom
	 * @param chatteur chatteur à ajouter
	 */
	public void addChatteur(Chatteur chatteur) {
		synchronized(chatters){
			chatters.add(chatteur);
		}
		putInQueue(this.listOfChatters());
		putInQueue(chatteur.getName() + " vient de se connecter");
	}
	
	/**
	 * Permet d'envoyer un message à tous les participants de la chatroom
	 * @param s le message à transmettre
	 */
	public synchronized void broadcast(String s){
		int i = 0;
		//pour tous les chatteurs
		while(i < chatters.size()){
			try {
				//on envoie le message à travers leur session
				chatters.get(i).getSession().send(s);
				i++;
			} catch (IOException e) {
				// on rentre dans cette partie du code lorsqu'on essaie d'envoyer un
				// message à travers une socket fermé
				// on cherche quelle est la session a détruire
				chatters.remove(i);
			}
		}
	}

	/**
	 * Vérifie si la session est gérée par cette chatroom
	 * @param idSession l'id de la session à vérifier
	 * @return true si la session appartient bien à cette chatroom
	 */
	public boolean haveSession(int idSession) {
		for (Chatteur c : chatters) {
			// les id de sessions sont uniques
			if (c.getSession().getIdSession() == idSession)
				return true;
		}
		return false;
	}

	/**
	 * Met un message dans la file
	 * @param elem le message à transmettre
	 */
	public void putInQueue(String elem) {
		queue.offer(elem);
	}

	/**
	 * Ferme toutes les sessions de la chatroom
	 * Utile lors de la fermeture du serveur
	 */
	public void closeAllSessions(){
		for (Chatteur c : this.chatters) {
			c.getSession().disconnect();
		}
	}
	
	/**
	 * Retourne la liste des noms des chatteurs présents dans la chatroom
	 * @return liste des noms des chatteurs présents dans un string
	 */
	public String listOfChatters(){
		String list = "*** ";
		synchronized(this.chatters){
			for(Chatteur chatter : this.chatters)
				list += chatter.getName() + ", ";
		}
		list += " ***";
		return list;
	}
}
