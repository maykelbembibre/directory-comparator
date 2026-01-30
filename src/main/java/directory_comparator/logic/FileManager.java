package directory_comparator.logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

import directory_comparator.exceptions.FileException;
import directory_comparator.logic.enums.ShortcutReason;
import mslinks.ShellLink;

/**
 * Class with the logic for comparing directories.
 */
public class FileManager {

	private final File oldDirectory;
	private final File newDirectory;
	private final File resultsNewFiles;
	private final File resultsChangedFiles;
	
	/**
	 * Creates a new object for managing files to do a directory comparison.
	 * @param oldDirectory The old directory.
	 * @param newDirectory The new directory.
	 * @param resultsDirectory The directory to put the results.
	 * @throws FileException If some of the parameters is invalid.
	 */
	public FileManager(File oldDirectory, File newDirectory, File resultsDirectory) throws FileException {
		if (oldDirectory == null) {
			throw new FileException("You must select the old folder.");
		}
		if (newDirectory == null) {
			throw new FileException("You must select the new folder.");
		}
		if (resultsDirectory == null) {
			throw new FileException("You must select the results folder.");
		}
    	if (!oldDirectory.isDirectory()) {
    		throw new FileException("The old directory doesn't exist.");
    	}
    	if (!newDirectory.isDirectory()) {
    		throw new FileException("The new directory doesn't exist.");
    	}
    	this.oldDirectory = oldDirectory;
		this.newDirectory = newDirectory;
		this.resultsNewFiles = new File(resultsDirectory, "New files");
		this.resultsChangedFiles = new File(resultsDirectory, "Changed files");
    	try {
			Files.createDirectories(this.resultsNewFiles.toPath());
			Files.createDirectories(this.resultsChangedFiles.toPath());
		} catch (IOException e) {
			throw new FileException("Something is wrong with the results directory.");
		}
    	deleteContents(this.resultsNewFiles);
    	deleteContents(this.resultsChangedFiles);
	}
	
	/**
	 * Counts the total number of files (not directories) in the given
	 * directory.
	 * @param directory A directory.
	 * @return
	 */
	public static int countFiles(File directory) {
		int result;
		if (directory.isDirectory()) {
			result = countFilesRecursively(directory);
		} else {
			result = 1;
		}
		return result;
	}
	
	/**
	 * Locates the file in the old directory that corresponds to the
	 * given path of a file in the new directory.
	 * @param newFilePath The path of a file in the new directory.
	 * @return The corresponding old file or an empty {@link Optional}
	 * if there's no such file.
	 */
	public Optional<Path> locateOldFile(Path newFilePath) {
		Path newRelativePath = this.newDirectory.toPath().relativize(newFilePath);
		Path oldFilePath = this.oldDirectory.toPath().resolve(newRelativePath);
		File oldFile = oldFilePath.toFile();
		Optional<Path> result;
		if (oldFile.exists()) {
			result = Optional.of(oldFilePath);
		} else {
			result = Optional.empty();
		}
		return result;
	}
	
	/**
	 * Locates the file in the new directory that corresponds to the
	 * given path of a file in the old directory.
	 * @param oldFilePath The path of a file in the old directory.
	 * @return The corresponding new file or an empty {@link Optional}
	 * if there's no such file.
	 */
	public Optional<Path> locateNewFile(Path oldFilePath) {
		Path oldRelativePath = this.oldDirectory.toPath().relativize(oldFilePath);
		Path newFilePath = this.newDirectory.toPath().resolve(oldRelativePath);
		File newFile = newFilePath.toFile();
		Optional<Path> result;
		if (newFile.exists()) {
			result = Optional.of(newFilePath);
		} else {
			result = Optional.empty();
		}
		return result;
	}
	
	public static boolean filesEqual(Path path, Path path2) throws FileException {
		try {
			return Files.mismatch(path, path2) == -1;
		} catch (NoSuchFileException e) {
			throw new FileException("It looks like the file " + e.getFile() + " doesn't exist anymore.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileException("I/O error when comparing " + path + " with " + path2 + ".");
		}
	}
	
	public void createShortcut(String newOrChangedPath, ShortcutReason reason) throws FileException {
		File newOrChangedFile = new File(newOrChangedPath);
		String shortcutFileName = stripExtension(newOrChangedFile.getName()) + ".lnk";
		File where;
		if (reason == ShortcutReason.FILE_CHANGED) {
			where = this.resultsChangedFiles;
		} else if (reason == ShortcutReason.FILE_CREATED) {
			where = this.resultsNewFiles;
		} else {
			throw new RuntimeException("reason is mandatory.");
		}
		File shortcutFile = new File(where, shortcutFileName);
		FileManager.createShortcut(newOrChangedPath, shortcutFile.getAbsolutePath());
	}

	private static void deleteContents(File file) {
		if (file.isDirectory()) {
			deleteContentsRecursively(file);
		} else if (file.isFile()) {
			file.delete();
		}
	}
	
	private static String stripExtension(String fileName) {
		String result = "";
		if (fileName != null && !fileName.isEmpty()) {
			int pos = fileName.length() - 1;
			while (pos > 0 && result.isEmpty()) {
				if (fileName.charAt(pos) == '.') {
					result = fileName.substring(0, pos);
				}
				pos--;
			}
		}
		return result;
	}
	
	private static void createShortcut(String targetPath, String createdShortcutPath) throws FileException {
		try {
			ShellLink.createLink(targetPath, createdShortcutPath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileException("ERROR: can't create shortcut to " + targetPath + ".");
		}	
	}
	
	private static void deleteContentsRecursively(File directory) {
		File[] children = directory.listFiles();
		for (File child : children) {
			if (child.isFile()) {
				child.delete();
			} else if (child.isDirectory()) {
				deleteContentsRecursively(child);
			}
		}
	}
	
	private static int countFilesRecursively(File directory) {
		int result = 0;
		File[] children = directory.listFiles();
		for (File child : children) {
			if (child.isFile()) {
				result++;
			} else if (child.isDirectory()) {
				result = result + countFilesRecursively(child);
			}
		}
		return result;
	}
}
