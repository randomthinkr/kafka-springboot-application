package dev.csv.buboyn.kafka.service.impl;

import dev.csv.buboyn.kafka.domain.Order;
import dev.csv.buboyn.kafka.repository.ShippingRepository;
import dev.csv.buboyn.kafka.service.ShippingConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

@Component
@Slf4j
public class ShippingConsumerServiceImpl implements ShippingConsumerService {

    private final ShippingRepository shippingRepository;

    public ShippingConsumerServiceImpl(ShippingRepository shippingRepository, KafkaTemplate<String, Order> kafkaTemplate) {
        this.shippingRepository = shippingRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public ShippingRepository getShippingRepository() {
        return shippingRepository;
    }

    public KafkaTemplate<String, Order> getKafkaTemplate() {
        return kafkaTemplate;
    }

    private final KafkaTemplate<String, Order> kafkaTemplate;

    @Value("${kafka.topic}")
    private String topicName;


    @KafkaListener(topics = "${kafka.topic:orders}", groupId = "${spring.kafka.consumer.group-id}")
    public void processOrder(@Payload Order order,
                              @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
       log.info("Received Order for shipment: {} from Partition {}", order, partition);

        shippingRepository.addOrder(order);
    }


    @Override
    public List<Order> getOrders() {
        return shippingRepository.findOrders();
    }

    @Override
    public Order getOrder(String orderId) {
        return shippingRepository.findOrder(orderId);
    }

    @Override
    public void handleRecovery(ConsumerRecord<String, Order> consumerRecord) {
        //attempt to recover by sending back to the topic the same record
        Order order = consumerRecord.value();
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
