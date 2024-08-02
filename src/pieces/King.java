package pieces;

import main.Screen;

public class King extends Pieces {

	public King(int color, int col, int row) {
		super(color, col, row);

		if (color == Screen.WHITE) {
			img = getImg("/pieces/kingw");
		} else {
			img = getImg("/pieces/king");
		}
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		
		if(isInBoard(targetCol, targetRow)) {
			
			if(Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 || Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1) {
				
				if(isValidSquare(targetCol, targetRow)) {
					return true;
				}
			}
		}
		
		return false;
	}
}
