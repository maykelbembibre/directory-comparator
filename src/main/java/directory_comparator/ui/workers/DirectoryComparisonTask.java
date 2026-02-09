package directory_comparator.ui.workers;

import java.awt.Component;
import java.awt.Toolkit;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import directory_comparator.exceptions.FileException;
import directory_comparator.logic.FileManager;
import directory_comparator.logic.enums.ShortcutReason;
import directory_comparator.ui.listeners.DirectoryComparisonChangeListener;
import directory_comparator.ui.workers.models.ComparisonResults;

/**
 * An asynchronous task that compares all the contents of two directories.
 */
public class DirectoryComparisonTask extends SwingWorker<Void, Void> {

	private final File oldDirectory;
	private final File newDirectory;
	private final File resultsDirectory;
	private volatile JTextArea statusNote;
	private final Collection<Component> sensitiveComponents;
	private final Component stopButton;
	private String error;
	private volatile int newFilesCompared;
	private volatile int totalNewFiles;
	private volatile int oldFilesChecked;
	private volatile int totalOldFiles;
	private ComparisonResults comparisonResults;
	private Set<String> oldFilesThatNotExistInNewDirectory;
	private FileManager fileManager;
	
	/**
	 * Constructor.
	 * @param oldDirectory The old directory.
	 * @param newDirectory The new directory, which will be compared
	 * to the old one to find out if there is something changed or new.
	 * @param resultsDirectory Where to store the results.
	 * @param statusNote The status note.
	 * @param sensitiveComponents The components that have to be enabled
	 * only when the task ends.
	 * @param stopButton The button that makes this task stop.
	 * @throws FileException 
	 */
    public DirectoryComparisonTask(
    	File oldDirectory, File newDirectory, File resultsDirectory,
    	JTextArea statusNote, Collection<Component> sensitiveComponents,
    	Component stopButton
    ) {
    	this.oldDirectory = oldDirectory;
    	this.newDirectory = newDirectory;
    	this.resultsDirectory = resultsDirectory;
		this.statusNote = statusNote;
		this.sensitiveComponents = sensitiveComponents;
		this.stopButton = stopButton;
	}
    
    public int getProcessedFiles() {
    	return this.newFilesCompared + this.oldFilesChecked;
    }
    
    public int getTotalFiles() {
    	return this.totalNewFiles + this.totalOldFiles;
    }

    /**
     * Main task. Executed in background thread. Note that this method is
     * executed only once.
     * @return <code>null</code>.
     */
    @Override
    public Void doInBackground() {
		this.setProgress(0);
		this.newFilesCompared = 0;
		this.oldFilesChecked = 0;
		try {
			this.fileManager = new FileManager(this.oldDirectory, this.newDirectory, this.resultsDirectory);
	    	this.totalNewFiles = FileManager.countFiles(this.newDirectory);
			this.totalOldFiles = FileManager.countFiles(this.oldDirectory);
			this.statusNote.setText(DirectoryComparisonChangeListener.createStatusNoteText(
				this.getProcessedFiles(), this.getTotalFiles()
			));
			this.comparisonResults = new ComparisonResults(); 
			this.oldFilesThatNotExistInNewDirectory = new HashSet<>();
			this.compareRecursively(this.newDirectory);
			this.getOldFilesThatNotExistInNewDirectoryRecursively(this.oldDirectory);
			this.statusNote.setText("Creating results in results folder...");
			for (String path : this.comparisonResults.getNewFilePaths()) {
				this.fileManager.createShortcut(path, ShortcutReason.FILE_CREATED);
			}
			for (String path : this.comparisonResults.getChangedFilePaths()) {
				this.fileManager.createShortcut(path, ShortcutReason.FILE_CHANGED);
			}
		} catch (FileException e) {
			this.error(e.getMessage());
		}
        return null;
    }

	/**
	 * Executed in event dispatching thread. This method is called when the task
	 * finishes.
	 */
    @Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        for (Component component : this.sensitiveComponents) {
        	component.setEnabled(true);
        }
        
        // When a task ends, it can't be stopped anymore.
        this.stopButton.setEnabled(false);
        
