# kafka-springboot-application
Kafka Spring Boot project that uses the Kafka Producer and Consumer

# producer module
This project module contains the codes for the Kafka Producer application (localhost:8080)

# consumer module
This project module contains the codes for the Kafka Consumer application (localhost:8081)

# api-gateway
This project module contains Spring Cloud API Gateway routing so that both producer and consumer applications are routed to the same port (localhost:8088)

# postman-collection
This folder contains the sample Postman collection to fire a POST Rest request which triggers publishing to the Kafka topic.
This also contains a request to retrieve Orders that were created (at runtime, no db in this application)
