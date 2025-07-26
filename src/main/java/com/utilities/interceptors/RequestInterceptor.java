package com.utilities.interceptors;

import java.lang.reflect.Type;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import com.utilities.log.Log;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class RequestInterceptor extends RequestBodyAdviceAdapter {

	/**
	 * {@link HttpServletRequest} parameter
	 */
	private HttpServletRequest request;

	private static boolean enable;

	public RequestInterceptor(@Value("${config.kafka.enable-audit-kafka}") Boolean enableKafka,
			HttpServletRequest req) {
		enable = enableKafka;
		request = req;
	}

	@Override
	public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Type targetType,
			@NonNull Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public @NonNull Object afterBodyRead(@NonNull Object body, @NonNull HttpInputMessage inputMessage,
			@NonNull MethodParameter parameter, @NonNull Type targetType,
			@NonNull Class<? extends HttpMessageConverter<?>> converterType) {
		if (enable) {
			String refNo = null;

			try {
				JSONObject obj = new JSONObject(body);
				refNo = obj.getString("refNo");
			} catch (JSONException e) {
				Log.warn(this.getClass().getSimpleName(), "afetrBodyRead",
						"Unable to fetch reference number.. setting value as null");
			}
			
			request.setAttribute("main-ref-no", refNo);
			request.setAttribute("main-cache-request", body);
		}
		return body;
	}
}
