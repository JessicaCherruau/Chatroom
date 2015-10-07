package client_gui;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

public class ChatroomPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<JButton> buttonsList;
	private JPanel grid;
	/**
	 * Create the panel.
	 */
	public ChatroomPanel() {
		setLayout(null);
		
		JLabel lblChoississezUneChatroom = new JLabel("Choississez une chatroom");
		lblChoississezUneChatroom.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblChoississezUneChatroom.setBounds(10, 10, 200, 50);
		add(lblChoississezUneChatroom);
		
		grid = new JPanel();
		grid.setBounds(10, 71, 100, 100);
		
		grid.setLayout(new GridBagLayout());
		buttonsList = new ArrayList<JButton>();
	}
	
	public void addChatroomButton(String theme, ActionListener event){
		JButton button = new JButton(theme);
		button.addActionListener(event);
		buttonsList.add(button);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = buttonsList.size()-1;
		gbc.gridy = 0;

		gbc.fill = GridBagConstraints.HORIZONTAL;
		grid.add(buttonsList.get(buttonsList.size()-1), gbc);
		
	}
	
	public void addPanel(){
		this.add(grid);
	}
	
}
