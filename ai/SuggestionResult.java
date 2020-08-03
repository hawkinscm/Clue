package ai;

import model.Card;
import model.Player;

public class SuggestionResult {

	private Player suggester;
	private Player responder;
	private Card responderCard;
	
	public SuggestionResult(Player suggester, Player responder, Card responderCard) {
		this.suggester = suggester;
		this.responder = responder;
		this.responderCard = responderCard;
	}
	
	public Player getSuggester() {
		return suggester;
	}
	
	public Player getResponder() { 
		return responder;
	}
	
	public Card getResponderCard() {
		return responderCard;
	}
}
