package com.utilities.log;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.utilities.exceptions.ConfigException;
import com.utilities.kafka.KafkaUtility;

/**
 * Custom class to log information in the application with different log level
 * methods catering to a wide variety of log level messages and providing a
 * homogeneous structure throughout the application while logging information.
 * 
 * @author Kabir Akware
 */
@Component
public class Log {

	private static Gson gson;

	private static String topic;

	private static String service;

	private static boolean enable;

	private static KafkaProducer<String, String> producer;

	public Log(@Value("apring.application.name") String serviceName,
			@Value("config.property.package") String appPackage,
			@Value("${config.kafka.enable-log-kafka}") Boolean enableKafka,
			@Value("${config.kafka.server-details}") String serverDetails,
			@Value("${config.kafka.log-topic}") String t) {
		service = serviceName;
		logger = LoggerFactory.getLogger(appPackage);
		gson = new Gson().newBuilder().disableHtmlEscaping().serializeNulls().serializeSpecialFloatingPointValues()
				.setPrettyPrinting().create();
		info(Log.class.getSimpleName(), "constructor", "logger initialized..");
		enable = enableKafka;
		if (enableKafka) {
			try {
				topic = t;
				producer = KafkaUtility.kafkaProps(serverDetails);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw ConfigException.getInstance(
						"need to provide two additional params if param[1] is true. provide bootstrap server & topic details respectively");
			}
		}
	}

	/**
	 * {@link Logger} declaration
	 */
	private static Logger logger;

	/**
	 * Single-threaded executor to ensure logs are processed in order
	 */
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();

	/**
	 * Method to log informational messages asynchronously
	 * 
	 * @param className  Class name for logging
	 * @param methodName Method name for logging
	 * @param message    Log message
	 * @param arguments  Arguments passed
	 */
	public static void info(String className, String methodName, String message, Object... arguments) {
		long id = Thread.currentThread().getId();
		executor.submit(() -> {
			logger.info(formatLogMessage(service, className, methodName, id, "info", message, arguments));
		});
	}

	/**
	 * Method to log debugger messages asynchronously
	 * 
	 * @param className  Class name for logging
	 * @param methodName Method name for logging
	 * @param message    Log message
	 * @param arguments  Arguments passed
	 */
	public static void debug(String className, String methodName, String message, Object... arguments) {
		long id = Thread.currentThread().getId();
		executor.submit(() -> {
			logger.debug(formatLogMessage(service, className, methodName, id, "debug", message, arguments));
		});
	}

	/**
	 * Method to log error messages asynchronously
	 * 
	 * @param className  Class name for logging
	 * @param methodName Method name for logging
	 * @param message    Log message
	 * @param arguments  Arguments passed
	 */
	public static void error(String className, String methodName, String message, Object... arguments) {
		long id = Thread.currentThread().getId();
		executor.submit(() -> {
			logger.error(formatLogMessage(service, className, methodName, id, "error", message, arguments));
		});
	}

	/**
	 * Method to log warning messages asynchronously
	 * 
	 * @param className  Class name for logging
	 * @param methodName Method name for logging
	 * @param message    Log message
	 * @param arguments  Arguments passed
	 */
	public static void warn(String className, String methodName, String message, Object... arguments) {
		long id = Thread.currentThread().getId();
		executor.submit(() -> {
			logger.warn(formatLogMessage(service, className, methodName, id, "warn", message, arguments));
		});
	}

	/**
	 * Method to log trace messages asynchronously
	 * 
	 * @param className  Class name for logging
	 * @param methodName Method name for logging
	 * @param message    Log message
	 * @param arguments  Arguments passed
	 */
	public static void trace(String className, String methodName, String message, Object... arguments) {
		long id = Thread.currentThread().getId();
		executor.submit(() -> {
			logger.trace(formatLogMessage(service, className, methodName, id, "trace", message, arguments));
		});
	}

	/**
	 * Formats the log message.
	 * 
	 * @param serviceName Service name
	 * @param className   Class name for logging
	 * @param methodName  Method name for logging
	 * @param message     Log message
	 * @param arguments   Arguments passed
	 * @param logLevel    Log level
	 * @param threadId    Thread id
	 * @return Formatted log message
	 */
	private static String formatLogMessage(String serviceName, String className, String methodName, long threadId,
			String logLevel, String message, Object... arguments) {
		String logMessage = gson.toJson(LogDto.getInstance(serviceName, className, methodName, threadId, logLevel,
				String.format(message, Arrays.stream(arguments).map(arg -> {
					if (arg instanceof Object[])
						return String.join(", ", (CharSequence) Arrays.asList((Object[]) arg));
					if (arg == null)
						return "";
					return arg;
				}).toArray())));

		if (enable)
			KafkaUtility.postToKafka(producer, logMessage, topic);
		return logMessage;
	}
}
