package model;

import gui.DisplayTile;
import gui.Messenger;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import xml.XMLException;
import xml.XMLReader;
import xml.XMLTag;
import xml.XMLWriter;

/**
 * Handles the reading and writing of CLUE game data.
 */
public class GameFileIOHandler {

	/**
	 * Writes the CLUE game data as XML to a file.
	 * @param defaultGameFile default file to offer as file choice for saving.
	 * @param gameData CLUE game data to save
	 * @return the chosen file were the game was saved or null if canceled or unable to save
	 */
	public static File writeToFile(File defaultGameFile, ClueGameData gameData) {
		JFileChooser chooser = new JFileChooser("ccgs/");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Custom Clue Game (*.ccg)", "ccg");
		chooser.setFileFilter(filter);
		chooser.setSelectedFile(defaultGameFile);
		int returnVal = chooser.showSaveDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return null;		
		File selectedFile = chooser.getSelectedFile();
		if (!selectedFile.getName().endsWith(".ccg"))
			selectedFile = new File(selectedFile.getAbsolutePath() + ".ccg");
		
		XMLTag rootTag = convertToXML(gameData);
		
		try {
			XMLWriter.writeXMLToFile(rootTag, selectedFile);
			return selectedFile;
		} 
		catch (XMLException ex) {
			Messenger.error(ex, "Unable to write to save file: " + selectedFile.getAbsolutePath(), "Game Creation Save Error");
			return null;
		}
	}
	
	/**
	 * Writes the given CLUE game data to an XML string.
	 * @param gameData CLUE game data to convert to XML
	 * @return the XML representation of the given CLUE game data
	 */
	public static String writeToString(ClueGameData gameData) {
		try {
			XMLTag rootTag = convertToXML(gameData);
			return XMLWriter.writeXMLToString(rootTag);
		}
		catch (XMLException ex) {
			Messenger.error(ex, "Unable to parse generated XML to string.", "Game XML Conversion Error");
			return null;
		}
	}
	
	/**
	 * Converts the given CLUE game data XML Tags.
	 * @param gameData CLUE game data to convert
	 * @return the root XML Tag
	 */
	private static XMLTag convertToXML(ClueGameData gameData) {
		XMLTag rootTag = new XMLTag();
		XMLTag clueGameTag = new XMLTag("ClueGame");
		
		XMLTag roomsTag = new XMLTag("Rooms");
		roomsTag.addAttribute("displayRoomPicturesOnBoard", Boolean.toString(gameData.getDisplayRoomPicturesOnBoard()));
		for (Room room : gameData.getRooms()) {
			XMLTag roomTag = new XMLTag("Room");
			roomTag.addAttribute("id", Integer.toString(room.getId()));
			roomTag.addAttribute("name", room.getName());
			String pictureName = room.getPictureName();
			if (pictureName != null && !pictureName.isEmpty())
				roomTag.addAttribute("pictureName", pictureName);
			if (room.getTransparentPictureColor() != null)
				roomTag.addAttribute("transparentPictureColor", Integer.toString(room.getTransparentPictureColor().getRGB()));
			roomsTag.addSubTag(roomTag);
		}
		clueGameTag.addSubTag(roomsTag);
		
		XMLTag suspectsTag = new XMLTag("Suspects");
		for (Suspect suspect : gameData.getSuspects()) {
			XMLTag suspectTag = new XMLTag("Suspect");
			suspectTag.addAttribute("id", Integer.toString(suspect.getId()));
			suspectTag.addAttribute("name", suspect.getName());
			suspectTag.addAttribute("color", Integer.toString(suspect.getColor().getRGB()));
			String pictureName = suspect.getPictureName();
			if (pictureName != null && !pictureName.isEmpty())
				suspectTag.addAttribute("pictureName", pictureName);
			suspectsTag.addSubTag(suspectTag);
		}
		clueGameTag.addSubTag(suspectsTag);
		
		XMLTag weaponsTag = new XMLTag("Weapons");
		for (Weapon weapon : gameData.getWeapons()) {
			XMLTag weaponTag = new XMLTag("Weapon");
			weaponTag.addAttribute("id", Integer.toString(weapon.getId()));
			weaponTag.addAttribute("name", weapon.getName());
			String pictureName = weapon.getPictureName();
			if (pictureName != null && !pictureName.isEmpty())
				weaponTag.addAttribute("pictureName", pictureName);
			weaponsTag.addSubTag(weaponTag);
		}
		clueGameTag.addSubTag(weaponsTag);

		Board board = gameData.getBoard();
		XMLTag boardTag = new XMLTag("ClueBoard");
		boardTag.addAttribute("height", Integer.toString(board.getHeight()));
		boardTag.addAttribute("width", Integer.toString(board.getWidth()));
		if (gameData.getBackgroundImageFilename() != null)
			boardTag.addAttribute("backgroundImageFilename", gameData.getBackgroundImageFilename());
		boardTag.addAttribute("backgroundColor", Integer.toString(gameData.getBackgroundColor().getRGB()));
		for (int row = 0; row < board.getHeight(); row++) {
			for (int col = 0; col < board.getWidth(); col++) {
				DisplayTile tile = board.getTile(row, col);
				if (tile.isFreeTile())
					continue;
				
				XMLTag tileTag = new XMLTag("Tile");
				tileTag.addAttribute("row", Integer.toString(row));
				tileTag.addAttribute("col", Integer.toString(col));
				
				if (tile.isRoomTile())
					tileTag.addAttribute("roomId", Integer.toString(tile.getRoom().getId()));
				for (DisplayTile.Direction direction : DisplayTile.Direction.values()) {
					if (tile.hasDoor(direction)) {
						XMLTag doorTag = new XMLTag("Door");
						doorTag.addContent(direction.toString());
						tileTag.addSubTag(doorTag);
					}		
				}
				
				if (tile.isRemovedTile())
					tileTag.addAttribute("removed", "true");
				else if (tile.isPassage()) {
					XMLTag passageConnectionTag = new XMLTag("PassageConnection");
					Board.TilePosition tilePosition = board.getTilePosition(tile.getPassageConnection());
					passageConnectionTag.addAttribute("row", Integer.toString(tilePosition.row));
					passageConnectionTag.addAttribute("col", Integer.toString(tilePosition.col));
					tileTag.addSubTag(passageConnectionTag);
				}
				else if (tile.hasSuspect()) {
					tileTag.addAttribute("suspectId", Integer.toString(tile.getSuspect().getId()));
					tileTag.addAttribute("suspectDirection", tile.getSuspectDirection().toString());
				}
				
				boardTag.addSubTag(tileTag);
			}
		}
		clueGameTag.addSubTag(boardTag);
		
		XMLTag storyTag = new XMLTag("Story");
		storyTag.addContent(gameData.getStory());
		clueGameTag.addSubTag(storyTag);
		
		rootTag.addSubTag(clueGameTag);
		return rootTag;
	}
	
