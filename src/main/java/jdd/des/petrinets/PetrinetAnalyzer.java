
package jdd.des.petrinets;

import java.util.TreeSet;

// import jdd.util.math.*;

/**
 * operations that involve analyzing a Petri Net are gathered here
 *
 *
 */



public class PetrinetAnalyzer {

	/** returns the transitions in conflict with t */
	public static TreeSet conflict(Petrinet pn, Transition t) {
		TreeSet ts = new TreeSet(TransitionComparator.comparator);

		PlaceEnumeration pe = pn.createPlaceEnumeration();
		TransitionEnumeration te = pn.createTransitionEnumeration();

		pn.incomingPlaces(pe,t);
		Place pin;
		while( (pin = pe.nextPlace()) != null) {
			pn.outgoingTransitions(te,pin);
			Transition tout;
			while( (tout = te.nextTransition()) != null) {
				if(tout != t) ts.add(tout);
			}
		}

		return ts;
	}
}

