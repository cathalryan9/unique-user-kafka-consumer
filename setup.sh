#!/bin/bash
sudo apt-get install maven
./kafka_2.*/bin/zookeeper-server-start.sh kafka_2.*/config/zookeeper.properties |
./kafka_2.*/bin/kafka-server-start.sh kafka_2.*/config/server.properties |
./kafka_2.*/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic cathalryantest |
# Start app
mvn clean install
mvn exec:java
# Input data
#./kafka_2.*/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic cathalryantest < ../stream.jsonl
