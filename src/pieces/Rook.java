package pieces;

import main.Screen;

public class Rook extends Pieces {

	public Rook(int color, int col, int row) {
		super(color, col, row);

		if (color == Screen.WHITE) {
			img = getImg("/pieces/rookw");
		} else {
			img = getImg("/pieces/rook");
		}
	}

	public boolean canMove(int targetCol, int targetRow) {
		
		if(isInBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			if(targetCol == preCol || targetRow == preRow) {
				if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		
		return false;
	}
}
