/* MachinePlayer.java */

package player;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {

  public final static int SEARCHDEPTH = 2;
  public static final boolean COMPUTER = true;
  private GameBoard board;
  private int searchDepth;
  private int color;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    board = new GameBoard(color + 1);
    this.color = color;
    this.searchDepth = SEARCHDEPTH;
  }

  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
    board = new GameBoard(color + 1);
    this.color = color;
    this.searchDepth = searchDepth;
  }

  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
    Move best_move = bestMove(searchDepth, COMPUTER, board.MINBOARDSCORE, board.MAXBOARDSCORE, color, 1 - color).move;
    board.makeMove(best_move, color + 1);
    return best_move;
  } 

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.

public boolean opponentMove(Move m) {
	    if (color == 0) {
	      if (board.isValidMove(m, board.WHITE)) {
	        board.makeMove(m, board.WHITE);
	        return true;
	      }
	    } else {
	      if (board.isValidMove(m, board.BLACK)) {
	        board.makeMove(m, board.BLACK);
	        return true;
	      }
	    }
	    return false;
	  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
    if (board.isValidMove(m, color + 1)) {
      board.makeMove(m, color + 1);
      return true;
    }
    return false;
  }
  
  /**
   *  bestMove() performs a minimax tree search to determine the best move,  
   *  given the list of valid moves from listMoves(). 
   *  @param list is a List of valid moves.
   *  @return type move, which is a Move that will maximize the increase in board score.
  **/


  private BestMove bestMove(int depth, boolean myPlayer, int alpha, int beta, int player, int otherPlayer){
  	BestMove my_bestMove = new BestMove();
    BestMove reply;
    if (depth == 0 || board.hasValidNetwork(player + 1) || board.hasValidNetwork(otherPlayer + 1)) {
      my_bestMove.score = board.evalBoard();
      return my_bestMove;
    }
    if (myPlayer) {
      my_bestMove.score = alpha;
    } else {
      my_bestMove.score = beta;
    }
    Move[] my_moves = board.listMoves(player + 1);
  	int i = 0;
  	while (i < my_moves.length){
  		board.makeMove(my_moves[i], color);
  		reply = bestMove(depth - 1, !myPlayer, alpha, beta, otherPlayer, player);
      board.undoMove(my_moves[i]);
      if (myPlayer && reply.score > my_bestMove.score){
  			my_bestMove.move = my_moves[i];
        my_bestMove.score = reply.score;
        alpha = reply.score;
  	  } else if (!myPlayer && reply.score < my_bestMove.score) {
        my_bestMove.move = my_moves[i];
        my_bestMove.score = reply.score;
        beta = reply.score;
      }
  		i++;
      if (alpha >= beta) {
        return my_bestMove;
      }
  	}
  	return my_bestMove;
  }

}
