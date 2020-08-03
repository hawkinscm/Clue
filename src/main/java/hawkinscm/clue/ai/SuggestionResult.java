package hawkinscm.clue.ai;

import hawkinscm.clue.model.Card;
import hawkinscm.clue.model.Player;

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
