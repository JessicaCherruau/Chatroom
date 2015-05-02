package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Une chatroom est g�r�e par le serveur et contient plusieurs clients
 * Au sein de celle-ci sont ajout� dans une pile les messages � broadcaster � tous les participants 
 * 
 * @author Jessica CHERRUAU
 *
 */
public class ChatRoom extends Thread{
	
	private ArrayList<Chatteur> chatters;
	private String theme;
	private ConcurrentLinkedQueue<String> queue;	//pile thread-safe contenant les messages � broadcast
	private boolean running;	//permet de contr�ler l'ex�cution du thread
	
	/**
	 * Constructeur sans param�tre
	 * On initialise une liste de chatteurs et une file � vide
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
	 * On initialise une liste de chatteurs et une file � vide
	 * @param t le th�me de la chatroom
	 */
	public ChatRoom(String t){
		this();
		theme = t;
	}

	/**
	 * Le thread va lire en continue le contenu de la file
	 * La file contient les messages � transmettre
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
	 * Obtenir le th�me de la chatroom
	 * @return le th�me
	 */
	public String getTheme(){
		return theme;
	}
	
	/**
	 * Obtenir le nombre de participants � la chatroom
	 * @return nombre de participants
	 */
	public int getNbChatteurs(){
		return chatters.size();
	}

	/**
	 * Ajouter un chatteur � la chatroom
	 * @param chatteur
	 */
	public void addChatteur(Chatteur chatteur) {
		synchronized(chatters){
			chatters.add(chatteur);
		}
		putInQueue(this.listOfChatters());
		putInQueue(chatteur.getName() + " vient de se connecter");
	}
	
	/**
	 * Permet d'envoyer un message � tous les participants de la chatroom
	 * @param s le message � transmettre
	 */
	public synchronized void broadcast(String s){
		int i = 0;
		//pour tous les chatteurs
		while(i < chatters.size()){
			try {
				//on envoie le message � travers leur session
				chatters.get(i).getSession().send(s);
				i++;
			} catch (IOException e) {
				// on rentre dans cette partie du code lorsqu'on essaie d'envoyer un
				// message � travers une socket ferm�
				// on cherche quelle est la session a d�truire
				chatters.remove(i);
			}
		}
	}

	/**
	 * V�rifie si la session est g�r�e par cette chatroom
	 * @param idSession l'id de la session � v�rifier
	 * @return true si la session appartient bien � cette chatroom
	 */
	public boolean haveSession(int idSession) {
		Iterator<Chatteur> it = chatters.iterator();
		while (it.hasNext()){
			Chatteur c = it.next();
			// les id de sessions sont uniques
			if(c.getSession().getIdSession() == idSession)
				return true;
		}
		return false;
	}

	/**
	 * Met un message dans la file
	 * @param elem le message � transmettre
	 */
	public void putInQueue(String elem) {
		queue.offer(elem);
	}

	/**
	 * Ferme toutes les sessions de la chatroom
	 * Utile lors de la fermeture du serveur
	 */
	public void closeAllSessions(){
		Iterator<Chatteur> it = this.chatters.iterator();
		while(it.hasNext()){
			Chatteur c = it.next();
			c.getSession().disconnect();
		}
	}
	
	/**
	 * Retourne la liste des noms des chatteurs pr�sents dans la chatroom
	 * @return
	 */
	public String listOfChatters(){
		Iterator<Chatteur> it = null;
		String list = "*** ";
		synchronized(this.chatters){
			it = this.chatters.iterator();
		}
		while(it.hasNext()){
			list += it.next().getName() + ", ";
		}
		list += " ***";
		return list;
	}
}
