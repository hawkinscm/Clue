package hawkinscm.clue.model;

/**
 * Enumerator for each type of player.
 */
public enum PlayerType {
	HOST,
	NETWORK,
	COMPUTER_EASY,
	COMPUTER_MEDIUM,
	COMPUTER_HARD,
	NON_PLAYER,
	ALIBI_ONLY;
	
	/**
	 * Returns a short description of what type of player this is.
	 * @return a short description of what type of player this is
	 */
	public String toString() {
		switch (this) {
			case HOST :
				return "HOST";
			case NETWORK:
				return "NETWORK";
			case COMPUTER_EASY :
				return "EASY COM";
			case COMPUTER_MEDIUM :
				return "MEDIUM COM";
			case COMPUTER_HARD :
				return "HARD COM";
			case NON_PLAYER :
				return "NON-PLAYER";
			case ALIBI_ONLY :
				return "ALIBI ONLY";
			default :
				return null;
		}
	}
	
	/**
	 * Parses a Player Type from the given string.
	 * @param str string to parse
	 * @return the parsed Player Type; or null if string is not a valid Player Type string
	 */
	public static PlayerType parseType(String str) {
		for (PlayerType type : values())
			if (str.equals(type.toString()))
				return type;
		
		return null;
	}
}

