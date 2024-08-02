package pieces;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.ChessBoard;
import main.Screen;

public class Pieces {

	public BufferedImage img;
	public int x, y;
	public int col, row, preCol, preRow;
	public int color;
	public Pieces hittingP;
	public boolean moved;

	public Pieces(int color, int col, int row) {
		this.color = color;
		this.col = col;
		this.row = row;

		x = getX(col);
		y = getY(row);

		preCol = col;
		preRow = row;
	}

	public BufferedImage getImg(String imagePath) {
		BufferedImage img = null;

		try {
			img = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return img;
	}

	public int getX(int col) {
		return col * ChessBoard.SPACE_SIZE;
	}

	public int getY(int row) {
		return row * ChessBoard.SPACE_SIZE;
	}

	public int getCol(int x) {
		return (x + ChessBoard.HALF_SIZE) / ChessBoard.SPACE_SIZE;
	}

	public int getRow(int y) {
		return (y + ChessBoard.HALF_SIZE) / ChessBoard.SPACE_SIZE;
	}

	public int getIndex() {
		for (int i = 0; i < Screen.simPieces.size(); i++) {
			if (Screen.simPieces.get(i) == this) {
				return i;
			}
		}

		return 0;
	}

	public void updatePosition() {
		x = getX(col);
		y = getY(row);
		preCol = getCol(x);
		preRow = getRow(y);
		moved = true;
	}

	public void resetPosition() {
		col = preCol;
		row = preRow;
		x = getX(col);
		y = getY(row);
	}

	public boolean canMove(int targetCol, int targetRow) {
		return false;
	}

	public boolean isInBoard(int targetCol, int targetRow) {
		if (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
			return true;
		}

		return false;
	}

	public boolean isSameSquare(int targetCol, int targetRow) {
		if(targetCol == preCol && targetRow == preRow) {
			return true;
		}
		
		return false;
	}
	
	public Pieces getHittingP(int targetCol, int targetRow) {
		for (Pieces piece : Screen.simPieces) {
			if (piece.col == targetCol && piece.row == targetRow && piece != this) {
				return piece;
			}
		}
		return null;
	}

	public boolean isValidSquare(int targetCol, int targetRow) {
		hittingP = getHittingP(targetCol, targetRow);

		if (hittingP == null) {// square is vacant
			return true;
		} else {// square is occupied
			if (hittingP.color != this.color) {
				return true;
			} else {
				hittingP = null;
			}
		}
		return false;
	}
	
	public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
		
		//when moving left
		for(int c = preCol - 1; c > targetCol; c--) {
			for(Pieces piece : Screen.simPieces) {
				if(piece.col == c && piece.row == targetRow) {
					hittingP = piece;
					return true;
				}
			}
		}
			
		//when moving right
		for(int c = preCol + 1; c < targetCol; c++) {
			for(Pieces piece : Screen.simPieces) {
				if(piece.col == c && piece.row == targetRow) {
					hittingP = piece;
					return true;
				}
			}
		}	
			
		//when moving up
		for(int r = preCol - 1; r > targetRow; r--) {
			for(Pieces piece : Screen.simPieces) {
				if(piece.col == targetCol && piece.row == r) {
					hittingP = piece;
					return true;
				}
			}
		}
		
		//when moving down
		for(int r = preCol + 1; r < targetRow; r++) {
			for(Pieces piece : Screen.simPieces) {
				if(piece.col == targetCol && piece.row == r) {
					hittingP = piece;
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {
		
		if(targetRow < preRow ) {
			//Up left
			for(int c = preCol - 1; c > targetCol; c--) {
				int diff = Math.abs(c - preCol);
				
				for(Pieces piece : Screen.simPieces) {
					if(piece.col == c && piece.row == preRow - diff) {
						hittingP = piece;
						return true;
					}
				}
			}
			
			//Up right
			for(int c = preCol + 1; c < targetCol; c++) {
				int diff = Math.abs(c - preCol);
				
				for(Pieces piece : Screen.simPieces) {
					if(piece.col == c && piece.row == preRow - diff) {
						hittingP = piece;
						return true;
					}
				}
			}
		}
		
		if(targetRow > preRow) {
			//Down left
			for(int c = preCol - 1; c > targetCol; c--) {
				int diff = Math.abs(c - preCol);
				
				for(Pieces piece : Screen.simPieces) {
					if(piece.col == c && piece.row == preRow + diff) {
						hittingP = piece;
						return true;
					}
				}
			}
			
			//Down Right
			for(int c = preCol + 1; c < targetCol; c++) {
				int diff = Math.abs(c - preCol);
				
				for(Pieces piece : Screen.simPieces) {
					if(piece.col == c && piece.row == preRow + diff) {
						hittingP = piece;
						return true;
					}
				}
			}
		}
		
		return false;
		
	}

	public void draw(Graphics2D g2D) {
		g2D.drawImage(img, x, y, ChessBoard.SPACE_SIZE, ChessBoard.SPACE_SIZE, null);
	}
}
