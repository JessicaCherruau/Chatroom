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
	private TalkPanel talkPane;
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
			int authentication = c.identification(name, mdp);
			if(authentication == Message.UNKNOWN_USER){
				authentication = JOptionPane.showConfirmDialog(frame,
					    "Vous n'avez pas encore de compte.\n"
					    + "Voulez-vous le créer avec les informations entrées précédemment ?\n",
					    "Création de compte",
					    JOptionPane.YES_NO_OPTION);
				String reply = (authentication == JOptionPane.YES_OPTION) ? "O" : "N";
				authentication = c.accountCreation(reply);
			}
			connectPane.setAuthInformation(Message.txtCodes[authentication]);
			if(authentication == Message.CONNECTED){
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
//					JOptionPane.showMessageDialog(frame, "Connecté");
					talkPane = new TalkPanel();
					chatPane.setVisible(false);
					frame.setContentPane(talkPane);
					talkPane.addSendListener(new SendListener());
					new Thread(new DisplayUpdater()).start();
				}
				else{
					JOptionPane.showMessageDialog(frame, "Non Connecté");
				}
			} catch (ClassNotFoundException | IOException e) {
				JOptionPane.showMessageDialog(frame, "Non Connecté");
			}
		}
		
	}

	private class SendListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String message = talkPane.getMyMessageArea().getText();
			c.send(message);
			talkPane.getMyMessageArea().setText("");
		}
	}

	private class DisplayUpdater implements Runnable{

		@Override
		public void run() {
			while(true){
				String message = c.receive();
				talkPane.getTalkArea().setText(talkPane.getTalkArea().getText()+"\n"+message);
			}
		}
	}

}