        if (this.error != null) {
        	this.statusNote.setText("Error: " + this.error);
        } else if (this.isCancelled()) {
        	this.statusNote.setText("Task cancelled.");
        } else if (this.comparisonResults == null) {
        	this.statusNote.setText("Error: there are not comparison results.");
        } else {
        	int changedFiles = this.comparisonResults.getChangedFilePaths().size();
        	int addedFiles = this.comparisonResults.getNewFilePaths().size();
        	int deletedFiles = this.oldFilesThatNotExistInNewDirectory.size();
        	String changesString;
        	if (changedFiles + addedFiles + deletedFiles == 0) {
        		changesString = "The two folders are identical.";
        	} else {
        		changesString = "Files changed in new folder: " + changedFiles
        		+ ".\nFiles added in new folder: " + addedFiles
        		+ ".\nFiles deleted in new folder: " + deletedFiles + ".";
        	}
        	String zeroKbFiles = printCollection(this.comparisonResults.getZeroKbFilePaths());
        	String oldFilesThatNotExistInNew = printCollection(this.oldFilesThatNotExistInNewDirectory);
        	this.statusNote.setText(
        		"File comparison has been completed.\nFiles in old folder: " + this.totalOldFiles
        		+ ".\nFiles in new folder: " + this.totalNewFiles + ".\n" + changesString
        		+ "\n\nZero KB files" + zeroKbFiles
        		+ "\n\nOld files that don't exist in new directory"
        		+ oldFilesThatNotExistInNew
        	);
        }
    }
    
    private static String printCollection(Collection<String> collection) {
    	String result;
    	if (collection.size() > 0) {
    		result = "\n" + String.join("\n", collection);
    	} else {
    		result = ": none.";
    	}
    	return result;
    }
    
    private void compareRecursively(File newDirectory) throws FileException {
    	File[] newDirectoryChildren = newDirectory.listFiles();
    	int index = 0;
    	File newDirectoryChild;
    	while (!this.isCancelled() && index < newDirectoryChildren.length) {
    		newDirectoryChild = newDirectoryChildren[index];
    		if (newDirectoryChild.isFile()) {
    			Optional<Path> oldFilePath = this.fileManager.locateOldFile(newDirectoryChild.toPath());
    			if (newDirectoryChild.length() == 0) {
    				this.comparisonResults.addZeroKbFilePath(newDirectoryChild.getAbsolutePath());
    			}
    			if(!oldFilePath.isPresent()) {
    				this.comparisonResults.addNewFilePath(newDirectoryChild.getAbsolutePath());
    			} else if (!FileManager.filesEqual(Path.of(newDirectoryChild.getAbsolutePath()), oldFilePath.get())) {
    				this.comparisonResults.addChangedFilePath(newDirectoryChild.getAbsolutePath());
    			}
    			this.newFilesCompared++;
    			this.setProgress(Math.min(newFilesCompared * 90 / totalNewFiles, 90));
    		} else if (newDirectoryChild.isDirectory()) {
    			this.compareRecursively(newDirectoryChild);
    		}
    		index++;
    	}
    }

    private void getOldFilesThatNotExistInNewDirectoryRecursively(File oldDirectory) {
    	File[] oldDirectoryChildren = oldDirectory.listFiles();
    	int index = 0;
    	File oldDirectoryChild;
    	while (!this.isCancelled() && index < oldDirectoryChildren.length) {
    		oldDirectoryChild = oldDirectoryChildren[index];
    		if (oldDirectoryChild.isFile()) {
    			Optional<Path> newFilePath = this.fileManager.locateNewFile(oldDirectoryChild.toPath());
    			if(!newFilePath.isPresent()) {
    				this.oldFilesThatNotExistInNewDirectory.add(oldDirectoryChild.getAbsolutePath());
    			}
    			this.oldFilesChecked++;
    			this.setProgress(Math.min(90 + oldFilesChecked * 10 / totalOldFiles, 100));
    		} else if (oldDirectoryChild.isDirectory()) {
    			this.getOldFilesThatNotExistInNewDirectoryRecursively(oldDirectoryChild);
    		}
    		index++;
    	}
    }
    
    private void error(String error) {
    	this.cancel(true);
    	this.error = error;
    }
}