package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import model.Room;
import model.Suspect;
import model.Weapon;

/**
 * Display for a CLUE detective notepad.
 */
public class DetectiveNotepadDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for a Detective Notepad
	 * @param owner parent/owner of dialog
	 * @param rooms list of game rooms
	 * @param suspects list of game suspects
	 * @param weapons list of game weapons
	 */
	public DetectiveNotepadDialog(JFrame owner, List<Room> rooms, List<Suspect> suspects, List<Weapon> weapons) {
		super(owner, "Detective Notepad");
		setResizable(true);
		JPanel mainPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(mainPanel);
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints mainc = new GridBagConstraints();
		mainc.gridx = 0;
		mainc.gridy = 0;
		mainc.insets = new Insets(5, 5, 5, 5);
		mainc.fill = GridBagConstraints.HORIZONTAL;
		mainc.weightx = 1.0;
		
		JPanel roomsPanel = new JPanel(new GridBagLayout());
		roomsPanel.setBorder(BorderFactory.createTitledBorder("ROOMS"));
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.insets = new Insets(1, 5, 1, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		for (Room room : rooms) {
			c.gridx = 0;
			JLabel nameLabel = new JLabel(room.getName());
			roomsPanel.add(nameLabel, c);
			c.gridx++;
			roomsPanel.add(new JCrossBox(nameLabel), c);
			c.gridx++;
			c.weightx = 1.0;
			roomsPanel.add(new UndoableTextField(10), c);
			c.weightx = 0;
			c.gridy++;
		}
		mainPanel.add(roomsPanel, mainc);
		
		mainc.gridy++;
		JPanel suspectsPanel = new JPanel(new GridBagLayout());
		suspectsPanel.setBorder(BorderFactory.createTitledBorder("SUSPECTS"));
		c = new GridBagConstraints();
		c.gridy = 0;
		c.insets = new Insets(1, 5, 1, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		for (Suspect suspect : suspects) {
			c.gridx = 0;
			JLabel nameLabel = new JLabel(suspect.getName());
			suspectsPanel.add(nameLabel, c);
			c.gridx++;
			suspectsPanel.add(new JCrossBox(nameLabel), c);
			c.gridx++;
			c.weightx = 1.0;
			suspectsPanel.add(new UndoableTextField(10), c);
			
			c.weightx = 0;
			c.gridy++;
		}
		mainPanel.add(suspectsPanel, mainc);
		
		mainc.gridy++;
		JPanel weaponsPanel = new JPanel(new GridBagLayout());
		weaponsPanel.setBorder(BorderFactory.createTitledBorder("WEAPONS"));
		c = new GridBagConstraints();
		c.gridy = 0;
		c.insets = new Insets(1, 5, 1, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		for (Weapon weapon : weapons) {
			c.gridx = 0;
			JLabel nameLabel = new JLabel(weapon.getName());
			weaponsPanel.add(nameLabel, c);
			c.gridx++;
			weaponsPanel.add(new JCrossBox(nameLabel), c);
			c.gridx++;
			c.weightx = 1.0;
			weaponsPanel.add(new UndoableTextField(10), c);
			c.weightx = 0;
			c.gridy++;
		}
		mainPanel.add(weaponsPanel, mainc);
		getContentPane().add(scrollPane);
		
		// Hide panel when F1 is typed
		JMenuBar menuBar = new JMenuBar();
		JMenuItem closeMenuItem = new JMenuItem();
		closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_F1, 0));
		closeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DetectiveNotepadDialog.this.setVisible(false);
			}
		});
		closeMenuItem.setSize(0, 0);
		menuBar.add(closeMenuItem);
		setJMenuBar(menuBar);
		menuBar.setPreferredSize(new Dimension(0, 0));

		pack();
		int maxX = Toolkit.getDefaultToolkit().getScreenSize().width - getWidth();
		int x = Math.min(maxX, owner.getLocation().x + owner.getWidth());
		int y = owner.getLocation().y - ((getHeight() - owner.getHeight()) / 2);
		setLocation(x, y);
	}
	
	/**
	 * Class for creating a checkbox that has an X rather than a check.
	 */
	private class JCrossBox extends JCheckBox {
		private static final long serialVersionUID = 1L;
		public JCrossBox(final JLabel label) {
			setIcon(ImageHelper.getEmptyCrossbox());
			setSelectedIcon(ImageHelper.getCrossbox());
			
			addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED)
						label.setText("<html><strike>" + label.getText().replaceAll("<[^>]*>", "") + "</strike></html>");
					else if (e.getStateChange() == ItemEvent.DESELECTED)
						label.setText(label.getText().replaceAll("<[^>]*>", ""));
				}
			});
		}
	}
}
