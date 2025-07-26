package com.utilities.property;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.utilities.exceptions.ConfigException;
import com.utilities.log.Log;

/**
 * The {@code AppProperties} class is responsible to load property file from the
 * path mentioned in {@code application.properties} using {@link InputStream}
 * and caching the properties in memory for further use in the application.
 * 
 * @see <a href =
 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/stereotype/Component.html">
 *      Component </a>
 * @see Properties
 * @author Kabir Akware
 */
public class AppProperties {

	/**
	 * Private constructor to override the default constructor for this utility
	 * class which contains static utility methods
	 * 
	 * @param configPath
	 */
	private AppProperties(String configPath) {
		loadProperties(configPath);
	}

	/**
	 * {@code Properties} object
	 */
	private static final Properties props = new Properties();

	/**
	 * Method to return the cached properties currently loaded in the application
	 * 
	 * @return {@link Properties} object with cached properties
	 */
	public static Properties getLoadedProperties() {
		return props;
	}

	/**
	 * Method to get String property value in the application
	 * 
	 * @param key Key in the property file
	 * @return Value against the key as string
	 */
	public static String strProperty(String key) {
		return props.getProperty(key);
	}

	/**
	 * Method to get Integer property value in the application
	 * 
	 * @param key Key in the property file
	 * @return Value against the key as integer
	 */
	public static int intProperty(String key) {
		return Integer.parseInt(props.getProperty(key));
	}

	/**
	 * Method to load and cache property file in a static {@link Properties} object
	 * which can be accessed through static methods
	 * 
	 * @param configPath File path in which the properties are stored
	 */
	private void loadProperties(String configPath) {
		try (InputStream is = new FileInputStream(configPath)) {
			props.clear();
			props.load(is);
			Log.info(this.getClass().getSimpleName(), "loadProperties", "loaded %s properties from %s", props.size(),
					configPath);
		} catch (IOException e) {
			Log.error(this.getClass().getSimpleName(), "loadProperties",
					"exception occurred while loading configuration properties: %n%s", ExceptionUtils.getStackTrace(e));
			throw ConfigException
					.getInstance("exception occurred while loading configuration properties: " + e.getMessage());
		}
	}

	/**
	 * Method to get instance of {@link AppProperties} object and initiate loading
	 * the properties in the cache
	 * 
	 * @param configPath File path in which the properties are stored
	 * @return Instance of {@link AppProperties} object
	 */
	public static AppProperties initateLoadingProperties(String configPath) {
		return new AppProperties(configPath);
	}

	/**
	 * Method to refresh the cached property file.
	 * 
	 */
	public void refreshProperties(String configPath) {
		loadProperties(configPath);
		Log.info(this.getClass().getSimpleName(), "refreshProperties", "property file refreshed at %s",
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd hh:mm:ss.SSS")));
	}

	/**
	 * Method to clear the properties
	 */
	public void clean() {
		props.clear();
		Log.info(this.getClass().getSimpleName(), "clean", "properties cleared");
	}
}
