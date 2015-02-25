package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import message.Message;

public class Client {

	private static final int PORT_ECOUTE = 5050;
	private static final String HOST = "localhost";
	
	private Socket socket;

	public Client(){
		this.socket = null;
		try {

			socket = new Socket(HOST, PORT_ECOUTE);

		} catch (IOException ioe) {

			System.err.println("Connection failed"); 

			return;

		}
	}

	public static void main(String[] args) {
		Client c = new Client();
		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			// instanciation du flux entrant
			reader = new BufferedReader(new InputStreamReader(c.socket.getInputStream()));
			// instanciation du flux de sortie, avec autoflush à true
			writer = new PrintWriter(c.socket.getOutputStream(), true);
			
		} catch (IOException e) {
			System.err.println("Ouverture des flux impossible." + e.getMessage());
			System.exit(0);
		}
		
		//instanciation du scanner
		Scanner sc = new Scanner(System.in);
		
		//l'échange commence par une demande d'authentification : demande du nom
		String reply = null;
		System.out.println("Votre nom ?");
		reply = sc.nextLine();
		writer.println(reply); // client name
		
		//demande du mot de passe
		System.out.println("Votre mot de passe ?");
		reply = sc.nextLine();
		writer.println(reply);
		
		int codeRetour = Message.WRONG_PWD;
		do{
			//attente de la réponse d'authentification
			try {
				reply = reader.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//reception d'un code corespondant au résultat de l'identification
			codeRetour = Integer.parseInt(reply);
		
			System.out.println(Message.txtCodes[codeRetour]);
			switch(codeRetour){
				case Message.WRONG_PWD :
				case Message.SIGNIN_ABORT:
					// le programme s'arrête suite une saisie incorrecte du mot de passe ou d'un abandon d'enregistrement
					try {
						c.socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.exit(0);
					break;
				case Message.UNKNOWN_USER :
					reply = sc.nextLine();
					writer.println(reply);
					// on va refaire un tour de boucle, nous avons besoin de savoir le traitement suivant (si l'utilsateur a décidé de s'enregistrer)
					break;
			}
		} while(codeRetour == Message.UNKNOWN_USER);
		
		// A partir de ce point le user est identifié
		// affectation à une chat room
		//
		sc.close();
	}

}
