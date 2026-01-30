package directory_comparator.ui.components;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.JTextArea;

/**
 * A read-only version of a {@link JTextArea}.
 */
public class ReadOnlyJTextArea extends JTextArea {

	private static final long serialVersionUID = 357272574720644231L;

	/**
	 * Constructor.
	 */
	public ReadOnlyJTextArea() {
		super(5, 20);
        this.setMargin(new Insets(5,5,5,5));
        this.setEditable(false);
        this.setCaretColor(new Color(255, 255, 255, 0));
        this.setWrapStyleWord(true);
        this.setLineWrap(true);
	}
}
