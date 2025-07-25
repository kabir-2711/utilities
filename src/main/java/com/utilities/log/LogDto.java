package com.utilities.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;

@Getter
public class LogDto {

	private LogDto(String service, String clazz, String method, long threadId, String logLevel, String time,
			String message) {
		this.service = service;
		this.clazz = clazz;
		this.method = method;
		this.threadId = threadId;
		this.logLevel = logLevel;
		this.time = time;
		this.message = message;
	}

	private String service;
	private String clazz;
	private String method;
	private long threadId;
	private String logLevel;
	private String time;
	private String message;

	public static LogDto getInstance(String service, String clazz, String method, long threadId, String logLevel,
			String message) {
		return new LogDto(service, clazz, method, threadId, logLevel,
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd hh:mm:ss.SSS")), message);
	}
}