	/**
	 * Parses XML from a given file and generates CLUE game data.
	 * @param gameFile XML file to parse
	 * @return the generated CLUE game data
	 */
	public static ClueGameData readFromFile(File gameFile) {
		if (gameFile == null)
			return null;
		
		try {
			XMLTag rootTag = XMLReader.parseXMLFile(gameFile);
			return readFromXMLRootTag(rootTag);
		}
		catch (XMLException ex) {
			Messenger.error(ex, "Corrupted or invalid XML Custom Clue Game file: " + gameFile, "CCG File Read Error");
			return null;
		}
	}
	
	/**
	 * Parses XML from the given text and generates CLUE game data.
	 * @param ccgXML XML text to parse
	 * @return the generated CLUE game data
	 */
	public static ClueGameData readFromString(String ccgXML) {
		try {
			XMLTag rootTag = XMLReader.parseXML(ccgXML);
			return readFromXMLRootTag(rootTag);
		}
		catch (XMLException ex) {
			Messenger.error(ex, "Invalid XML: " + ccgXML, "CCG String Read Error");
			return null;
		}
	}
	
	/**
	 * Converts the given XMLTag to CLUE game data.
	 * @param rootTag root Tag of the XML to begin parsing with
	 * @return the generated CLUE game data
	 * @throws XMLException
	 */
	private static ClueGameData readFromXMLRootTag(XMLTag rootTag) throws XMLException {
		ClueGameData gameData = new ClueGameData();
		char passageLetter = 'A';
		
		XMLTag clueGameTag = rootTag.getSubTag("ClueGame");
		
		XMLTag roomsTag = clueGameTag.getSubTag("Rooms");
		gameData.setDisplayRoomPicturesOnBoard(Boolean.parseBoolean(roomsTag.getAttributeValue("displayRoomPicturesOnBoard")));
		List<Room> rooms = new ArrayList<Room>();
		for (XMLTag roomTag : roomsTag.getSubTags("Room")) {
			String roomName = roomTag.getAttributeValue("name");
			if (roomName == null)
				throw new XMLException("Tag does not have a name attribute: " + XMLWriter.writeXMLToString(roomTag));				
			Room room = new Room(parseAttributeAsInt(roomTag, "id"), roomName);
			
			String pictureName = roomTag.getAttributeValue("pictureName");
			if (pictureName != null)
				room.setPictureName(pictureName);
			
			if (roomTag.getAttributeValue("transparentPictureColor") != null)
				room.setTransparentPictureColor(new Color(parseAttributeAsInt(roomTag, "transparentPictureColor")));
			
			rooms.add(room);
		}
		gameData.setRooms(rooms);

		XMLTag suspectsTag = clueGameTag.getSubTag("Suspects");
		List<Suspect> suspects = new ArrayList<Suspect>();
		for (XMLTag suspectTag : suspectsTag.getSubTags("Suspect")) {
			String suspectName = suspectTag.getAttributeValue("name");
			if (suspectName == null)
				throw new XMLException("Tag does not have a name attribute: " + XMLWriter.writeXMLToString(suspectTag));
			Color color = new Color(parseAttributeAsInt(suspectTag, "color"));
			Suspect suspect = new Suspect(parseAttributeAsInt(suspectTag, "id"), suspectName, color);
			
			String pictureName = suspectTag.getAttributeValue("pictureName");
			if (pictureName != null)
				suspect.setPictureName(pictureName);
			
			suspects.add(suspect);
		}
		gameData.setSuspects(suspects);

		XMLTag weaponsTag = clueGameTag.getSubTag("Weapons");
		List<Weapon> weapons = new ArrayList<Weapon>();
		for (XMLTag weaponTag : weaponsTag.getSubTags("Weapon")) {
			String weaponName = weaponTag.getAttributeValue("name");
			if (weaponName == null)
				throw new XMLException("Tag does not have a name attribute: " + XMLWriter.writeXMLToString(weaponTag));				
			Weapon weapon = new Weapon(parseAttributeAsInt(weaponTag, "id"), weaponName);
			
			String pictureName = weaponTag.getAttributeValue("pictureName");
			if (pictureName != null)
				weapon.setPictureName(pictureName);
			
			weapons.add(weapon);
		}
		gameData.setWeapons(weapons);
		
		XMLTag boardTag = clueGameTag.getSubTag("ClueBoard");
		Board board = new Board(parseAttributeAsInt(boardTag, "height"), parseAttributeAsInt(boardTag, "width"));
		gameData.setBackgroundImageFilename(boardTag.getAttributeValue("backgroundImageFilename"));
		gameData.setBackgroundColor(new Color(parseAttributeAsInt(boardTag, "backgroundColor")));
		for (XMLTag tileTag : boardTag.getSubTags("Tile")) {
			DisplayTile tile = board.getTile(parseAttributeAsInt(tileTag, "row"), parseAttributeAsInt(tileTag, "col"));
			if (tileTag.containsAttribute("roomId")) {
				int roomId = parseAttributeAsInt(tileTag, "roomId");
				Room room = null;
				for (Room currentRoom : rooms) {
					if (currentRoom.getId() == roomId) {
						room = currentRoom;
						break;
					}
				}
				if (room == null)
					throw new XMLException("No matching room for Tile roomId: " + XMLWriter.writeXMLToString(tileTag));
				tile.setRoom(room);
			}
				
			for (XMLTag doorTag : tileTag.getSubTags("Door")) {
				try {
					tile.addDoor(DisplayTile.Direction.valueOf(doorTag.getContent()));
				}
				catch (IllegalArgumentException ex) {
					throw new XMLException("Door Tag does not contain a valid direction: " + XMLWriter.writeXMLToString(doorTag));
				}
			}
			
			if (tileTag.containsAttribute("removed") && tileTag.getAttributeValue("removed").equals("true"))
				tile.setRemoved();
			
			for (XMLTag passageConnectionTag : tileTag.getSubTags("PassageConnection")) {
				if (tile.isPassage())
					continue;
				
				DisplayTile connectedTile = board.getTile(parseAttributeAsInt(passageConnectionTag, "row"), parseAttributeAsInt(passageConnectionTag, "col"));
				tile.setPassageConnection(connectedTile, passageLetter);
				connectedTile.setPassageConnection(tile, passageLetter);
				passageLetter++;
			}
			
			if (tileTag.containsAttribute("suspectId")) {
				int suspectId = parseAttributeAsInt(tileTag, "suspectId");
				Suspect suspect = null;
				for (Suspect currentSuspect : suspects) {
					if (currentSuspect.getId() == suspectId) {
						suspect = currentSuspect;
						break;
					}
				}
				if (suspect == null)
					throw new XMLException("No matching suspect for Tile suspectId: " + XMLWriter.writeXMLToString(tileTag));
				
				DisplayTile.Direction direction = DisplayTile.Direction.valueOf(tileTag.getAttributeValue("suspectDirection"));
				tile.addSuspect(suspect, direction);
			}
		}
		gameData.setBoard(board);
		
		gameData.setStory(clueGameTag.getSubTag("Story").getContent());
		return gameData;
	}
		
	/**
	 * Parses an attribute as an integer and returns the parsed number.
	 * @param tag XML Tag to parse
	 * @param attributeName name of the attribute to parse
	 * @return the parsed attribute value an an integer
	 * @throws XMLException if the attribute value is not found or is non-numeric
	 */
	private static int parseAttributeAsInt(XMLTag tag, String attributeName) throws XMLException {
		try {
			String idStr = tag.getAttributeValue(attributeName);
			if (idStr == null)
				throw new XMLException("Tag does not have an attribute of \"" + attributeName + "\": " + XMLWriter.writeXMLToString(tag));
			return Integer.parseInt(idStr);
		}
		catch (NumberFormatException ex) {
			throw new XMLException("Attribute \"" + attributeName + "\" of Tag \"" + tag.getName() + "\" is not a valid number id: " + tag.getAttributeValue("id"));
		}
	}
}
