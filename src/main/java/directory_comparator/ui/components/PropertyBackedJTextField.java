package directory_comparator.ui.components;

import java.util.Optional;

import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import directory_comparator.logic.PropertyManager;

/**
 * A {@link JTextField} whose content is backed in a properties file.
 */
public class PropertyBackedJTextField extends JTextField {

	private static final long serialVersionUID = -55720539581272550L;
	
	private final PropertyManager propertyManager;
	private final String propertyKey;

	/**
	 * Creates a {@link JTextField} whose content is backed in a properties file.
	 * @param propertyManager The properties file manager instance.
	 * @param propertyKey The key of the property where the value of this instance
	 * will be saved.
	 */
	public PropertyBackedJTextField(PropertyManager propertyManager, String propertyKey) {
		this.propertyManager = propertyManager;
		this.propertyKey = propertyKey;
		Optional<String> propertyValue = propertyManager.getProperty(propertyKey);
		if (propertyValue.isPresent()) {
			super.setText(propertyValue.get());
		}
		this.addCaretListener(new CaretListener() {
			private String lastValue = "";
			@Override
			public void caretUpdate(CaretEvent e) {
				String currentVal = getText();
				if (!this.lastValue.equals(currentVal)) {
				    propertyManager.setProperty(propertyKey, currentVal);
				    if (currentVal != null) {
				    	this.lastValue = currentVal;
				    }
			    }
			}
		});
	}
	
	@Override
	public void setText(String text) {
		super.setText(text);
		this.propertyManager.setProperty(this.propertyKey, text);
	}
}
