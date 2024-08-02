package pieces;

import main.Screen;

public class Pawn extends Pieces {

	public Pawn(int color, int col, int row) {
		super(color, col, row);

		if (color == Screen.WHITE) {
			img = getImg("/pieces/ponw");
		} else {
			img = getImg("/pieces/pon");
		}
	}

	public boolean canMove(int targetCol, int targetRow) {
		if (isInBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {

			int moveValue;

			if (color == Screen.WHITE) {
				moveValue = -1;
			} else {
				moveValue = 1;
			}
		
			//check hittin piece
			hittingP = getHittingP(targetCol, targetRow);
			
			//1 square movement
			if(targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
				return true;
			}
			
			//2 squares
			if(targetCol == preCol && targetRow == preRow + moveValue * 2 && hittingP == null && moved == false && pieceIsOnStraightLine(targetCol, targetRow) == false) {
				return true;
			}
			
			//kill move
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null && hittingP.color != color) {
				return true;
			}
		}
		return false;
	}

}
