package com.utilities.interceptors;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.google.gson.Gson;
import com.utilities.exceptions.ConfigException;
import com.utilities.kafka.KafkaUtility;

import jakarta.servlet.http.HttpServletRequest;

public class ResponseInterceptor implements ResponseBodyAdvice<Object> {

	/**
	 * {@link HttpServletRequest} parameter
	 */
	private HttpServletRequest request;

	private static Gson gson;

	private static String topic;

	private static boolean enable;

	private static KafkaProducer<String, String> producer;

	public ResponseInterceptor(@Value("${config.kafka.enable-audit-kafka}") Boolean enableKafka,
			@Value("${config.kafka.server-details}") String serverDetails,
			@Value("${config.kafka.audit-topic}") String t, HttpServletRequest req) {
		gson = new Gson().newBuilder().disableHtmlEscaping().serializeNulls().serializeSpecialFloatingPointValues()
				.setPrettyPrinting().create();
		enable = enableKafka;
		request = req;
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

	@Override
	public boolean supports(@NonNull MethodParameter returnType,
			@NonNull Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	@Nullable
	public Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType,
			@NonNull MediaType selectedContentType,
			@NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NonNull ServerHttpRequest request,
			@NonNull ServerHttpResponse response) {
		if (enable)
			try {
				KafkaUtility.postToKafka(producer,
						gson.toJson(
								AuditKafkaDto.getInstance(LocalDateTime.now().format(DateTimeFormatter.ofPattern("")),
										request.getURI().getPath(), this.request.getAttribute("main-ref-no"), 0,
										gson.toJson(request.getBody()), gson.toJson(response.getBody()))),
						topic);
			} catch (IOException e) {
				e.printStackTrace();
			}

		return body;
	}
}
