package pieces;

import main.PieceType;
import main.Screen;

public class Bishop extends Pieces {

	public Bishop(int color, int col, int row) {
		super(color, col, row);
		type = PieceType.BISHOP;
		if (color == Screen.WHITE) {
			img = getImg("/pieces/bishopw");
		} else {
			img = getImg("/pieces/bishop");
		}
	}

		
	public boolean canMove(int targetCol, int targetRow) {
			
		if(isInBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
				
				
			if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
				if(isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false) {
					return true;
				}
			}
				
		}
			
		return false;
	}
}
