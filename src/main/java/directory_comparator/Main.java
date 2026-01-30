package directory_comparator;

import directory_comparator.ui.AppWindow;

/**
 * The entry point to the program.
 */
public class Main {
	
	/**
	 * The main method.
	 * @param args Arguments. Not used.
	 */
	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AppWindow();
            }
        });
    }
}
