/* GameBoard.java */

package player;
import list.*;

/**
 * A GameBoard class used by MachinePlayer to play the game, Network.
**/

public class GameBoard {

	private final static int DIMENSION = 8;
	protected final static int BLACK = 1;
	protected final static int WHITE = 2;
	protected final static int MAXBOARDSCORE = 110;
	protected final static int MINBOARDSCORE = -110;
	private int[][] board;
	private int[] myPlayer;
	private int[] opponent;

	/** Creates a GameBoard.
	 *  myPlayer[0] and opponent[0] is an integer representing the color of the player: 1 for black, 2 for white.
	 *  myPlayer[1] and opponent[1] is an integer representing the number of pieces placed by the player
	 *  @param playerColor is an integer that is either 1 for black or 2 for white.
	 *  White has first move.
	**/

	public GameBoard(int playerColor) {
		board = new int[DIMENSION][DIMENSION];
		myPlayer = new int[]{playerColor, 0};
		opponent = new int[]{3 - playerColor, 0};

	}

	/**
	 *  Implemented by zm
	 *  isValidMove() determines if the move is valid. A move is valid if it satisfies all 
	 *  of the following:
	 *  1)  The chip is not placed in any of the four corners. 
	 *  2)  The chip is not placed in a goal of the opposite color.
	 *  3)  The chip is not placed in a square that is already occupied.
	 *  4)  The chip is not placed in a connected group (cluster) of over 2 chips, whether 
	 *  connected orthogonally or diagonally.
	 *  5)  If there are greater than 10 chips place by a player, then only placed chips can be 
	 *  moved. (The move is a step move)
	 *  6) If the move is a step move, the chip cannot be moved to the same location that it is 
	 *  already in.
	 *  7) The move is an add move if the player has less than 10 chips placed.
	 *  @param move is Move m.
	 *  @param player is an integer representing the player making this move.
	 *  @return true if the move is valid; else, return false.
	**/
	protected boolean isValidMove(Move move, int player) {
		if (move.moveKind == Move.ADD) {
			if ((player == myPlayer[0] && myPlayer[1] == 10) || (player == opponent[0] && opponent[1] == 10)) {
				return false;
			}
			if (!inBound(move.x1, move.y1)) {
				return false;
			}
		} else {
			if ((player == myPlayer[0] && myPlayer[1] < 10) || (player == opponent[0] && opponent[1] < 10)) {
				return false;
			}
			if (!inBound(move.x1, move.y1) || !inBound(move.x2, move.y2)) {
				return false;
			}
			if (board[move.x2][move.y2] != player) {
				return false;
			}
			if ((move.x1 == move.x2) && (move.y1 == move.y2)) {
				return false;
			}
		}
		if (!checkMoveRequirements(move, player)) {
			return false;
		}
		return true;
	}

	/**
	 *  Implemented by zm
	 *  inBound() is a helper method that checks if the coordinates are in bounds for a move.
	 *  @param x and y are integers that represent the coordinates to check.
	 *  @return true if the coordinates are within the board DIMENSION. false else.
	**/
	private boolean inBound(int x, int y) {
		return x >= 0 && x < DIMENSION && y >= 0 && y < DIMENSION;
	}

