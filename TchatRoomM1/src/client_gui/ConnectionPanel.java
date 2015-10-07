package client_gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Connection Form
 * two fields for login and password, and a button
 * @author Jessica CHERRUAU
 *
 */
public class ConnectionPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField txtLogin;
	private JPasswordField txtPassword;
	private JLabel lblBienvenue;
	private JButton btnConnexion;
	
	/**
	 * Create the panel.
	 */
	public ConnectionPanel() {
		GridBagLayout gbl_this = new GridBagLayout();
		gbl_this.columnWidths = new int[]{0, 0, 0};
		gbl_this.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		this.setLayout(gbl_this);
		
		lblBienvenue = new JLabel("Bienvenue");
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 5, 0);
		gbc.gridx = 1;
		gbc.gridy = 0;
		this.add(lblBienvenue, gbc);
		
		JLabel lblLogin = new JLabel("Login");
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 0;
		gbc.gridy = 1;
		this.add(lblLogin, gbc);
		
		txtLogin = new JTextField();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 1;
		this.add(txtLogin, gbc);
		txtLogin.setColumns(10);
		
		JLabel lblMotDePasse = new JLabel("Mot de passe");
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 0;
		gbc.gridy = 2;
		this.add(lblMotDePasse, gbc);
		
		txtPassword = new JPasswordField();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 2;
		this.add(txtPassword, gbc);
		
		btnConnexion = new JButton("Connexion");
		gbc.gridx = 1;
		gbc.gridy = 3;
		this.add(btnConnexion, gbc);
	}
	
	/**
	 * Add an action listener to the button of the form
	 * @param event action listener à ajouter
	 */
	public void setListener(ActionListener event){
		btnConnexion.addActionListener(event);
	}
	
	/**
	 * 
	 * @return content of the login field
	 */
	public String getLogin(){
		return txtLogin.getText().trim();
	}
	
	/**
	 * 
	 * @return content of the password field
	 */
	public String getPassword(){
		return new String(txtPassword.getPassword());
	}
	
	public void setAuthInformation(String text){
		lblBienvenue.setText(text);
	}

}
