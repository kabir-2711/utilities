package com.utilities.exceptions;

import lombok.Getter;

/**
 * Custom Exception Class extending {@link RuntimeException} class thrown to specify
 * and handle the Exceptions in the application to help debug
 * 
 * 
 * @author Kabir Akware
 */
@Getter
public class ConfigException extends RuntimeException {
	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Exception message
	 */
	private String message;

	/**
	 * Private constructor to call {@code super()}
	 * 
	 * @param message     Exception message
	 */
	private ConfigException(String message) {
		this.message = message;
	}

	/**
	 * Method to get a new instance of {@link ConfigException}
	 * 
	 * @param message     Custom error message
	 * @return New instance of {@link ConfigException}
	 */
	public static ConfigException getInstance(String message) {
		return new ConfigException(message);
	}

}
