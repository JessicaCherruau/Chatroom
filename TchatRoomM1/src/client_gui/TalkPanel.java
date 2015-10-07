package client_gui;

import client.ClientUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Graphic interface for the conversation between participants
 */
public class TalkPanel extends JPanel{
    private JLabel lblBuddiesList;      //label containing the participants of the chatroom
    private JTextArea talkArea;         // contains the conversation
    private JButton btnSend;

    public JTextArea getMyMessageArea() {
        return myMessageArea;
    }

    public JTextArea getTalkArea() {
        return talkArea;
    }

    private JTextArea myMessageArea;    // input the message to send;

    public TalkPanel(){
        GridBagLayout gbl_this = new GridBagLayout();
//        gbl_this.columnWidths = new int[]{0, 0, 0};
//        gbl_this.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        this.setLayout(gbl_this);
        GridBagConstraints constraints = new GridBagConstraints();


        constraints.gridx = 0;
        constraints.gridy = 0;
        lblBuddiesList = new JLabel("*** buddies here ***");
        this.add(lblBuddiesList, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        JButton btnQuit = new JButton("Quitter");
        this.add(btnQuit, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        talkArea = new JTextArea();
        talkArea.setColumns(30);
        talkArea.setRows(10);
        talkArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(talkArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scrollPane, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        myMessageArea = new JTextArea();
        myMessageArea.setColumns(30);
        myMessageArea.setRows(3);
        JScrollPane scrollPane2 = new JScrollPane(myMessageArea);
        scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane2.setBounds(10, 11, 455, 249);
        this.add(scrollPane2, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        btnSend = new JButton("Envoyer");
        this.add(btnSend, constraints);
    }

    public void addSendListener(ActionListener sendListener) {
        btnSend.addActionListener(sendListener);
    }
}
