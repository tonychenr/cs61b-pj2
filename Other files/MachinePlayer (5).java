/* MachinePlayer.java */

package player;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {

  private final static int SEARCHDEPTH = 2;
  private GameBoard board;
  @SuppressWarnings("unused")
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
    Move my_move = abPruner(true, 3, this.color, 1 - this.color, 0, 0);
    board.makeMove(my_move, this.color);
    return my_move;
  } 

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  @SuppressWarnings("static-access")
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
    if (board.isValidMove(m, color +1)) {
      board.makeMove(m, color + 1);
      return true;
    }
    return false;
  }
  

  
  
  /**
   *  bestMove() performs a minimax tree search to determine the best move,  
   *  given the an array of valid moves from listMoves(). 
   *  @param depth is the given search depth the machine player should follow through with.
   *  @param myPlayer is a boolean that indicates whether the side playing is "this" MachinePlayer
   *  @param player is an integer reperesenting the color of "this" Machineplayer
   *  @param opponent is an integer representing the color of the opponent
   *  @return type move, which is a Move that will maximize the increase in board score.
  **/

  @SuppressWarnings("unused")
private Move bestMove(boolean myPlayer, int depth, int player, int opponent){
	 Move [] my_moves = board.listMoves(player + 1);
	 Move my_bestMove = new Move();

	if (board.hasValidNetwork(player) || board.hasValidNetwork(opponent) || depth == 0){
		return my_bestMove;
	}
	 int i = 0;
	 while (i < my_moves.length){
		 int prevScore = board.evalBoard();
		 board.makeMove(my_moves[i], player);
		 Move op_bestMove = bestMove(!myPlayer, depth - 1, opponent, player);
		 board.makeMove(op_bestMove, opponent);
		 int currentScore = board.evalBoard();
		 board.undoMove(my_moves[i]);
		 board.undoMove(op_bestMove);
		 if ((currentScore > prevScore && myPlayer) || (currentScore < prevScore && !myPlayer)){
			 my_bestMove = my_moves[i];
		 }
		 i++;	 
		 }
	return my_bestMove;
  }
  
  /**
   *  abPruner() performs alpha-beta pruning for the minimax tree search.
   *  @param myPlayer is a boolean that indicates  whether the side playing is "this" MachinePlayer
   *  @param depth is an Integer that determines the searchDepth of "this" MachinePlayer
   *  @param alpha is an Integer, which gives the minimum score our        
   *  machinePlayer would get from a specific move
   *  @param beta is an Integer, which is the maximum score of the opponent
   *  @param player is an int, which represents the color of "this" MachinePlayer
   *  @param opponent is an int, which represents the color of the opponent of "this" MachinePlayer
   *  @return move is a Move that will maximize the increase in board score
  **/

private Move abPruner(boolean myPlayer, int depth, int player, int opponent, int alpha, int beta){
	 Move [] my_moves = board.listMoves(player + 1);  
	 Move my_bestMove = new Move();
	 
	 if (board.hasValidNetwork(player) || board.hasValidNetwork(opponent) || depth == 0){
		 return my_bestMove;
	 }
	 int i = 0;
	 while (i < my_moves.length){
		 int prevScore = board.evalBoard();
		 board.makeMove(my_moves[i], player);
		 Move op_bestMove = abPruner(!myPlayer, depth - 1, opponent, player, alpha, beta);
		 board.makeMove(op_bestMove, opponent);
		 int currentScore = board.evalBoard();
		 board.undoMove(my_moves[i]);
		 board.undoMove(op_bestMove);
		 if (myPlayer &&  (currentScore > prevScore)){
			 my_bestMove = my_moves[i];
			 alpha = currentScore;
		 } else if (!myPlayer && (currentScore < prevScore)){
			 my_bestMove = my_moves[i];
			 beta = currentScore;
		 }
		 if (alpha >= beta){
			 return my_bestMove;
		 }
	 }
     return my_bestMove;
 }
}

