package hawkinscm.clue.action;

import java.util.List;

import hawkinscm.clue.model.Card;
import hawkinscm.clue.model.Room;
import hawkinscm.clue.model.Suspect;
import hawkinscm.clue.model.Weapon;
import hawkinscm.clue.gui.Messenger;

/**
 * Abstract class representing an action that can be communicated via text across a network.
 * Every class that inherits Action must have an empty constructor in order for the parseAction method to work.
 */
public abstract class Action<T> {

	private String message;

	protected final static String MAIN_DELIM = ";";
	protected final static String INNER_DELIM = ",";

	private final static String ROOM_PREFIX = "r.";
	private final static String SUSPECT_PREFIX = "s.";
	private final static String WEAPON_PREFIX = "w.";

	/**
	 * Sets the message for this action.
	 * @param message action message to set
	 */
	protected void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns the action message without the action class specifier heading.
	 * @return the action message without the action class specifier heading
	 */
	protected String getMessageWithoutClassHeader() {
		int firstDividerIndex = message.indexOf(MAIN_DELIM);
		if (firstDividerIndex == -1)
			return MAIN_DELIM;
		return message.substring(firstDividerIndex + 1);
	}

	/**
	 * Parses and returns a card from the given card message.
	 * @param cardMessage card message to parse
	 * @param rooms list of rooms with which the card may be defined
	 * @param suspects list of rooms with which the card may be defined
	 * @param weapons list of rooms with which the card may be defined
	 * @return the parsed card
	 */
	protected Card parseCard(String cardMessage, List<Room> rooms, List<Suspect> suspects, List<Weapon> weapons) {
		if (cardMessage.startsWith("null"))
			return null;

		int id = Integer.parseInt(cardMessage.substring(2));
		if (cardMessage.startsWith(ROOM_PREFIX)) {
			for (Room room : rooms)
				if (room.getId() == id)
					return new Card(room);
		}
		else if (cardMessage.startsWith(SUSPECT_PREFIX)) {
			for (Suspect suspect : suspects)
				if (suspect.getId() == id)
					return new Card(suspect);
		}
		else if (cardMessage.startsWith(WEAPON_PREFIX)) {
			for (Weapon weapon : weapons)
				if (weapon.getId() == id)
					return new Card(weapon);
		}
		throw new IllegalArgumentException("Unable to create card for card message: " + cardMessage);
	}

	/**
	 * Creates a data message from the given card.
	 * @param card card to convert to a string
	 * @return a data message from the fiven card
	 */
	protected String generateCardMessage(Card card) {
		if (card == null)
			return "null";
		if (card.getRoom() != null)
			return ROOM_PREFIX + card.getRoom().getId();
		else if (card.getSuspect() != null)
			return SUSPECT_PREFIX + card.getSuspect().getId();
		else if (card.getWeapon() != null)
			return WEAPON_PREFIX + card.getWeapon().getId();
		throw new IllegalArgumentException("Invalid card passed in");
	}

	/**
	 * Returns this action as a string message.
	 * @return this action as a string message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Performs this action using the given object.
	 * @param obj object needed to performing the action.
	 * @return return an array of reply messages or null if not applicable
	 */
	public abstract String[] performAction(T obj);

	/**
	 * Returns the class that the class requires in order to handle a message.
	 * @return the class that the class requires in order to handle a message
	 */
	public abstract Class<T> getActionTypeClass();

	/**
	 * Parses the given action message then creates and returns an Action ready to be performed.
	 * @param actionMessage action message to parse
	 * @return the parsed Action
	 */
	public static Action<?> parseAction(String actionMessage) {
		try {
			String className = actionMessage;
			int classNameEndIndex = actionMessage.indexOf(MAIN_DELIM);
			if (classNameEndIndex > 0)
				className = actionMessage.substring(0, classNameEndIndex);
			try {
				Action<?> parsedAction = (Action<?>)Class.forName(className).newInstance();
				parsedAction.setMessage(actionMessage);
				return parsedAction;
			}
			catch (ClassNotFoundException ex) {
				return new Action<Object>() {
					@Override
					public String[] performAction(Object obj) {
						return null;
					}

					@Override
					public Class<Object> getActionTypeClass() {
						return Object.class;
					}

				};
			}
		}
		catch (Exception ex) {
			Messenger.error(ex, ex.getMessage() + ": \"" + actionMessage + "\"", "Illegal Action Message");
		}

		return null;
	}
}
