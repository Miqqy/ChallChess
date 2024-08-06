package pieces;

import main.PieceType;
import main.Screen;

public class Knight extends Pieces {

	public Knight(int color, int col, int row) {
		super(color, col, row);
		type = PieceType.KNIGHT;
		if (color == Screen.WHITE) {
			img = getImg("/pieces/Knightw");
		} else {
			img = getImg("/pieces/Knight");
		}
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		
		if(isInBoard(targetCol, targetRow)) {
			if(Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2) {
				if(isValidSquare(targetCol, targetRow)) {
					return true;
				}
			}
		}
		
		return false;
	}

}
