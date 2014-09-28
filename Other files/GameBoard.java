/* GameBoard.java */

package player;
import list.*;

/**
 * A GameBoard class used by MachinePlayer to play the game, Network.
**/

public class GameBoard {

	private final static int DIMENSION = 8;
	public final static int BLACK = 1;
	public final static int WHITE = 2;
	private final static int MAXBOARDSCORE = 11;
	private final static int MINBOARDSCORE = -11;
	private int[][] board;
	private int[] myPlayer;
	private int[] opponent;

	// Creates a GameBoard.
	// myPlayer[0] and opponent[0] is an integer representing the color of the player: 1 for black, 2 for white.
	// myPlayer[1] and opponent[1] is an integer representing the number of pieces placed by the player
	// @param playerColor is an integer that is either 1 for black or 2 for white.
	// White has first move.

	public GameBoard(int playerColor) {
		board = new int[DIMENSION][DIMENSION];
		if (playerColor == BLACK) {
			myPlayer = new int[]{playerColor, 0};
			opponent = new int[]{playerColor + 1, 0};
		} else {
			myPlayer = new int[]{playerColor, 0};
			opponent = new int[]{playerColor - 1, 0};
		}
	}

	protected boolean isValidMove(Move move, int player) {
		if (!checkMoveRequirements(move.x1, move.y1, player)) {
			return false;
		}
		if (move.moveKind == Move.ADD) {
			if ((player == myPlayer[0] && myPlayer[1] == 10) || (player == opponent[0] && opponent[1] == 10)) {
				return false;
			} else if (!inBound(move.x1, move.y1)) {
				return false;
			}
			return true;
		} else {
			if ((player == myPlayer[0] && myPlayer[1] < 10) || (player == opponent[0] && opponent[1] < 10)) {
				return false;
			} else if (!inBound(move.x1, move.y1) && !inBound(move.x2, move.y2)) {
				return false;
			} else if (move.x1 == move.x2 && move.y1 == move.y2) {
				return false;
			}
			return true;
		}
	}

	private boolean inBound(int x, int y) {
		return x >= 0 && x < DIMENSION && y >= 0 && y < DIMENSION;
	}

	private boolean checkMoveRequirements(int x, int y, int player) {
		if ((x == 0 && (y == 0 || y == DIMENSION - 1)) ||
				(x == DIMENSION - 1 && (y == 0 || y == DIMENSION - 1))) {
			return false;
		} else if (player == 1 && (x == DIMENSION - 1 || x == 0)) {
			return false;
		} else if (player == 2 && (y == 0 || y == DIMENSION - 1)) {
			return false;
		} else if (!(board[x][y] == 0)) {
			return false;
		} else {
			for (int i = x - 1; i <= x + 1; i++) {
				for (int j = y - 1; j <= y + 1; j++) {
					if (i >= 0 && i < DIMENSION && j >= 0 && j < DIMENSION) {
						if (isClustered(i, j, player)) {
							return false;
						}
					}
				}
			}
			return true;
		}
	}

	private boolean isClustered(int x, int y, int player) {
		int count = 0;
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (i >= 0 && i < DIMENSION && j >= 0 && j < DIMENSION) {
					if (count > 1) {
						return true;
					}
					if (board[i][j] == player) {
						count++;
					}
				}
			}
		}
		return false;
	}

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

	private int[] vectorSearch(int x, int y, int x_vector, int y_vector) {
		for (int i = x, j = y; i >= 0 && i < DIMENSION && j >= 0 && j < DIMENSION; i += x_vector, j += y_vector) {
			if (board[i][j] > 0) {
				if (board[i][j] == board[x][y]) {
					int[] coord = new int[] {i, j};
					return coord;
				}
				break;
			}
		}
		return null;
	}

	protected int evalBoard() {
		int score = 0;
		if (hasValidNetwork(myPlayer[0])) {
			return MAXBOARDSCORE;
		} else if (hasValidNetwork(opponent[0])) {
			return MINBOARDSCORE;
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
		return score / 10;
	}

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

	protected Move[] listMoves(int player){
    	DList my_list = new DList();
    	int x1 = 1;
    	int y1 = 0;
    	int x2 = 1;
    	int y2 = 0;
    	Move quitMove = new Move();
    	my_list.insertFront(quitMove);
    	for (int i = x1; i<7; i++){
    		for (int j = y1; j<7; j++){
    			Move addMove = new Move(i, j);
    			if (isValidMove(addMove, player)){
    				my_list.insertFront(addMove);
    			}
    		}
    	}
    	for (int i = x1; i<7; i++){
    		for (int j = y1; j< 7; j++){
    			for (int i2 = x2; i2<7; i2++){
    				for (int j2 = y2; j2<7; j2++){
    					Move stepMove = new Move(i,j,i2,j2);
    					if (isValidMove(stepMove, player)){
    						my_list.insertFront(stepMove);
    					}
    				}
    			}
    		}
    	}
    	Move[] validMoves;
    	validMoves = my_list.toMoveArray();
    	return validMoves;
  }

 	@SuppressWarnings("unused")
	protected boolean hasValidNetwork(int player) {
 		boolean[][] checked_chips = new boolean[8][8];
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
 			if (getNetwork(((int[]) node.item)[0], ((int[]) node.item)[1], player, checked_chips,
 					-1, startGoal, endGoal, network).length() > 5) {
 				return true;
 			}
 			node = startGoal.next(node);
 		}
 		return false;
 	}

 	@SuppressWarnings("static-access")
	private DList getgoalPieces(int player, int goal) {
 		DList goalPieces = new DList();
  	for (int i = 1; i < DIMENSION-1; i++){
  		if (player == this.BLACK) {
  			if (board[i][goal] == player) {
    			goalPieces.insertBack(new int[]{i, goal});
   			}
  		} else if (player == this.WHITE) {
  			if (board[goal][i] == player) {
  				goalPieces.insertBack(new int[]{goal, i});
  			}
  		}
    }
    return goalPieces;
 	}

 	private DList getNetwork(int x_coord, int y_coord, int player, boolean[][] checked_chips, int direction,
 													 DList startGoal, DList endGoal, DList network) {
 		if (network.length() >= 6) {
 			if (inGoal(x_coord, y_coord, endGoal)) {
 				return network;
 			}
 		}
 		DList network2 = new DList();
 		int[][] connections = findConnections(x_coord, y_coord);
 		checked_chips[x_coord][y_coord] = true;
 		for (int i = 0; i < connections.length; i++) {
 			if (connections[i] != null) {
 				int x_coord1 = connections[i][0];
 				int y_coord1 = connections[i][1];
	 			if (!checked_chips[x_coord1][y_coord1] && i != direction && 
	 					!inGoal(x_coord1, y_coord1, endGoal) && !inGoal(x_coord1, y_coord1, startGoal)) {
	 				network.insertBack(connections[i]);
	 	 			network2 = getNetwork(connections[i][0], connections[i][1], player, checked_chips, i, 
	 	 														startGoal, endGoal, network);
	 			}
	 			if (network2.length() >= 6) {
	 				break;
	 			}
	 			network.remove(network.back());
 			}
 		}
 		return network2;
 	}

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
 	  protected void undoMove(Move m){
 		  if (m.moveKind == Move.STEP){
 			  board[m.x2][m.y2] = board[m.x1][m.y1];
 			  board[m.x1][m.y1] = 0;
 		  }
 		  else if (m.moveKind == Move.ADD){
 			  board[m.x1][m.x1] = 0;
 		  }
 	  }

}