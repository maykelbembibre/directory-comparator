package directory_comparator.ui.workers.models;

import java.util.HashSet;
import java.util.Set;

public class ComparisonResults {

	private final Set<String> newFilePaths = new HashSet<>();
	private final Set<String> changedFilePaths = new HashSet<>();
	private final Set<String> zeroKbFilePaths = new HashSet<>();
	
	public void addNewFilePath(String path) {
		this.newFilePaths.add(path);
	}
	
	public void addChangedFilePath(String path) {
		this.changedFilePaths.add(path);
	}
	
	public void addZeroKbFilePath(String path) {
		this.zeroKbFilePaths.add(path);
	}
	
	public Set<String> getNewFilePaths() {
		return newFilePaths;
	}
	
	public Set<String> getChangedFilePaths() {
		return changedFilePaths;
	}

	public Set<String> getZeroKbFilePaths() {
		return zeroKbFilePaths;
	}
}
