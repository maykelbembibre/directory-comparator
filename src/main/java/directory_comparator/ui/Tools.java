package directory_comparator.ui;

import java.awt.Dimension;
import java.awt.Insets;

/**
 * General UI tools.
 */
public class Tools {

	/**
	 * Adds the given dimension and insets.
	 * @param dimension A dimension.
	 * @param insets The insets.
	 * @return The result of adding the parameters together.
	 */
	public static Dimension add(Dimension dimension, Insets insets) {
		return new Dimension(
			dimension.width + insets.left + insets.right,
			dimension.height + insets.top + insets.bottom
		);
	}
}
