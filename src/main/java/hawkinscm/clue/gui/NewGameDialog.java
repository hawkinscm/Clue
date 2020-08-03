package hawkinscm.clue.gui;

import hawkinscm.clue.action.DisplayMessageAction;
import hawkinscm.clue.model.Player;
import hawkinscm.clue.model.PlayerType;
import hawkinscm.clue.model.Suspect;
import hawkinscm.clue.socket.PlayerSocket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * Dialog used by a host for choosing players and setting options for a new game.
 */
public class NewGameDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	private final String WAIT_MESSAGE = "Waiting...";
	
	// Arrays of Drop Down lists for each player for type
	private JComboBox[] typeComboBoxes;
	private JTextField[] nameFields;
	private JLabel[] portLabels;
	
	private LinkedList<Player> newPlayers;
	private PlayerSocket[] playerSockets;
	
	/**
	 * Creates a new CreatePlayersDialog Dialog.
	 * @param gui frame that contains/owns this dialog
	 * @param suspects the suspects who will be in the game
	 */
	public NewGameDialog(final HostGUI gui, final List<Suspect> suspects) {
		super(gui, "New Game");
		
		newPlayers = null;
		playerSockets = new PlayerSocket[suspects.size()];
		
		c.insets = new Insets(7, 7, 7, 7);
		CustomButton helpButton = new CustomButton("Help") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				String message = "<html>" +
								 "The host IP displayed is the host IP of your local machine. If you are not behind a router this is the" + "<br>" +
								 "IP that network players will use to connect to you.  If you are behind a router you will need your external" + "<br>" +
								 "or router IP which can be found using http://www.whatismyip.com or by checking your router settings. You " + "<br>" +
								 "will need to make sure that if you are behind a router, you set up port forwarding for ports 55550 - 55558." + "<br>" +
								 "<br>" + 
								 "The port number will be displayed whenever you select a NETWORK Player Type. Give this port number and the" + "<br>" +
								 "correct IP to a network player to allow him to connect to your game." + "<br>" +
								 "<br>" + 
								 "Player Type Explainations:" + "<br>" +
								 "&nbsp;&nbsp;&nbsp;&nbsp; HOST - This is you, the host. One and only one player must be set to host." + "<br>" +
								 "&nbsp;&nbsp;&nbsp;&nbsp; NETWORK - Use this to allow someone to connect to you. Give them the IP and port to connect to." + "<br>" +
								 "&nbsp;&nbsp;&nbsp;&nbsp; EASY COM - Easy computer player comparable to a CLUE newbie." + "<br>" +
								 "&nbsp;&nbsp;&nbsp;&nbsp; MEDIUM COM - Medium computer player comparable to an average CLUE player." + "<br>" +
								 "&nbsp;&nbsp;&nbsp;&nbsp; HARD COM - Hard computer player comparable to an expert CLUE player." + "<br>" +
								 "&nbsp;&nbsp;&nbsp;&nbsp; NON-PLAYER - A Suspect who will not take turns or hold any cards." + "<br>" +
								 "&nbsp;&nbsp;&nbsp;&nbsp; ALIBI ONLY - A Suspect who will hold cards, but will not take turns (except to disprove a suggestion)." + "<br>" +
								 "<br>" + 
								 "You may use the default Suspect name or add your own name. If you add your own name it will be used in" + "<br>" +
								 "place of the Suspect's name throughout the game. Network players will also be allowed to change their names." +
								 "</html>";
				Messenger.display(message, "New Game Help");
				// create help button to explain player types and name overrides and ports/ips
			}
		};
		add(helpButton, c);
		
		c.gridx++;
		c.gridwidth = 2;
		JPanel infoPanel = new JPanel();
		infoPanel.add(new JLabel("Local Host IP:"));
		JTextField ipTextField = new JTextField(getHostIP()); 
		ipTextField.setBorder(null); 
		ipTextField.setOpaque(false); 
		ipTextField.setEditable(false); 
		infoPanel.add(ipTextField);
		add(infoPanel, c);
		c.gridwidth = 1;
		
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.NORTH;
		getContentPane().add(new JLabel("PLAYER TYPE"), c);

		c.gridx++;
		getContentPane().add(new JLabel("NAME"), c);
		
		c.gridx++;
		getContentPane().add(new JLabel("PORT"), c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.insets.top = 7;
		
		typeComboBoxes = new JComboBox[suspects.size()];
		nameFields = new JTextField[suspects.size()];
		portLabels = new JLabel[suspects.size()];
		
		for(int playerIndex = 0; playerIndex < suspects.size(); playerIndex++) {
			final int currentIndex = playerIndex;
			
			c.gridx = 0;
			c.gridy++;
						
			// Allow selecting of type: human or computer level (default is human)
			typeComboBoxes[playerIndex] = new JComboBox(PlayerType.values());
			final int currentPlayerIndex = playerIndex;
			typeComboBoxes[playerIndex].addActionListener(e -> {
				if ((PlayerType)typeComboBoxes[currentPlayerIndex].getSelectedItem() == PlayerType.NETWORK) {
					if (playerSockets[currentPlayerIndex] != null)
						playerSockets[currentPlayerIndex].close();

					nameFields[currentPlayerIndex].setText(WAIT_MESSAGE);
					nameFields[currentPlayerIndex].setEnabled(false);
					playerSockets[currentPlayerIndex] = new PlayerSocket(currentPlayerIndex);
					if (playerSockets[currentPlayerIndex].getPortIndex() < 0) {
						typeComboBoxes[currentPlayerIndex].setSelectedItem(PlayerType.NON_PLAYER);
						nameFields[currentPlayerIndex].setEnabled(true);
						nameFields[currentPlayerIndex].setText(suspects.get(currentPlayerIndex).getName());
						return;
					}
					portLabels[currentPlayerIndex].setText("" + playerSockets[currentPlayerIndex].getPortIndex());

					Thread connectThread = new Thread() {
						@Override
						public void run() {
							String playerName = playerSockets[currentPlayerIndex].connect(suspects.get(currentPlayerIndex).getName());
							if (playerName == null)
								return;
							nameFields[currentPlayerIndex].setText(playerName);
							StringBuilder playerMessage = new StringBuilder("<html><center> Current players connected to host: " + nameFields[currentPlayerIndex].getText());
							for (int playerIndex1 = 0; playerIndex1 < suspects.size(); playerIndex1++) {
								if (playerIndex1 == currentPlayerIndex)
									continue;
								PlayerSocket playerSocket = playerSockets[playerIndex1];
								if (playerSocket != null && playerSocket.isConnected())
									playerMessage.append(", ").append(nameFields[playerIndex1].getText());
							}
							playerMessage.append(" <br> Waiting on host to begin the game... </center></html>");

							String actionMessage = new DisplayMessageAction().createMessage(playerMessage.toString());
							for (int playerIndex1 = 0; playerIndex1 < suspects.size(); playerIndex1++) {
								PlayerSocket playerSocket = playerSockets[playerIndex1];
								if (playerSocket != null && playerSocket.isConnected())
									playerSocket.sendActionMessage(actionMessage);
							}
						}
					};
					connectThread.start();
				}
				else {
					if (nameFields[currentPlayerIndex] != null) {
						nameFields[currentPlayerIndex].setEnabled(true);
						if (nameFields[currentPlayerIndex].getText().equals(WAIT_MESSAGE)) {
							nameFields[currentPlayerIndex].setText(suspects.get(currentPlayerIndex).getName());
						}
						if (portLabels[currentPlayerIndex] != null)
							portLabels[currentPlayerIndex].setText("-");
						if (playerSockets[currentPlayerIndex] != null)
							playerSockets[currentPlayerIndex].close();
					}
				}
			});
			getContentPane().add(typeComboBoxes[playerIndex], c);
			
			c.gridx++;
			// Allow entering of name			
			nameFields[playerIndex] = new JTextField(10);
			nameFields[playerIndex].addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent arg0) {
					nameFields[currentIndex].selectAll();				
				}
				public void focusLost(FocusEvent arg0) {}
			});
			getContentPane().add(nameFields[playerIndex], c);
			
			c.gridx++;
			portLabels[playerIndex] = new JLabel("-");
			getContentPane().add(portLabels[playerIndex], c);
						
			typeComboBoxes[playerIndex].setSelectedItem(PlayerType.NON_PLAYER);
			nameFields[playerIndex].setText(suspects.get(playerIndex).getName());
		}
		
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 3;
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints panelc = new GridBagConstraints();
		panelc.gridx = 0;
		panelc.gridy = 0;
		panelc.insets = new Insets(10, 15, 0 ,15);
		// Button that signals the completion of player creation input and uses the input to create the players.
		CustomButton okButton = new CustomButton("OK") {
			private static final long serialVersionUID = 1L;
			
			public void buttonClicked() {
				newPlayers = new LinkedList<>();
				int hostCount = 0;
				for(int playerIndex = 0; playerIndex < nameFields.length; playerIndex++) {
					String name = nameFields[playerIndex].getText().replaceAll("[^\\w .']", "").trim();
					if (name.equals("")) {
						name = suspects.get(playerIndex).getName().trim();
						if (name.equals(""))
							name = "Suspect" + (playerIndex + 1);
					}
					PlayerType playerType = (PlayerType) typeComboBoxes[playerIndex].getSelectedItem();
					if (playerType == PlayerType.HOST)
						hostCount++;
					
					Player player;
					PlayerSocket playerSocket = playerSockets[playerIndex];
					if (playerSocket != null && playerSocket.getPlayer() != null) {
						player = playerSocket.getPlayer();
					}
					else
						player = new Player(suspects.get(playerIndex), playerType);
					
					for (Player currentPlayer : newPlayers) {
						if (name.equals(currentPlayer.getName())) {
							String message = "You cannot have two players with the same name: \"" + player + "\"";
							Messenger.error(message, "Duplicate Name Error", gui);
							newPlayers = null;
							return;
						}
						else if (nameFields[playerIndex].getText().equals(WAIT_MESSAGE)) {
							String message = "Still waiting for network participants to connect.";
							Messenger.error(message, "Waiting For Network Players", gui);
							newPlayers = null;
							return;
						}
					}

					newPlayers.add(player);
					suspects.get(playerIndex).setName(name);
				}
				
				if (hostCount != 1) {
					String message = "You must have one and only one host.";
					Messenger.error(message, "Host Error", gui);
					newPlayers = null;
					return;
				}
				
				for (Player player : newPlayers)
					if (player.getPlayerType() == PlayerType.HOST)
						gui.setControlledPlayer(player);
				
				dispose();
			}
		};
		buttonPanel.add(okButton, panelc);
		
		panelc.gridx++;
		// Button that will open the Options Display
		CustomButton optionsButton = new CustomButton("Options...") {
			private static final long serialVersionUID = 1L;
			
			public void buttonClicked() {
				gui.displayOptions();
			}
		};
		buttonPanel.add(optionsButton, panelc);
				
		panelc.gridx++;
		// Button that will cancel this dialog and not create any new players
		CustomButton cancelButton = new CustomButton("Cancel") {
			private static final long serialVersionUID = 1L;
			
			public void buttonClicked() {
				for (PlayerSocket playerSocket : playerSockets)
					if (playerSocket != null && !playerSocket.isConnected())
						playerSocket.close();
				
				dispose();
			}
		};
		buttonPanel.add(cancelButton, panelc);
		getContentPane().add(buttonPanel, c);
		
		refresh();
		okButton.requestFocus();
	}
	
	/**
	 * Returns the local IP of the host.
	 * @return the local IP of the host OR an error message if network not found
	 */
	private String getHostIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} 
		catch (UnknownHostException e) {}
		return "NETWORK NOT FOUND";
	}
	
	/**
	 * Returns the players created by this dialog.
	 * @return the players for the game
	 */
	public LinkedList<Player> getPlayers() {	
		return newPlayers;
	}
	
	/**
	 * Returns a list of all connected network player sockets.
	 * @return a list of all connected network player sockets
	 */
	public LinkedList<PlayerSocket> getPlayerSockets() {
		LinkedList<PlayerSocket> playerSocketList = new LinkedList<>();
		for (int playerIndex = 0; playerIndex < newPlayers.size(); playerIndex++) {
			Player player = newPlayers.get(playerIndex);
			if (player.getPlayerType() == PlayerType.NETWORK) {
				if (playerSockets[playerIndex].getPlayer() == null)
					playerSockets[playerIndex].setPlayer(player);
				playerSocketList.add(playerSockets[playerIndex]);
			}
		}
		return playerSocketList;
	}
}
