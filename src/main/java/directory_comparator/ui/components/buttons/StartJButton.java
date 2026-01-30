package directory_comparator.ui.components.buttons;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import directory_comparator.ui.AppWindow;
import directory_comparator.ui.components.FileJTextField;
import directory_comparator.ui.listeners.DirectoryComparisonChangeListener;
import directory_comparator.ui.workers.DirectoryComparisonTask;

/**
 * The start button.
 */
public class StartJButton extends JButton {

	private static final long serialVersionUID = -6805354002802955071L;

	/**
	 * Creates a button that starts a task.
	 * @param appWindow The root app window.
	 * @param progressBar The progress bar for the task that is going to start.
	 * @param taskOutput The text area to output the progress of the task that is going to start.
	 * @param stopButton The stop button for the task that is going to start.
	 */
	public StartJButton(AppWindow appWindow, JProgressBar progressBar, JTextArea taskOutput, JButton stopButton) {
		super("Start");
		FileJTextField sourceFileTextField = appWindow.getSourceFileTextField();
		FileJTextField destinationFileTextField = appWindow.getDestinationFileTextField();
		FileJTextField resultsDirectoryFileTextField = appWindow.getResultDirectoryTextField();
		Collection<Component> sensitiveComponents = Arrays.asList(
			this, sourceFileTextField, destinationFileTextField, resultsDirectoryFileTextField,
			appWindow.getSourceFileChooseButton(), appWindow.getDestinationFileChooseButton(),
			appWindow.getResultsDirectoryChooseButton()
		);
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File sourceDirectory = sourceFileTextField.getSelectedFile();
				File destinationDirectory = destinationFileTextField.getSelectedFile();
				File resultsDirectory = resultsDirectoryFileTextField.getSelectedFile();
				
				progressBar.setValue(0);
		    	taskOutput.setText("Calculating data...");
		    	for (Component component : sensitiveComponents) {
		    		component.setEnabled(false);
		    	}
		        
		        //Instances of javax.swing.SwingWorker are not reusuable, so
		        //we create new instances as needed.
		    	DirectoryComparisonTask task = new DirectoryComparisonTask(
		        	sourceDirectory, destinationDirectory, resultsDirectory, taskOutput, sensitiveComponents, stopButton
		        );
		        PropertyChangeListener propertyChangeListener = new DirectoryComparisonChangeListener(
	            	progressBar, taskOutput, task
	            );
		        task.addPropertyChangeListener(propertyChangeListener);
		        appWindow.setTask(task);
		        task.execute();
		        
		        // After a task starts, the user has the possibility of stopping it.
		        stopButton.setEnabled(true);
			}
		});
	}
}
