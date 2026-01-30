package directory_comparator.logic.enums;

/**
 * The reason why the program creates a shortcut in the results directory.
 */
public enum ShortcutReason {

	/**
	 * An existing file has been changed in the new directory.
	 */
	FILE_CHANGED,
	
	/**
	 * A new file has been created in the new directory.
	 */
	FILE_CREATED
}