	/**
	 *  Implemented by zm
	 *  checkMoveRequirements() is a helper method used by isValidMove().
	 *  It check the following requirements:
	 *  1)  The chip is not placed in any of the four corners. 
	 *  2)  The chip is not placed in a goal of the opposite color.
	 *  3)  The chip is not placed in a square that is already occupied.
	 *  4)  The chip is not placed in a connected group (cluster) of 2 chips, whether 
	 *  connected orthogonally or diagonally.
	 *  @param x and y are integers representing the coordinates for which we want to make a
	    move
	 *  @param player is an integer representing the color of the player making the move.
	 *  @return true if the move passes the above requirements. false else.
	**/
	private boolean checkMoveRequirements(Move m, int player) {
		if ((m.x1 == 0 && (m.y1 == 0 || m.y1 == DIMENSION - 1)) ||
				(m.x1 == DIMENSION - 1 && (m.y1 == 0 || m.y1 == DIMENSION - 1))) {
			return false;
		}
		if (player == BLACK && (m.x1 == DIMENSION - 1 || m.x1 == 0)) {
			return false;
		}
		if (player == WHITE && (m.y1 == 0 || m.y1 == DIMENSION - 1)) {
			return false;
		}
		if (board[m.x1][m.y1] != 0) {
			return false;
		}
		for (int i = m.x1 - 1; i <= m.x1 + 1; i++) {
			for (int j = m.y1 - 1; j <= m.y1 + 1; j++) {
				if (inBound(i, j) && (board[i][j] == player || ((i == m.x1) && (j == m.y1)))) {
					if (isClustered(i, j, player, m)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 *  Implemented by zm
	 *  isClustered() is a helper method used by checkMoveRequirements() that checks if the chip 
	 *  at a coordinate is clustered.
	 *  A chip is clustered if any of the chips directly adjacent this chip are not empty.
	 *  @param x and y are integers indicating the location of the chip to check.
	 *  @param player is the color of the chip to check.
	 *  @return true if the chip at the location is clustered. false else.
	**/
	private boolean isClustered(int x, int y, int player, Move m) {
		if (m.moveKind == Move.ADD) {
			int count = 0;
			for (int i = x - 1; i <= x + 1; i++) {
				for (int j = y - 1; j <= y + 1; j++) {
					if (inBound(i, j)) {
						if (board[i][j] == player) {
							count++;
						}
						if (count > 1) {
							return true;
						}
					}
				}
			}
		} else if (m.moveKind == Move.STEP) {
			Move temp_add = new Move(m.x1, m.y1);
			Move temp_remove = new Move(m.x2, m.y2);
			undoMove(temp_remove);
			boolean clustered = isClustered(x, y, player, temp_add);
			makeMove(temp_remove, player);
			return clustered;
		}
		return false;
	}

	/**
	 *  Implemented by zm
	 *  findConnections() finds all chips which are connections to a specified chip. 
	 *  A chip is a connection to another if it runs along a straight line, is orthogonal 
	 *  or diagonal to the indicated chip and if the chip is the first chip that is along 
	 *  the line originating from the specified chip. 
	 *  @param x is an Integer that is the x-coordinate of the chip
	 *  @param y is an Integer that is the y-coordinate of the chip
	 *  @return a DList, in which each node.item is a 1x2 array, where the first entry is the 
	 *  x-coordinate of the connected chip and the second entry is the y-coordinate of the 
	 *  connected chip.
	**/
	protected int[][] findConnections(int x, int y) {
		int[][] connections = new int[8][2];
		int index = 0;
		for (int i = -1; i <= 1; i++) {
		  for (int j = -1; j <= 1; j++) {
		    if (i != 0 || j != 0) {
		    	connections[index] = vectorSearch(x, y, i, j);
		    	index++;
		    }
		  }
		}
		return connections;
	}

	/**
	 *  Implemented by zm
	 *  vectorSearch() is a helper method for findConnections() to search for a connection in a 
	 *  given direction.
	 *  A chip is a connection to another if it runs along a straight line, is orthogonal 
	 *  or diagonal to the indicated chip and if the chip is the first chip that is along 
	 *  the line originating from the specified chip.
	 *  @param x and y are integers giving the starting position of the search.
	 *  @param x_vector and y_vector are integers ranging from -1 to 1, giving the direction to 
	 *  search.
	 *  @return a 1x2 integer array, where the first entry is the x-coordinate of the connection and 
	 *  the second entry is the y-coordinate of the connected chip.
	 *  @return null if there is no connection found in the direction specified by x_vector, y_vector
	**/
	private int[] vectorSearch(int x, int y, int x_vector, int y_vector) {
		for (int i = x + x_vector, j = y + y_vector; i >= 0 && i < DIMENSION && j >= 0 && j < DIMENSION; i += x_vector, j += y_vector) {
			if (board[i][j] > 0) {
				if (board[i][j] == board[x][y]) {
					return new int[] {i, j};
				}
				break;
			}
		}
		return null;
	}

	/**
	 *  Implemented by zm
	 *  evalBoard() uses this gameboard and returns an integer between -110 and 110 depending on 
	 *  the number of connections Machine player has as well as the number of connections the 
	 *  opponent has.
	 *  The score increases with the number of chips the Machine player has in a goal as well as 
	 *  the number of connections the Machine player's chips have.
	 *  The score decreases with the number of chips the opponent has in a goal as well as the 
	 *  number of connections the opponent player's chips have.
	 *  110 means the machine player hasValidNetwork.
	 *  -110 means the opponent hasValidNetwork.
	 *  110 is the best board score, and -110 is the worst board score.
	 *  evalBoard() gives a higher score for wins made in fewer moves.
	 *  @param moves_made is an integer that tells you how many moves you have made.
	 *  @return score is an Integer that rates how good the board is.
	**/
	protected int evalBoard(int moves_made) {
		int score = 0;
		if (hasValidNetwork(myPlayer[0])) {
			return MAXBOARDSCORE - moves_made;
		} else if (hasValidNetwork(opponent[0])) {
			return MINBOARDSCORE + moves_made;
		}
		for (int i = 0; i < DIMENSION; i++) {
			if (board[i][0] == BLACK || board[i][DIMENSION-1] == BLACK) {
				if (myPlayer[0] == BLACK) {
					score += 3;
				} else {
					score -= 3;
				}
			} 
		}
		for (int i = 0; i < DIMENSION; i++) {
			if (board[0][i] == WHITE || board[DIMENSION-1][i] == WHITE) {
				if (myPlayer[0] == WHITE) {
					score += 3;
				} else {
					score -= 3;
				}
			}
		}
		score += connectionCount(myPlayer[0]);
		score -= connectionCount(opponent[0]);
		return score;
	}

	/**
	 *  Implemented by zm
	 *  connectionCount() counts the number of connections a specified player has.
	 *  This is a helper method used by evalBoard().
	 *  @param player is an int specifying the player for whom to check connections.
	 *  @return an integer specifying the number of connections between chips of the player.
	**/
	private int connectionCount(int player) {
		int count = 0;
		int[][] temp_conn;
		for (int i = 0; i < DIMENSION; i++) {
			for (int j = 0; j < DIMENSION; j++) {
				if (board[i][j] == player) {
					temp_conn = findConnections(i, j);
					for (int k = 0; k < temp_conn.length; k++) {
						if (temp_conn[k] != null) {
							count++;
						}				
					}
				}
			}
		}
		return count;
	}

	/**
	 *  Implemented by zm
	 *  makeMove() takes input Move m and updates the game board if the move is 
	 *  legal
	 *  @param Move m is the move that the player wants to make on the board.
	**/
	protected void makeMove(Move m, int player) {
		if (isValidMove(m, player)) {
			board[m.x1][m.y1] = player;
			if (m.moveKind == Move.STEP) {
				board[m.x2][m.y2] = 0;
			} else {
				if (myPlayer[0] == player) {
					myPlayer[1]++;
				} else {
					opponent[1]++;
				}
			}
		}
	}

	/**
	 *  Implemented by vh
	 *  undoMove() takes input Move m and updates the game board back to its previous state 
	 *  before the move was made
	 *  @param Move m is the move that has to be undone.
	 **/
	protected void undoMove(Move m) {
		if (m.moveKind == Move.ADD) {
			if (board[m.x1][m.y1] == myPlayer[0]) {
				myPlayer[1]--;
			} else {
				opponent[1]--;
			}
			board[m.x1][m.y1] = 0;
		} else if (m. moveKind == Move.STEP) {
			board[m.x2][m.y2] = board[m.x1][m.y1];
			board[m.x1][m.y1] = 0;
		}
	}

	/**
	 *  Implemented by vh
	 *  listMoves() lists all valid moves that the player can make.
	 *  Each element of the array contains 1 move.
	 *  Only returns add moves when less than 10 pieces have been placed by player.
	 *  Only returns step moves when 10 pieces have been placed by player.
	 *  @param int player, is an integer representing the player whose possible moves we are  considering.
	 *  @return array of type Move, which contains all valid moves.
	**/
	protected Move[] listMoves(int player){
    DList my_list = new DList();
   	Move quitMove = new Move();
   	my_list.insertBack(quitMove);
    if ((player == myPlayer[0] && myPlayer[1] < 10) || (player == opponent[0] && opponent[1] < 10)) {
    	for (int i = 0; i < DIMENSION; i++){
    		for (int j = 0; j < DIMENSION; j++){
    			Move addMove = new Move(i, j);
    			if (isValidMove(addMove, player)){
    				my_list.insertBack(addMove);
    			}
    		}
    	}
    } else {
    	for (int i = 0; i < DIMENSION; i++){
    		for (int j = 0; j < DIMENSION; j++){
    			for (int i2 = 0; i2 < DIMENSION; i2++){
    				for (int j2 = 0; j2 < DIMENSION; j2++){
    					Move stepMove = new Move(i,j,i2,j2);
    					if (isValidMove(stepMove, player)){
    						my_list.insertBack(stepMove);
    					}
    				}
    			}
    		}
    	}
    }
    Move[] validMoves;
   	validMoves = my_list.toMoveArray();
   	return validMoves;
  }

	/**
	 *  Implemented by zm
	 *  hasValidNetwork() determines whether this.gameboard has a valid network
	 *  for player "side".  (Does not check whether the opponent has a network.)
	 *  A network is valid if it satisfies the following:
	 *	1) The network does not pass through the same chip twice.
	 *  2) The network has 1 chip in each goal.
	 *  3) A network has 6 or more connected chips. Connected chips are described
	 *	in the findConnections() method description.
	 *  4) Black's goal areas are squares 10, 20, 30, 40, 50, 60 and 17, 27, 37, 47, 57, 67.
	 *	5) White's goal areas are squares 01, 02, 03, 04, 05, 06 and 71, 72, 73, 74, 75, 76.
	 *  Unusual conditions:
	 *    If side is neither MachinePlayer.COMPUTER nor MachinePlayer.HUMAN,
	 *          returns false.
	 *    If this.gameboard squares contain illegal values, the behavior of this
	 *          method is undefined (i.e., don't expect any reasonable behavior).
	 *
	 *  @param player is integer: MachinePlayer.COMPUTER or MachinePlayer.HUMAN
	 *  @return true if player has a winning network in this.gameboard; false otherwise.
	**/
 	protected boolean hasValidNetwork(int player) {
 		boolean[][] checked_chips = new boolean[DIMENSION][DIMENSION];
 		DList startGoal = getgoalPieces(player, 0);
 		DList endGoal = getgoalPieces(player, DIMENSION-1);
 		int direction = -1;
 		DListNode node = startGoal.front();
 		while (node != null) {
 			int x_coord = ((int[]) node.item)[0];
 			int y_coord = ((int[]) node.item)[1];
 			checked_chips[x_coord][y_coord] = true;
 			DList network = new DList();
 			network.insertFront(node.item);
 			if (getNetwork(x_coord, y_coord, player, checked_chips,
 					-1, startGoal, endGoal, network)) {
 				return true;
 			}
 			network.remove(network.front());
 			node = startGoal.next(node);
 		}
 		return false;
 	}

	/**
	 *  Implemented by zm
	 *  getGoalPieces() returns a list of pieces of in the goal of a specified player.
	 *  @param player is an integer specifying the color of the player.
	 *  @param goal is an integer specifying which goal to get pieces: 0 for the starting goal. 7 
	 *  (DIMENSION - 1) for the ending goal.
	 *  @return a DList where each node.item is a 1x2 array. The first entry is the x-coordinate of 
	 *  the piece in the goal. The second entry is the y-coordinate.
	**/
 	private DList getgoalPieces(int player, int goal) {
 		DList goalPieces = new DList();
	 	for (int i = 1; i < DIMENSION-1; i++){
	 		if (player == BLACK) {
	  		if (board[i][goal] == player) {
	   			goalPieces.insertBack(new int[]{i, goal});
	   		}
	  	} else if (player == WHITE) {
	  		if (board[goal][i] == player) {
	  			goalPieces.insertBack(new int[]{goal, i});
	  		}
	  	}
	  }
	  return goalPieces;
 	}

	/**
	 *  Implemented by zm
	 *  getNetwork() gets the first valid network containing 6 or more pieces.
	 *  @param x_coord and y_coord are the coordinates of the chip for which to search for 
	 *  networks
	 *  @param player specifies the player for whom to search for networks.
	 *  @param checked_chips is an 8x8 array of booleans. Each cell is true if it has been check, 
	 *  and false by default.
	 *  @param int direction specifies the direction not to check, because a network must turn a 
	 *  corner at each node of the network.
	 *  @param startGoal and endGoal are DLists which are specify the chips in the starting goal 
	 *  and ending goal respectively. Each node.item is a 1x2 array. The first entry is the 
	 *  x-coordinate of the piece in the goal. The second entry is the y-coordinate.
	 *  @param network is a DList containing the constructed network so far. Each node.item is a 
	 *  1x2 array. The first entry is the x-coordinate of the piece in the goal. The second entry is 
	 *  the y-coordinate.
	 *  @return true if there is a valid network. else return false.
	**/
 	private boolean getNetwork(int x_coord, int y_coord, int player, boolean[][] checked_chips, int direction,
 													 		DList startGoal, DList endGoal, DList network) {
	 	if (inGoal(x_coord, y_coord, endGoal)) {
	 		if (network.length() >= 6) {
	 			return true;	 			
	 		}
	 		return false;
 		}
	 	checked_chips[x_coord][y_coord] = true;
 		int[][] connections = findConnections(x_coord, y_coord);
 		int count = 0;
 		for (int i = 0; i < connections.length; i++) {
 			if (connections[i] != null) {
 				int x_coord1 = connections[i][0];
 				int y_coord1 = connections[i][1];
 				if (inGoal(x_coord1, y_coord1, startGoal) || i == direction || checked_chips[x_coord1][y_coord1]) {
 					continue;
 				}
	 			network.insertBack(connections[i]);
	 	 		if (getNetwork(x_coord1, y_coord1, player, checked_chips, i, startGoal, endGoal, network)) {
	 	 			return true;
	 	 		}
	 	 		checked_chips[x_coord1][y_coord1] = false;
	 			network.remove(network.back());
 			}
 		}
 		return false;
 	}

	/**
	 *  Implemented by zm
	 *  inGoal() determines if a chip is in a goal area.
	 *  @param x and y are coordinates of the chip to check.
	 *  @param goalPieces is a DList of the chips in a specified goal.  Each node.item is a 1x2  
	 *  array. The first entry is the x-coordinate of the piece in the goal. The second entry is the 
	 *  y-coordinate.
	 *  @return true if the chip is in the goal. false otherwise.
	**/
 	private boolean inGoal(int x, int y, DList goalPieces) {
 		DListNode node = goalPieces.front();
 		while (node != null) {
 			if (x == ((int[]) node.item)[0] && y == ((int[]) node.item)[1]) {
				return true;
 			}
 			node = goalPieces.next(node);
 		}
 		return false;
 	}

 	public static void main (String[] args) {
 		GameBoard thisone = new GameBoard(BLACK);
 		thisone.makeMove(new Move(1, 1), BLACK);
 		thisone.makeMove(new Move(2, 1), BLACK);
 		thisone.makeMove(new Move(4, 1), BLACK);
 		thisone.makeMove(new Move(5, 1), BLACK);
 		thisone.makeMove(new Move(1, 6), BLACK);
 		thisone.makeMove(new Move(0, 2), WHITE);
 		thisone.makeMove(new Move(1, 2), WHITE);
 		thisone.makeMove(new Move(4, 2), WHITE);
 		thisone.makeMove(new Move(0, 4), WHITE);
 		thisone.makeMove(new Move(1, 5), WHITE);
 		thisone.makeMove(new Move(4, 5), WHITE);
 		thisone.makeMove(new Move(7, 5), WHITE);
 		boolean net = thisone.hasValidNetwork(WHITE);
 		System.out.println(net);
 		thisone = new GameBoard(BLACK);
 		thisone.makeMove(new Move(1, 1), BLACK);
 		thisone.makeMove(new Move(2, 1), BLACK);
 		thisone.makeMove(new Move(4, 1), BLACK);
 		thisone.makeMove(new Move(5, 1), BLACK);
 		thisone.makeMove(new Move(5, 5), BLACK);
 		thisone.makeMove(new Move(1, 6), BLACK);
 		thisone.makeMove(new Move(2, 6), BLACK);
 		thisone.makeMove(new Move(0, 2), WHITE);
 		thisone.makeMove(new Move(6, 1), WHITE);
 		thisone.makeMove(new Move(3, 2), WHITE);
 		thisone.makeMove(new Move(1, 3), WHITE);
 		thisone.makeMove(new Move(5, 3), WHITE);
 		thisone.makeMove(new Move(3, 5), WHITE);
 		thisone.makeMove(new Move(6, 5), WHITE);
 		thisone.makeMove(new Move(7, 2), WHITE);
 		DList goal = thisone.getgoalPieces(WHITE, 7);
 		// boolean z = thisone.inGoal(7, 2, goal);
 		// System.out.println(z);
 		System.out.println(thisone.hasValidNetwork(WHITE));
 		thisone = new GameBoard(BLACK);
 		thisone.makeMove(new Move(1, 0), BLACK);
 		thisone.makeMove(new Move(1, 2), BLACK);
 		thisone.makeMove(new Move(6, 2), BLACK);
 		thisone.makeMove(new Move(6, 7), BLACK);
 		thisone.makeMove(new Move(0, 2), WHITE);
 		thisone.makeMove(new Move(1, 3), WHITE);
 		thisone.makeMove(new Move(1, 6), WHITE);
 		thisone.makeMove(new Move(4, 3), WHITE);
 		thisone.makeMove(new Move(4, 6), WHITE);
 		thisone.makeMove(new Move(7, 3), WHITE);
 		System.out.println(thisone.hasValidNetwork(WHITE));

 	}


}