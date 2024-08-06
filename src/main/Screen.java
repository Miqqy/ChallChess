	package main;

import javax.swing.JPanel;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Pieces;
import pieces.Queen;
import pieces.Rook;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

public class Screen extends JPanel implements Runnable {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 800;
	final int FPS = 144;
	Thread chessThread;
	ChessBoard board = new ChessBoard();
	Mouse mouse = new Mouse();

	// pieces
	public static ArrayList<Pieces> pieces = new ArrayList<>();
	public static ArrayList<Pieces> simPieces = new ArrayList<>();
	ArrayList<Pieces> promotionPieces = new ArrayList<>();
	Pieces activePiece, checkingPiece;
	public static Pieces castlingPiece;

	// color
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	int currentColor = WHITE;

	// Booleans
	boolean canMove;
	boolean validSquare;
	boolean promotion;
	boolean gameOver;
	boolean stalemate;

	public Screen() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.DARK_GRAY);
		addMouseMotionListener(mouse);
		addMouseListener(mouse);

		setPieces();
		copyPieces(pieces, simPieces);
	}

	public void launch() {
		chessThread = new Thread(this);
		chessThread.start();
	}

	public void setPieces() {
		pieces.add(new Pawn(WHITE, 0, 6));
		pieces.add(new Pawn(WHITE, 1, 6));
		pieces.add(new Pawn(WHITE, 2, 6));
		pieces.add(new Pawn(WHITE, 3, 6));
		pieces.add(new Pawn(WHITE, 4, 6));
		pieces.add(new Pawn(WHITE, 5, 6));
		pieces.add(new Pawn(WHITE, 6, 6));
		pieces.add(new Pawn(WHITE, 7, 6));
		pieces.add(new Rook(WHITE, 0, 7));
		pieces.add(new Rook(WHITE, 7, 7));
		pieces.add(new Knight(WHITE, 1, 7));
		pieces.add(new Knight(WHITE, 6, 7));
		pieces.add(new Bishop(WHITE, 2, 7));
		pieces.add(new Bishop(WHITE, 5, 7));
		pieces.add(new King(WHITE, 4, 7));
		pieces.add(new Queen(WHITE, 3, 7));

		pieces.add(new Pawn(BLACK, 0, 1));
		pieces.add(new Pawn(BLACK, 1, 1));
		pieces.add(new Pawn(BLACK, 2, 1));
		pieces.add(new Pawn(BLACK, 3, 1));
		pieces.add(new Pawn(BLACK, 4, 1));
		pieces.add(new Pawn(BLACK, 5, 1));
		pieces.add(new Pawn(BLACK, 6, 1));
		pieces.add(new Pawn(BLACK, 7, 1));
		pieces.add(new Rook(BLACK, 0, 0));
		pieces.add(new Rook(BLACK, 7, 0));
		pieces.add(new Knight(BLACK, 1, 0));
		pieces.add(new Knight(BLACK, 6, 0));
		pieces.add(new Bishop(BLACK, 2, 0));
		pieces.add(new Bishop(BLACK, 5, 0));
		pieces.add(new King(BLACK, 4, 0));
		pieces.add(new Queen(BLACK, 3, 0));
	
	}
	
	private boolean isStalemate() {
		int enemyCount = 0;
		int myCount = 0;
		
		for(Pieces piece: simPieces) {
			if(piece.color == currentColor) {
				
				myCount++;
			}
			
		}
		
		for(Pieces piece: simPieces) {
			if(piece.color != currentColor)
			{
				enemyCount++;
			}
		}
		
		System.out.println("me " + myCount);
		System.out.println(enemyCount);
		
		if(enemyCount == 1 && myCount ==1) {
			return true;
		}
		
		
		else if(enemyCount == 1) {
			if(kingCanMove(getKing(true)) == false) {
				return true;
			}
		}
		
		
		return false;
	}
	
	private boolean canPromote() {
		
		if(activePiece.type == PieceType.PAWN) {
			if(currentColor == WHITE && activePiece.row == 0 || currentColor == BLACK && activePiece.row == 7) {
				promotionPieces.clear();
				promotionPieces.add(new Rook(currentColor,9,2));
				promotionPieces.add(new Knight(currentColor,9,3));
				promotionPieces.add(new Bishop(currentColor,9,4));
				promotionPieces.add(new Queen(currentColor,9,5));
				return true;
			}
		}
		return false;
	}

	private void copyPieces(ArrayList<Pieces> source, ArrayList<Pieces> target) {
		target.clear();

		for (int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}

	/*
	 * 
	 * game loops is a sequence of processes that run continuously as the game runs,
	 * run nanotime to see
	 * 
	 * elapsed time and use update and repaint methods every 1/144 of a second.
	 */
	@Override
	public void run() {

		double interval = 1000000000 / FPS;
		double diff = 0;

		long latestVal = System.nanoTime();
		long currentVal;

		while (chessThread != null) {
			currentVal = System.nanoTime();

			diff += (currentVal - latestVal) / interval;
			latestVal = currentVal;

			if (diff >= 1) {
				updates();
				repaint();
				diff--;
			}
		}

	}

	public void updates() {
		
		if(promotion) {
			promoting();
		}
		else if(gameOver == false && stalemate == false){
			// left click press
			if (mouse.pressed) {
				if (activePiece == null) {

					// if activePiece is null, check if you can pick and use a piece
					for (Pieces piece : simPieces) {

						// if mouse is on an ally piece, pick it up as activePiece
						if (piece.color == currentColor && piece.col == mouse.x / ChessBoard.SPACE_SIZE
								&& piece.row == mouse.y / ChessBoard.SPACE_SIZE) {
							activePiece = piece;
						}
					}
				} else {
					// if user is holding a piece, show simulation of possible moves
					simulate();
				}
			}

			// left click release
			if (mouse.pressed == false) {
				
				if (activePiece != null) {
					
					if (validSquare) {//confirming move
						
						//update the piece list in the case that a piece has been captured and removed during sim
						copyPieces(simPieces, pieces);
						activePiece.updatePosition();
						if(castlingPiece != null) {
							castlingPiece.updatePosition();
						}
						if(isKingInCheck() && isCheckmate()) {
							gameOver = true;
						}
						else if(isStalemate() && isKingInCheck() == false) {
							stalemate = true;
						}
						else {
							if(canPromote()) {
								promotion = true;
							}
							else {
								changePlayer();
							}
						}
						
					} else {//move isn't valid so reset everything
						copyPieces(pieces, simPieces);
						activePiece.resetPosition();
						activePiece = null;
					}
				}
			}
		}
	}

	
	
	private boolean isKingInCheck() {
		Pieces king = getKing(true);
		if(activePiece.canMove(king.col, king.row)) {
			checkingPiece = activePiece;
			return true;
		}
		else {
			checkingPiece = null;
		}
		return false;
	}

	private Pieces getKing(boolean opponent) {
		Pieces king = null;
		for(Pieces piece: simPieces) {
			if(opponent) {
				if(piece.type == PieceType.KING && piece.color != currentColor) {
					king = piece;
				}
			}
			else {
				if(piece.type == PieceType.KING && piece.color == currentColor) {
					king = piece;
				}
			}
		}
		return king;
	}

	private void promoting() {
		if(mouse.pressed) {
			for(Pieces piece: promotionPieces) {
				if(piece.col == mouse.x/ChessBoard.SPACE_SIZE && piece.row == mouse.y/ChessBoard.SPACE_SIZE) {
					switch(piece.type) {
					case ROOK: simPieces.add(new Rook(currentColor, activePiece.col, activePiece.row)); break;
					case KNIGHT: simPieces.add(new Knight(currentColor, activePiece.col, activePiece.row)); break;
					case BISHOP: simPieces.add(new Bishop(currentColor, activePiece.col, activePiece.row)); break;
					case QUEEN: simPieces.add(new Queen(currentColor, activePiece.col, activePiece.row)); break;
					default: break;
					}
					simPieces.remove(activePiece.getIndex());
					copyPieces(simPieces, pieces);
					activePiece = null;
					promotion = false;
					changePlayer();
				}
			}
		}
		
	}

	private boolean isIllegal(Pieces king) {
		if(king.type == PieceType.KING) {
			for(Pieces piece: simPieces) {
				if(piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
					return true;
				}
			}
		}
		return false;
	}
	public void simulate() {

		canMove = false;
		validSquare = false;
		
		//reset the piece list in every loop
		//this is for restoring the removed piece during sim
		copyPieces(pieces, simPieces);
		
		if(castlingPiece != null) {
			castlingPiece.col = castlingPiece.preCol;
			castlingPiece.x = castlingPiece.getX(castlingPiece.col);
			castlingPiece = null;
		}

		// if piece is held, update its position to show user it holding it currently
		activePiece.x = mouse.x - ChessBoard.HALF_SIZE;
		activePiece.y = mouse.y - ChessBoard.HALF_SIZE;

		activePiece.col = activePiece.getCol(activePiece.x);
		activePiece.row = activePiece.getRow(activePiece.y);

		// check if the piece can move to the space wanted
		if (activePiece.canMove(activePiece.col, activePiece.row)) {

			canMove = true;
			
			if(activePiece.hittingP != null) {
				simPieces.remove(activePiece.hittingP.getIndex());
			}
			
			checkCastling();
			if(isIllegal(activePiece) == false && opponentCanCaptureKing() == false) {
				validSquare = true;
			}
		}
	}
	
	private void checkCastling() {
		if(castlingPiece != null) {
			if(castlingPiece.col == 0) {
				castlingPiece.col += 3;
			}
			else if(castlingPiece.col == 7) {
				castlingPiece.col -= 2;
			}
			castlingPiece.x = castlingPiece.getX(castlingPiece.col);
		}
		
	}
	private void changePlayer() {
		if(currentColor == WHITE) {
			currentColor = BLACK;
			for(Pieces piece: pieces) {
				if(piece.color == BLACK) {
					piece.twoStepped = false;
				}
			}
		}
		else {
			currentColor = WHITE;
			for(Pieces piece: pieces) {
				if(piece.color == WHITE) {
					piece.twoStepped = false;
				}
			}
		}
		activePiece = null;
	}
	
	private boolean opponentCanCaptureKing() {
			Pieces king = getKing(false);
			for(Pieces piece : simPieces) {
				if(piece.color != king.color && piece.canMove(king.col, king.row)) {
					return true;
				}
			}
			return false;
		
		}
	
	private boolean isCheckmate() {
		Pieces king = getKing(true);
		if(kingCanMove(king)) {
			return false;
		}
		else {
			int colDiff = Math.abs(checkingPiece.col - king.col);
			int rowDiff = Math.abs(checkingPiece.row - king.row);
			if(colDiff == 0) {
				if(checkingPiece.row < king.row) {
					for(int row = checkingPiece.row; row < king.row; row++) {
						for(Pieces piece: simPieces) {
							if(piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)) {
								return false;
							}
						}
					}
				}
				if(checkingPiece.row > king.row) {
					for(int row = checkingPiece.row; row > king.row; row--) {
						for(Pieces piece: simPieces) {
							if(piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)) {
								return false;
							}
						}
					}
				}
			}
			else if(rowDiff == 0) {
				if(checkingPiece.col < king.col) {
					for(int col = checkingPiece.col; col < king.col; col++) {
						for(Pieces piece: simPieces) {
							if(piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)) {
								return false;
							}
						}
					}
				}
				if(checkingPiece.col > king.col) {
					for(int col = checkingPiece.col; col > king.col; col--) {
						for(Pieces piece: simPieces) {
							if(piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)) {
								return false;
							}
						}
					}
				}
			}
			else if(colDiff == rowDiff) {
				if(checkingPiece.row < king.row) {
					if(checkingPiece.col < king.col) {
						for(int col = checkingPiece.col, row = checkingPiece.row; col < king.col; col++, row++) {
							for(Pieces piece: simPieces) {
								if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					if(checkingPiece.col > king.col) {
						for(int col = checkingPiece.col, row = checkingPiece.row; col > king.col; col--, row++) {
							for(Pieces piece: simPieces) {
								if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
				if(checkingPiece.row > king.row) {
					if(checkingPiece.col < king.col) {
						for(int col = checkingPiece.col, row = checkingPiece.row; col < king.col; col++, row--) {
							for(Pieces piece: simPieces) {
								if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					if(checkingPiece.col > king.col) {
						for(int col = checkingPiece.col, row = checkingPiece.row; col > king.col; col--, row--) {
							for(Pieces piece: simPieces) {
								if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
			}
			
		}
		
		
		return true;
	}
	private boolean kingCanMove(Pieces king) {
		if(isValidMove(king, -1, -1)) {return true;}
		if(isValidMove(king, 0, -1)) {return true;}
		if(isValidMove(king, 1, -1)) {return true;}
		if(isValidMove(king, -1, 0)) {return true;}
		if(isValidMove(king, 1, 0)) {return true;}
		if(isValidMove(king, -1, 1)) {return true;}
		if(isValidMove(king, 0, 1)) {return true;}
		if(isValidMove(king, 1, 1)) {return true;}
		return false;
		
	}
	
	private boolean isValidMove(Pieces king, int colPlus, int rowPlus) {
		boolean isValidMove = false;
		
		king.col += colPlus;
		king.row += rowPlus;
		
		if(king.canMove(king.col, king.row)) {
			if(king.hittingP != null) {
				simPieces.remove(king.hittingP.getIndex());
			}
			if(isIllegal(king) == false) {
				isValidMove = true;
			}
			
		}
		king.resetPosition();
		copyPieces(pieces, simPieces);
		return isValidMove;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2D = (Graphics2D) g;

		// board
		board.draw(g2D);

		// pieces
		for (Pieces p : simPieces) {
			p.draw(g2D);
		}

		if (activePiece != null) {
			
			if(canMove) {
				if(isIllegal(activePiece) || opponentCanCaptureKing()) {
					g2D.setColor(Color.gray);
					g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2D.fillRect(activePiece.col * ChessBoard.SPACE_SIZE, activePiece.row * ChessBoard.SPACE_SIZE,
							ChessBoard.SPACE_SIZE, ChessBoard.SPACE_SIZE);
					g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
				else {
					g2D.setColor(Color.white);
					g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2D.fillRect(activePiece.col * ChessBoard.SPACE_SIZE, activePiece.row * ChessBoard.SPACE_SIZE,
							ChessBoard.SPACE_SIZE, ChessBoard.SPACE_SIZE);
					g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
			
			}

			// draw active piece in the end so that it shows that its on the square that was
			// selected
			activePiece.draw(g2D);
		}
		// Displaying turns
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2D.setColor(Color.white);
		
		if(promotion) {
			g2D.drawString("Promote to: ", 840, 150);
			for(Pieces piece: promotionPieces) {
				g2D.drawImage(piece.img, piece.getX(piece.col), piece.getX(piece.row), ChessBoard.SPACE_SIZE, ChessBoard.SPACE_SIZE, null);
			}
			
		}
		else {
			if(currentColor == WHITE) {
				g2D.setFont(new Font("Arial", Font.PLAIN, 45));
				g2D.drawString("White's turn", 840, 550);
				
				if(checkingPiece != null && checkingPiece.color == BLACK) {
					g2D.setColor(Color.red);
					g2D.drawString("CHECK", 840, 650);
					
				}
			}
			else{
				g2D.setFont(new Font("Arial", Font.PLAIN, 45));
				g2D.drawString("Black's turn", 840, 250);
				if(checkingPiece != null && checkingPiece.color == WHITE) {
					
					g2D.setColor(Color.red);
					g2D.drawString("CHECK", 840, 100);
				}
				
			}
		}
		if(gameOver) {
			String s = "";
			if(currentColor == WHITE)
			{
				s = "White wins";
				
			}
			
			else {
				s = "Black wins";
			}
			g2D.setFont(new Font("Arial", Font.PLAIN, 90));
			g2D.setColor(Color.green);
			g2D.drawString(s, 200, 420);
		}
		if(stalemate) {
			g2D.setFont(new Font("Arial", Font.PLAIN, 90));
			g2D.setColor(Color.LIGHT_GRAY);
			g2D.drawString("Stalemate", 200, 420);
		}
	}

}