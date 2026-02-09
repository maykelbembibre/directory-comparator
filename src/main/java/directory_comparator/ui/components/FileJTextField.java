package directory_comparator.ui.components;

import java.io.File;

import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import directory_comparator.logic.PropertyManager;

/**
 * A {@link JTextField} whose content is the absolute path of a file.
 * Also, the content of this field is backed by a properties file.
 */
public class FileJTextField extends PropertyBackedJTextField {

	private static final long serialVersionUID = -7579550663906486422L;
	
	/**
	 * The file that is currently selected in this instance.
	 */
	private File selectedFile;

	/**
	 * Creates a new {@link JTextField} whose content is the absolute path of
	 * a file and is backed by a properties file.
	 * @param propertyManager The properties file manager instance.
	 * @param propertyKey The key of the property where the value of this instance
	 * will be saved.
	 */
	public FileJTextField(PropertyManager propertyManager, String propertyKey) {
		super(propertyManager, propertyKey);
		String text = super.getText();
		if (text != null && !text.isEmpty()) {
			this.selectedFile = new File(text);
		}
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
