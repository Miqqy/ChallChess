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
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	Pieces activePiece;

	// color
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	int currentColor = WHITE;

	// Booleans
	boolean canMove;
	boolean validSquare;

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
		pieces.add(new Rook(WHITE, 4, 4));
		pieces.add(new Knight(WHITE, 1, 7));
		pieces.add(new Knight(WHITE, 6, 7));
		pieces.add(new Bishop(WHITE, 2, 7));
		pieces.add(new Bishop(WHITE, 5, 7));
		pieces.add(new King(WHITE, 4, 7));
		pieces.add(new Queen(WHITE, 4, 4));

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
				} else {//move isn't valid so reset everything
					copyPieces(pieces, simPieces);
					activePiece.resetPosition();
					activePiece = null;
				}
			}
		}
	}

	public void simulate() {

		canMove = false;
		validSquare = false;
		
		//reset the piece list in every loop
		//this is for restoring the removed piece during sim
		copyPieces(pieces, simPieces);

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
			
			validSquare = true;
		}
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
				g2D.setColor(Color.white);
				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
				g2D.fillRect(activePiece.col * ChessBoard.SPACE_SIZE, activePiece.row * ChessBoard.SPACE_SIZE,
						ChessBoard.SPACE_SIZE, ChessBoard.SPACE_SIZE);
				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			}

			// draw active piece in the end so that it shows that its on the square that was
			// selected
			activePiece.draw(g2D);
		}
	}

}