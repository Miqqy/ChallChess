package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class ChessBoard {

	final int COL = 8;
	final int ROW = 8;

	public static final int SPACE_SIZE = 100;
	public static final int HALF_SIZE = SPACE_SIZE / 2;

	public void draw(Graphics2D g2D) {

		int c = 0;

		for (int row = 0; row < ROW; row++) {

			for (int col = 0; col < COL; col++) {

				if (c == 0) {
					g2D.setColor(new Color(148, 114, 67));
					c = 1;
				} else {
					g2D.setColor(new Color(221, 195, 159));
					c = 0;
				}
				g2D.fillRect(col * SPACE_SIZE, row * SPACE_SIZE, SPACE_SIZE, SPACE_SIZE);
			}

			if (c == 0) {
				c = 1;
			} else {
				c = 0;
			}
		}
	}

}