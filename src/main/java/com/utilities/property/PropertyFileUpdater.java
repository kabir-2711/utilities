package com.utilities.property;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.utilities.exceptions.ConfigException;
import com.utilities.log.Log;

public class PropertyFileUpdater {

	/**
	 * Method to add or update a single property in the property file
	 * 
	 * @param fileName Property file path
	 * @param key      Key of the property
	 * @param value    Value of the property
	 * @throws ConfigException Thrown when there is a problem with updating or
	 *                         adding properties
	 */
	public static void updateProperty(String fileName, String key, String value) {
		File originalFile = new File(fileName);
		File tempFile = new File(fileName + ".temp");
		write(key, value, originalFile, tempFile);
		replaceFile(originalFile, tempFile);
	}

	private static void write(String key, String value, File originalFile, File tempFile) {
		try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

			String currentLine;
			boolean updated = false;
			while ((currentLine = reader.readLine()) != null) {
				if (currentLine.startsWith(key + "=")) {
					currentLine = key + "=" + value;
					updated = true;
				}
				writer.write(currentLine);
				writer.newLine();
			}

			if (!updated) {
				writer.write(key + "=" + value);
				writer.newLine();
			}

		} catch (IOException e) {
			Log.error(PropertyFileUpdater.class.getSimpleName(), "write",
					"Error occurred while reading/writing file:\n%s", ExceptionUtils.getStackTrace(e));
			throw ConfigException
					.getInstance(e.getMessage() + ": Error occurred while reading/writing file for key " + key);
		}
	}

	private static void replaceFile(File originalFile, File tempFile) {
		// Replace the original file with the updated file
		try {
			Files.move(tempFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Log.error(PropertyFileUpdater.class.getSimpleName(), "replaceFile",
					"Error occurred while updating file:\n%s", ExceptionUtils.getStackTrace(e));
			throw ConfigException.getInstance(e.getMessage() + ": Error occurred while updating file");
		}
	}
}
