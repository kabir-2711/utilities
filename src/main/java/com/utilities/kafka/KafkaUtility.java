package com.utilities.kafka;

import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import com.utilities.log.Log;

public class KafkaUtility {

	public static void postToKafka(Producer<String, String> producer, String logMessage, String topic) {
		String key = "";
		ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, logMessage);

		producer.send(record, new Callback() {
			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if (exception != null) {
					Log.error(Log.class.getSimpleName(), "postToKafka", "Failed to send message with key=%s%n%s", key,
							ExceptionUtils.getStackTrace(exception));
				} else {
					Log.info(Log.class.getSimpleName(), "postToKafka",
							"Message sent to topic=%s partition=%d offset=%d", metadata.topic(), metadata.partition(),
							metadata.offset());
				}
			}
		});
	}

	public static KafkaProducer<String, String> kafkaProps(String bootstrapServers) {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.RETRIES_CONFIG, 3);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 5);

		return new KafkaProducer<>(props);
	}

}
