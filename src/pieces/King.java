package pieces;

import main.PieceType;
import main.Screen;

public class King extends Pieces {

	public King(int color, int col, int row) {
		super(color, col, row);
		type = PieceType.KING;
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
		
			
			//Castling Handling
			if(moved == false) {
				
				if(targetCol == preCol+2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false) {
					
					for(Pieces piece : Screen.simPieces) {
						if(piece.col == preCol + 3 && piece.row == preRow && piece.moved == false) {
							Screen.castlingPiece = piece;
							return true;
						}
					}
				}
			
				if(targetCol == preCol-2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false) {
					
					Pieces p[] = new Pieces[2];
					for(Pieces piece: Screen.simPieces) {
						if(piece.col == preCol-3 && piece.row == targetRow) {
							p[0] = piece;
						}
						if(piece.col == preCol-4 && piece.row == targetRow) {
							p[1] = piece;
						}
						if(p[0] == null && p[1] != null && p[1].moved == false) {
							Screen.castlingPiece = p[1];
							return true;
					}
				}
			}
		}
			
			
			
		}
		
		return false;
	}
}
