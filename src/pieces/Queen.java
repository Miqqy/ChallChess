package pieces;

import main.Screen;

public class Queen extends Pieces {

	public Queen(int color, int col, int row) {
		super(color, col, row);

		if (color == Screen.WHITE) {
			img = getImg("/pieces/queenw");
		} else {
			img = getImg("/pieces/queen");
		}
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if(isInBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			
			//vert and horz
			if(targetCol == preCol || targetRow == preRow) {
				if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
					return true;
				}
			}
			
			
			//diagonal
			if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
				if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		
		return false;
	}
}
