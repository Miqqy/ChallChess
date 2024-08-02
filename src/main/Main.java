package main;

import javax.swing.JFrame;

public class Main {

	public static void main(String args[]) {

		JFrame window = new JFrame("ChallChess");

		// close program so it doesn't keep running after closing window
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/*
		 * 
		 * can't change window size to prevent sizing issues and set window to be in the
		 * middle of the screen
		 */
		window.setResizable(false);
	
		Screen screen = new Screen();
		window.add(screen);
		window.pack();
		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		screen.launch();
	}
}