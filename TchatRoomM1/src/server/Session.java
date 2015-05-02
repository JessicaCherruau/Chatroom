package server;

import java.io.*;
import java.net.*;
import java.util.*;

import message.Message;

/**
 * Une session permet de gérer les différentes étapes par lequel doit passer un client pour communiquer : identification, 
 * choix de chatroom et communication
 * Gestion des flux d'entrées-sorties via la socket du client
 * 
 * @author Jessica CHERRUAU
 *
 */
public class Session extends Thread{
	private int idSession;
	private Socket clientSocket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Chatteur chatteur;
	
	public Session(int id, Socket s){
		this.idSession = id;
		this.clientSocket = s;
		try {
			out = new ObjectOutputStream(s.getOutputStream());
			in = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			System.out.println("Impossible d'établir les communications." + e.getMessage());
		}
	}
	
	/**
	 * Fonction du thread de la session
	 * Effectue toutes les fonctions du cycle de vie d'une session : identification du client, choix de la chatroom,
	 * communication dans la chatroom
	 */
	public void run(){
		try {
			System.out.println(this.idSession);
			if(identification()){
				System.out.println("connecté");
				try {
					if(chooseChatroom()){
						//on envoie la confirmation de connexion 
						out.writeObject(Message.CONNECTED);
						communicate();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				try {
					clientSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Lis les messages qu'envoie le client à travers la socket
	 * Analyse le message : si le message est /QUIT on arrête la lecture, sinon on continue et on envoie à la chatroom le message
	 */
	private void communicate() {
		String recu = "";
		do{
			try {
				recu = (String) in.readObject();
			} catch (IOException e) {
				recu = "/QUIT";
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				recu = "/QUIT";
			} 
			System.out.println("Session "+idSession+" reçoit "+recu);
			//on n'envoie pas le message /QUIT car c'est une commande pour la déconnexion
			if(recu.equals("/QUIT")){
				Server.transmitToChatRoom(idSession, chatteur.getName()+
						" a été déconnecté");
			}
			else{	
				Server.transmitToChatRoom(idSession, chatteur.getName()+
						" dit : "+recu);
			}
		} while (!recu.contains("/QUIT"));
	}

	/**
	 * Permet de relier le client à une chatroom
	 * @return si le client a bien choisi une chatroom et n'a pas quitté
	 * @throws IOException
	 */
	private boolean chooseChatroom() throws IOException {
		// récupération de la liste des chatrooms
		ArrayList<ChatRoom> crooms = Server.getChatrooms();
		
		// on prépare la map à envoyer au client
		HashMap<Integer, String> liste = new HashMap<Integer, String>();
		for(int i = 0 ; i < crooms.size() ; i++)
			liste.put(i, crooms.get(i).getTheme() + " : " + crooms.get(i).getNbChatteurs());
		
		//envoi de la liste
		out.writeObject(liste);
		
		//analyse de la réponse
		int numero = -1;
		String theme = "";
		try {
			numero = (Integer) in.readObject();
			theme = (String) in.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//si un theme a été renseigné il faut créer une nouvelle chatroom
		if(!theme.equals("")){
			numero = Server.createNewChatRoom(theme);
		}
		
		//affectation du chatter à la chatroom
		Server.affecterAChatroom(numero, chatteur);
		return true;
	}

	/**
	 * Etape d'identification
	 * Verfie le login et le mot de passe du client
	 * @return true si l'utilisateur existe ou que le compte a été crée, false si erreur de mot de passe ou si abandon de création de compte
	 * @throws ClassNotFoundException
	 */
	private boolean identification() throws ClassNotFoundException {
		String name = "";
		String pwd = "";
		System.out.println("Demande d'identification");

		//demande nom et mot de passe
		try {
			name = (String) in.readObject();
			pwd = (String) in.readObject();
		} catch (IOException e) {
			System.err.println("Erreur de lecture dans le flux input : " + e.getMessage());
		}

		// recherche du user de ce nom
		User client = Server.findUser(name);
		try {
			if(client != null){
				// vérification du mot de passe
				if(client.checkPassword(pwd)){
					// bon mot de passe il est connecté
					out.writeObject(Message.CONNECTED);
					setChatteur(new Chatteur(client.getName()));
					return true;
				}
				else{
					//mauvais mot de passe
					out.writeObject(Message.WRONG_PWD);
					return false;
				}
			}
			else{
				//le nom n'a pas été trouvé
				String reply = "";
				out.writeObject(Message.UNKNOWN_USER);
				try {
					reply = (String) in.readObject();
				} catch (IOException e) {
					System.err.println("Erreur de lecture dans le flux input : " + e.getMessage());
				}
				if(reply.toLowerCase().equals("o")){
					//le user est enregistré
					User newUser = new User(name, pwd);
					Server.addSubscriber(newUser);
					setChatteur(new Chatteur(newUser.getName()));
					out.writeObject(Message.CONNECTED);
					return true;
				}
				else
					//abandon de l'enregistrement
					out.writeObject(Message.SIGNIN_ABORT);
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Affecte un chatteur à la session
	 * @param u le chatteur identifié
	 */
	private void setChatteur(Chatteur u){
		if(u == null)
			throw new NullPointerException("Utilisateur inconnu");
		else{
			this.chatteur = u;
			this.chatteur.setSession(this);
		}
	}
	
	/**
	 * Envoie un message au client à travers la socket
	 * @param s le message à envoyer
	 * @throws IOException
	 */
	public void send(String s) throws IOException{
		out.writeObject(s);
	}
	
	/**
	 * Obtenir l'id de session
	 * @return
	 */
	public int getIdSession(){
		return this.idSession;
	}

	/**
	 * Déconnecte la session en fermant les flux
	 */
	public void disconnect() {
		try {
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
