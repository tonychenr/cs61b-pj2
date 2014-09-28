/* MachinePlayer.java */

package player;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 **/
public class MachinePlayer extends Player {

  private final static int SEARCHDEPTH = 2;
  private final static boolean COMPUTER = true;
  private final static boolean HUMAN = false;
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
    BestMove best_move = bestMove(searchDepth, COMPUTER, GameBoard.MINBOARDSCORE, GameBoard.MAXBOARDSCORE);
    board.makeMove(best_move.move, color + 1);
    return best_move.move;
  } 

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.

  public boolean opponentMove(Move m) {
	  if (board.isValidMove(m, (1 - this.color) + 1)) {
	    board.makeMove(m, (1 - this.color) + 1);
	    return true;
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
   *  Implemented by vh
   *  minimaxbestMove() performs a minimax tree search to determine the best move to 
   *  make, given an array of valid moves from 
   *  listMoves(). 
   *  @param boolean side, which represents the machine player using the mini-max search
   *  @param int depth, searchDepth value for the player
   *  @param int player, integer representing the color of the Machine player
   *  @param int opponent, integer representing the color of the Machine player's opponent
   *  @return move is BestMove that will maximize the increase in board score.
  **/
  private BestMove minimaxBestMove(int depth, boolean side){
    BestMove my_bestMove = new BestMove();
    BestMove reply;
    if (depth == 0 || board.hasValidNetwork(color + 1) || board.hasValidNetwork(2 - color)) {
      my_bestMove.score = board.evalBoard(searchDepth - depth);
      return my_bestMove;
    }
    int player;
    if (side == COMPUTER) {
      my_bestMove.score = GameBoard.MINBOARDSCORE;
      player = this.color;
    } else {
      my_bestMove.score = GameBoard.MAXBOARDSCORE;
      player = 1 - this.color;
    }
    Move[] my_moves = board.listMoves(player + 1);
    my_bestMove.move = my_moves[0];
    for (int i = 0; i < my_moves.length; i++) {
      board.makeMove(my_moves[i], player + 1);
      reply = minimaxBestMove(depth - 1, !side);
      board.undoMove(my_moves[i]);
      if ((side == COMPUTER && reply.score > my_bestMove.score) || (side == HUMAN && reply.score < my_bestMove.score)) {
        my_bestMove.move = my_moves[i];
        my_bestMove.score = reply.score;
      }
    }
    return my_bestMove;
  }
  
  /**
   *  Implemented by vh
   *  bestMove() performs alpha-beta pruning for the minimax tree search.
   *  @param side is a boolean that indicates  whether the side playing is "this" 
   *  MachinePlayer
   *  @param depth is an Integer that determines the searchDepth of "this" MachinePlayer
   *  @param alpha is an Integer, which gives the minimum score our        
   *  machinePlayer would get from a specific move
   *  @param beta is an Integer, which is the maximum score of the opponent
   *  @return move is a BestMove that will maximize the increase in board score
  **/
  private BestMove bestMove(int depth, boolean side, int alpha, int beta){
  	BestMove my_bestMove = new BestMove();
    BestMove reply;
    if (depth == 0 || board.hasValidNetwork(color + 1) || board.hasValidNetwork(2 - color)) {
      my_bestMove.score = board.evalBoard(searchDepth - depth);
      return my_bestMove;
    }
    int player;
    if (side == COMPUTER) {
      my_bestMove.score = alpha;
      player = this.color;
    } else {
      my_bestMove.score = beta;
      player = 1 - this.color;
    }
    Move[] my_moves = board.listMoves(player + 1);
    my_bestMove.move = my_moves[0];
  	for (int i = 0; i < my_moves.length; i++) {
  		board.makeMove(my_moves[i], player + 1);
  		reply = bestMove(depth - 1, !side, alpha, beta);
      board.undoMove(my_moves[i]);
      if ((side == COMPUTER) && (reply.score > my_bestMove.score)) {
  			my_bestMove.move = my_moves[i];
        my_bestMove.score = reply.score;
        alpha = reply.score;
  	  } else if ((side == HUMAN) && (reply.score < my_bestMove.score)) {
        my_bestMove.move = my_moves[i];
        my_bestMove.score = reply.score;
        beta = reply.score;
      }
      if (alpha >= beta) {
        return my_bestMove;
      }
  	}
  	return my_bestMove;
  }

  public static void main(String[] args) {
    MachinePlayer thisone = new MachinePlayer(1);
    thisone.board.makeMove(new Move(1, 1), GameBoard.BLACK);
    thisone.board.makeMove(new Move(2, 1), GameBoard.BLACK);
    thisone.board.makeMove(new Move(4, 1), GameBoard.BLACK);
    thisone.board.makeMove(new Move(5, 1), GameBoard.BLACK);
    thisone.board.makeMove(new Move(1, 6), GameBoard.BLACK);
    thisone.board.makeMove(new Move(0, 2), GameBoard.WHITE);
    thisone.board.makeMove(new Move(1, 2), GameBoard.WHITE);
    thisone.board.makeMove(new Move(4, 2), GameBoard.WHITE);
    thisone.board.makeMove(new Move(0, 4), GameBoard.WHITE);
    thisone.board.makeMove(new Move(1, 5), GameBoard.WHITE);
    thisone.board.makeMove(new Move(4, 5), GameBoard.WHITE);
    thisone.chooseMove();
    System.out.println(thisone.board.hasValidNetwork(GameBoard.WHITE));

    thisone = new MachinePlayer(1, 3);
    thisone.board.makeMove(new Move(1, 0), GameBoard.BLACK);
    thisone.board.makeMove(new Move(1, 2), GameBoard.BLACK);
    thisone.board.makeMove(new Move(6, 2), GameBoard.BLACK);
    thisone.board.makeMove(new Move(6, 7), GameBoard.BLACK);
    thisone.board.makeMove(new Move(0, 2), GameBoard.WHITE);
    thisone.board.makeMove(new Move(1, 6), GameBoard.WHITE);
    thisone.board.makeMove(new Move(4, 3), GameBoard.WHITE);
    thisone.board.makeMove(new Move(4, 6), GameBoard.WHITE);
    thisone.chooseMove();
    System.out.println(thisone.board.hasValidNetwork(GameBoard.WHITE));
    thisone.chooseMove();
    System.out.println(thisone.board.hasValidNetwork(GameBoard.WHITE));

    thisone = new MachinePlayer(1, 3);
    thisone.board.makeMove(new Move(6, 3), GameBoard.BLACK);
    thisone.board.makeMove(new Move(3, 3), GameBoard.BLACK);
    thisone.board.makeMove(new Move(3, 4), GameBoard.BLACK);
    thisone.board.makeMove(new Move(5, 5), GameBoard.BLACK);
    thisone.board.makeMove(new Move(5, 6), GameBoard.BLACK);
    thisone.board.makeMove(new Move(0, 3), GameBoard.WHITE);
    thisone.board.makeMove(new Move(2, 3), GameBoard.WHITE);
    thisone.board.makeMove(new Move(2, 4), GameBoard.WHITE);
    thisone.board.makeMove(new Move(0, 5), GameBoard.WHITE);
    thisone.board.makeMove(new Move(4, 6), GameBoard.WHITE);
    thisone.chooseMove();
    System.out.println(thisone.board.hasValidNetwork(GameBoard.WHITE));
    thisone.chooseMove();
    System.out.println(thisone.board.hasValidNetwork(GameBoard.WHITE));

    thisone = new MachinePlayer(1);
    thisone.board.makeMove(new Move(1, 0), GameBoard.BLACK);
    thisone.board.makeMove(new Move(3, 4), GameBoard.BLACK);
    thisone.board.makeMove(new Move(6, 4), GameBoard.BLACK);
    thisone.board.makeMove(new Move(1, 6), GameBoard.BLACK);
    thisone.board.makeMove(new Move(6, 7), GameBoard.BLACK);
    thisone.board.makeMove(new Move(0, 1), GameBoard.WHITE);
    thisone.board.makeMove(new Move(6, 1), GameBoard.WHITE);
    thisone.board.makeMove(new Move(2, 2), GameBoard.WHITE);
    thisone.board.makeMove(new Move(2, 3), GameBoard.WHITE); 
    thisone.board.makeMove(new Move(7, 4), GameBoard.WHITE);
    thisone.chooseMove();
    System.out.println(thisone.board.hasValidNetwork(GameBoard.WHITE));
  }

}
