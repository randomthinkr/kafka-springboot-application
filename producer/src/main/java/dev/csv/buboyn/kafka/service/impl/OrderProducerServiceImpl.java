package dev.csv.buboyn.kafka.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.csv.buboyn.kafka.domain.Order;
import dev.csv.buboyn.kafka.service.OrderProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class OrderProducerServiceImpl implements OrderProducerService {
    private final KafkaTemplate<String, Order> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic}")
    private String topicName;

    public OrderProducerServiceImpl(KafkaTemplate<String, Order> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }


    @Override
    public String sendOrder(Order order) throws JsonProcessingException {

        //String key = order.getUuid()

        //asynchronous call. Kafka handles retries
        //ListenableFuture<SendResult<String,Order>> listenableFuture = kafkaTemplate.send(topicName, key, order);
        ListenableFuture<SendResult<String,Order>> listenableFuture = kafkaTemplate.send(topicName, order);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, Order>>() {
            @Override
            public void onFailure(Throwable throwable) {
                //push to retry topic or store in a DB where a scheduler can update the topic later on.
                handleFailure(order, throwable);
            }

            @Override
            public void onSuccess(SendResult<String, Order> sendResult) {
                handleSuccess(order, sendResult);
            }
        });

        //returned immediately to the client like guaranteeing that the  order will be successfully created
        return order.getUuid();
    }

    private void handleSuccess(Order order, SendResult<String, Order> sendResult) {
        log.info("Message for order id - {} was sent to partition - {} for topic - {}", order.getUuid(), sendResult.getRecordMetadata().partition(), sendResult.getRecordMetadata().topic());
    }

    private void handleFailure(Order order, Throwable ex) {
        log.error("Failed to publish event with this error: {}", ex.getMessage());
        try{
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error on Failure handler {}", throwable.getMessage());
        }
    }
}
