package directory_comparator.ui.components;

import java.awt.Dimension;

import javax.swing.JSeparator;

/**
 * A horizontal separator.
 */
public class HorizontalSeparator extends JSeparator {

	private static final long serialVersionUID = -11618630229787899L;

	/**
	 * Constructor.
	 */
	public HorizontalSeparator() {
		// So the separator doesn't stretch vertically indefinitely.
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
	}
}
