
package hawkinscm.clue.model;

import java.util.Random;

/**
 * Class that handles randomization.
 */
public class Randomizer {

	// generator of random numbers
	private static Random generator = new Random();
	
	/**
	 * Returns a random number between 0 and range exclusive.
	 * @param range the range of numbers that the random number can be; the upper bound
	 * @return a randomly generated number between 0 and range exclusive
	 */
	public static int getRandom(int range) {
		return generator.nextInt(range);
	}
}
