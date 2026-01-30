package directory_comparator.ui.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import directory_comparator.ui.workers.DirectoryComparisonTask;

/**
 * An object of this class must listen for changes in a
 * {@link DirectoryComparisonTask}.
 */
public class DirectoryComparisonChangeListener implements PropertyChangeListener {

	private final JProgressBar progressBar;
	private final JTextArea statusNote;
	private final DirectoryComparisonTask task;
	
	/**
	 * Constructor.
	 * @param progressBar The progress bar.
	 * @param statusNote The status note.
	 * @param task The asynchronous task.
	 */
	public DirectoryComparisonChangeListener(
		JProgressBar progressBar, JTextArea statusNote, DirectoryComparisonTask task
	) {
		this.progressBar = progressBar;
		this.statusNote = statusNote;
		this.task = task;
	}
	
	public static String createStatusNoteText(int processedFiles, int totalFiles) {
		String result;
		if (totalFiles > 0) {
			result = "Processed " + processedFiles + "/" + totalFiles + " files.";
		} else {
			result = "";
		}
		return result;
	}

	/**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            statusNote.setText(createStatusNoteText(this.task.getProcessedFiles(), this.task.getTotalFiles()));
        } 
    }
}
