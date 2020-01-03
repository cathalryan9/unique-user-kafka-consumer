package com.kafka.unique_user_kafka_consumer;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class App {
	private final static String TOPIC = "cathalryantest";
	private final static String BOOTSTRAP_SERVERS = "localhost:9092";
	private final static long INTERVAL = 60;

	public static void main(String[] args) {
		System.out.println("Initializing..");
		final Properties props = new Properties();

		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,

				BOOTSTRAP_SERVERS);

		props.put(ConsumerConfig.GROUP_ID_CONFIG,

				"KafkaExampleConsumer");

		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,

				LongDeserializer.class.getName());

		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,

				StringDeserializer.class.getName());

		// Create the consumer using props.

		final Consumer<Long, String> consumer =

				new KafkaConsumer<Long, String>(props);

		// Subscribe to the topic.
		consumer.subscribe(Collections.singletonList(TOPIC));

		long startTimestamp = 0;
		Set<String> userSet = new HashSet<String>();
		long startTime = System.nanoTime();
		try {
			System.out.println("Reading from Kafka topic " + TOPIC);
			while (true) {
				// Poll the topic to retrieve new messages
				ConsumerRecords<Long, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));

				Iterator<ConsumerRecord<Long, String>> a = consumerRecords.iterator();
				while (a.hasNext()) {
					
					ConsumerRecord<Long, String> record = a.next();
					Gson h = new Gson();
					JsonObject json = h.fromJson(record.value(), JsonObject.class);
					Long timestamp = json.get("ts").getAsLong();
					if (startTimestamp == 0) {
						startTimestamp = timestamp;
					}

					if (timestamp > startTimestamp + INTERVAL) {
						// Here create method to send to kafka in Json format

						// Write to console
						System.out.print("Minute - ");
						System.out.print(startTimestamp);
						System.out.print("\tTotal frames - ");
						System.out.println(userSet.size());

						// Performance metrics
						long endTime = System.nanoTime();
						System.out.print("Duration for " + userSet.size() + " frames: ");
						System.out.println(endTime - startTime);
						System.out.print("Average frames per second: ");
						System.out.println(userSet.size() / INTERVAL);
						startTime = endTime;

						startTimestamp = startTimestamp + INTERVAL;
						userSet.clear();
					}
					// HashSet won't add duplicates
					userSet.add(json.get("uid").getAsString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close the consumer object
			consumer.close();
		}

	}
}
