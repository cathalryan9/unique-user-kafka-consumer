# Unique-user-kafka-consumer
Read json messages/frames from Kafka to identify the number of unique users. uid is user ID field in the JSON message/frame.

How to run:
-
1. Clone this repository.
2. Kafka must be installed. Version 2.4 was used. Download the binary from https://kafka.apache.org/downloads and unzip it into the repository forlder.
3. Run setup.sh script. This will start the kafka server. create and configure a topic.  with "*mvn exec:java*"
5. Input data into topic. This can be done with the kafka console producer in the kafka/bin folder *./kafka-console-producer.sh --broker-list localhost:9092 --topic test < ../../stream.jsonl*. Stream.jsonl is the test data provided. The number of frames per minute will be output by the app.

How I did it?
-
- Polled the kafka topic every second to collect the new frames.
- Convert the frame into a JSON java object and extract ts and uid fields. JSON object is only parsed once per frame to improve performance.
- The uid of each frame is added to a Set. A set does not allow duplicates and this is ideal for our goal and we don't have to write code to check if it is a duplicate (Required with an arraylist). At the end of each minute the size of the set gives us the number of unique users in that minute.
- The start timestamp is recorded and used to output the frame count. The start timestamp is then updated to the next minute.
- The output provides the no. of frames for that minute. Some performance metrics such as time taken to count 1 minutes worth of data  and average no. of frames counted per second are in the output.
- It was implemented it this way because getting a working prototype first was important before trying to achieve more difficult requirements. Outputting the result after a minute of frames was the simplest form. 

Given more time I would...
-
- Implement a strategy to overcome the challenge of unordered frames and also have latent frames added to the previous set if its timestamp is in that range. 
- Output the number of frames as they are counted. Currently it is not clear if data is being processed or it is finished.
- Currently, the app does not support the processing of historical data. To fix this, during the consumer configuration a new Group ID would be used and offset adjusted. This offset would be based on how far back the data goes. A argument in the console command could be passed in to the app to declare a historical data run be done.
- Write the output in JSON format to a new topic *unique-users-minute*. A different topic would be used for other time periods. A JSON format will make it easy to use this data in the future since most languages provide simplified serialization json libraries.  A change in the format of the input data should be considered. The number of frames is large and only 2 fields in each frame are required so maybe there is some performance improvements to be gained by changing to a different format e.g. Avro.

Future considerations
-
- Currently the solution is designed to count frames per minute. This will need to be scaled to count per hour/day/week/month and year. Adjustments will need to be made to the app to make these longer time periods. One solution is to output data at shorter intervals to make part of the data accessible before it is fully counted.
- The number of frames could be an issue since it taking a bit less than 1 minute to count 1 minute of data on my machine. An increase in the size of the data would quickly overload the server. A cluster of instances running the app would reduce the load and an increase in frames would be manageable. The topics also become more complex as a result with an increase in partitions required and should be acknowledged.
- Since we are creating a set containing unique users for each minute, could the sets of each minute be stored on disk and after 60 minutes merge them efficiently without duplicates (sets wont add duplicates). This hasn't been tested but is worth considering and would scale to longer timeframes.

