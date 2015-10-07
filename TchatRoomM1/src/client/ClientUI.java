package client;

import java.awt.EventQueue;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import client_gui.*;
import message.Message;

public class ClientUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ConnectionPanel connectPane;
	private ChatroomPanel chatPane;
	private JFrame frame;
	Client c;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
            try {
                ClientUI frame1 = new ClientUI();
                frame1.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
	}

	/**
	 * Create the frame.
	 */
	public ClientUI() {
		frame = this;
		this.c = new Client();
		setTitle("Chat Client");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		connectPane = new ConnectionPanel();
		setContentPane(connectPane);
		connectPane.setListener(new ConnectionMouse());
		
	}
	
	private class ConnectionMouse implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//get content from textboxes
			String name = connectPane.getLogin();
			String mdp = connectPane.getPassword();
			int authentification = c.identification(name, mdp);
			if(authentification == Message.UNKNOWN_USER){
				authentification = JOptionPane.showConfirmDialog(frame,
					    "Vous n'avez pas encore de compte.\n"
					    + "Voulez-vous le cr�er avec les informations entr�es pr�c�demment ?\n",
					    "Cr�ation de compte",
					    JOptionPane.YES_NO_OPTION);
				String reply = (authentification == JOptionPane.YES_OPTION) ? "O" : "N";
				authentification = c.accountCreation(reply);
			}
			connectPane.setAuthInformation(Message.txtCodes[authentification]);
			if(authentification == Message.CONNECTED){
				//if  the user is connected, he can access to the list of the chatroom, sent by the session
				HashMap<Integer, String> list = c.receiveChatroomList();
				chatPane = new ChatroomPanel();
				connectPane.setVisible(false);
				for (Entry<Integer, String> pair : list.entrySet()) {
					chatPane.addChatroomButton(pair.getValue(), new ChatroomMouse(pair.getKey()));
				}
				chatPane.addPanel();
				frame.setContentPane(chatPane);
			}			
		}
		
	}
	
	private class ChatroomMouse implements ActionListener{
		private int index;
		
		public ChatroomMouse(int index){
			super();
			this.index = index;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				if(c.chooseChatroom(index)){
					JOptionPane.showMessageDialog(frame, "Connect�");
				}
				else{
					JOptionPane.showMessageDialog(frame, "Non Connect�");
				}
			} catch (ClassNotFoundException | IOException e) {
				JOptionPane.showMessageDialog(frame, "Non Connect�");
			}
		}
		
	}

}