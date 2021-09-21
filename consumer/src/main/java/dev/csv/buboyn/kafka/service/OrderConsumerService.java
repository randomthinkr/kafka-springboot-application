package dev.csv.buboyn.kafka.service;

import dev.csv.buboyn.kafka.domain.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

public interface OrderConsumerService {
    List<Order> getOrders();
    Order getOrder(String orderId);

    void handleRecovery(ConsumerRecord<String, Order> consumerRecord);
}
