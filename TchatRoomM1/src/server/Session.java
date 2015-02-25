package server;

import java.io.*;
import java.net.*;

import message.Message;

public class Session extends Thread{
	private Socket clientSocket;
	private BufferedReader in;
	private PrintWriter out;
	
	public Session(Socket s){
		this.clientSocket = s;
		try {
			in = new BufferedReader(	new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(s.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("Impossible d'établir les communications." + e.getMessage());
		}
	}
	
	public void run(){
		if(identification()){
			out.println("Connecté");
			System.out.println("connecté");
		}
		else{
			try {
				out.println("Déconnecté");
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		System.out.println("haha");
//		String name;
//		try {
//			name = in.readLine();
//			System.out.println("reçu " +name);
//			out.println("Hello " + name);
//			System.out.println("envoi Hello " +name);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		out.flush();
	}

	private boolean identification() {
		String name = "";
		String pwd = "";
		System.out.println("Demande d'identification");
		
		//demande nom et mot de passe
		try {
			name = in.readLine();
			pwd = in.readLine();
		} catch (IOException e) {
			System.err.println("Erreur de lecture dans le flux input : " + e.getMessage());
		}
		
		// recherche du user de ce nom
		User client = Server.findUser(name);
		if(client != null){
			// vérification du mot de passe
			if(client.checkPassword(pwd)){
				// bon mot de passe il est connecté
				out.println(Message.CONNECTED);
				return true;
			}
			else{
				//mauvais mot de passe
				out.println(Message.WRONG_PWD);
				return false;
			}
		}
		else{
			//le nom n'a pas été trouvé
			String reply = "";
			out.println(Message.UNKNOWN_USER);
			try {
				reply = in.readLine();
			} catch (IOException e) {
				System.err.println("Erreur de lecture dans le flux input : " + e.getMessage());
			}
			if(reply.equals("O")){
				//le user est enregistré
				User newUser = new User(name, pwd);
				Server.addSubscriber(newUser);
				out.println(Message.CONNECTED);
				return true;
			}
			else
				//abandon de l'enregistrement
				out.println(Message.SIGNIN_ABORT);
				return false;
		}
	}
}
