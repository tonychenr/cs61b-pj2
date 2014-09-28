/* BestMove.java  */

package player;

/**
 *  A subclass of Move. Stores a move and a score. Used by MachinePlayer in alpha beta pruning and minimax search.
 **/
public class BestMove extends Move {

	public int score;
	public Move move;

	public BestMove() {
		move = new Move();
		score = 0;
	}

}