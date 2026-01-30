package directory_comparator.ui.components;

import java.io.File;

import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * A {@link JTextField} whose content is the absolute path of a file.
 */
public class FileJTextField extends JTextField {

	private static final long serialVersionUID = -7579550663906486422L;
	
	private File selectedFile;

	/**
	 * Creates a text field whose content is the absolute path of a file.
	 */
	public FileJTextField() {
		this.addCaretListener(new CaretListener() {
			private String lastValue = "";
			@Override
			public void caretUpdate(CaretEvent e) {
				String currentVal = getText();
				if (!this.lastValue.equals(currentVal)) {
				    if (currentVal == null || currentVal.isEmpty()) {
				    	selectedFile = null;
				    } else {
				    	selectedFile = new File(currentVal);
				    }
				    if (currentVal != null) {
				    	this.lastValue = currentVal;
				    }
			    }
			}
		});
	}
	
	/**
	 * Returns the selected file.
	 * @return The selected File.
	 */
	public File getSelectedFile() {
		return selectedFile;
	}

	/**
	 * Sets the selected file.
	 * @param selectedFile The selected file.
	 */
	public void setSelectedFile(File selectedFile) {
		this.setText(selectedFile.getAbsolutePath());
	}
}
