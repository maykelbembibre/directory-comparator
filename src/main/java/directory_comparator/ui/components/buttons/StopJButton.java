package directory_comparator.ui.components.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import directory_comparator.ui.AppWindow;
import directory_comparator.ui.workers.DirectoryComparisonTask;

/**
 * A stop button.
 */
public class StopJButton extends JButton {

	private static final long serialVersionUID = -7548150077454006931L;

	/**
	 * Creates a stop button.
	 * @param appWindow The root app window.
	 */
	public StopJButton(AppWindow appWindow) {
		super("Stop");
		this.setEnabled(false);
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// It's better that as soon as the stop button is clicked, it can't get clicked again.
				setEnabled(false);
				
				DirectoryComparisonTask task = appWindow.getTask();
				if (task != null) {
	                task.cancel(true);
				}
			}
		});
	}
}
