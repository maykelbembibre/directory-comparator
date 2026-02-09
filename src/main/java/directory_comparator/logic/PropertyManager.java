package directory_comparator.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class PropertyManager {

	private final String name;
	private final File propertyFile;
	private Properties properties;
	
	public PropertyManager(String name) {
		this.name = name;
		String userHomePath = System.getProperty("user.home");
		File userHomeDirectory = new File(userHomePath);
		if (userHomeDirectory.isDirectory()) {
			this.propertyFile = new File(userHomeDirectory, name + ".properties");
		} else {
			this.propertyFile = null;
			System.err.println("WARNING: this property manager instance won't work because it can't access the user home directory.");
		}
		this.loadFromFile();
	}

	public Optional<String> getProperty(String key) {
		Optional<String> result;
		if (this.properties == null) {
			result = Optional.empty();
		} else {
			result = Optional.ofNullable(this.properties.getProperty(key));
		}
		return result;
	}
	
	public void setProperty(String key, String value) {
		if (this.properties != null) {
			this.properties.put(key, value);
			this.storeToFile();
		}
	}
	
	private void storeToFile() {
		if (this.propertyFile != null) {
			FileOutputStream outputStrem;
			try {
				outputStrem = new FileOutputStream(this.propertyFile);
				this.properties.store(outputStrem, "This is a properties file for saving the state of the " + this.name + " app.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadFromFile() {
		if (this.propertyFile == null) {
			this.properties = null;
		} else {
			this.properties = new Properties();
			FileInputStream fileInputStream;
			if (this.propertyFile.exists()) {
				try {
					fileInputStream = new FileInputStream(propertyFile);
					this.properties.load(fileInputStream);
				} catch (IOException e) {
					this.properties = null;
					System.err.println("WARNING: this property manager instance won't work because of an IO error: " + e.getMessage());
				}
			} else {
				this.storeToFile();
			}
		}
	}
}
