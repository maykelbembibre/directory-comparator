package directory_comparator.ui.components;

import javax.swing.JFileChooser;

/**
 * An extension of {@link JFileChooser} that chooses a directory.
 */
public class DirectoryChooser extends JFileChooser {

	private static final long serialVersionUID = 2904776470756141581L;

	/**
	 * Constructor.
	 */
	public DirectoryChooser() {
		this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.setDialogTitle("Select a folder");
		this.setApproveButtonText("Select");
	}
}
