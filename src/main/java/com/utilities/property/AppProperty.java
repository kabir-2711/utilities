package com.utilities.property;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppProperty {

	private String key;

	private String value;

	@Override
	public String toString() {
		return new StringBuilder().append("{\"key\": \"").append(key).append("\", \"value\": \"").append(value)
				.toString();
	}
}
