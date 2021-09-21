package dev.csv.buboyn.kafka.service.impl;

import dev.csv.buboyn.kafka.domain.Order;
import dev.csv.buboyn.kafka.repository.OrderRepository;
import dev.csv.buboyn.kafka.service.OrderConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OrderConsumerServiceImpl implements OrderConsumerService {

    private final OrderRepository orderRepository;

    public OrderConsumerServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "${kafka.topic:orders}", groupId = "${spring.kafka.consumer.group-id}")
    public void processOrder(@Payload Order order,
                              @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
       log.info("Received Order: {} from Partition {}", order, partition);

       orderRepository.addOrder(order);
    }


    @Override
    public List<Order> getOrders() {
        return orderRepository.findOrders();
    }

    @Override
    public Order getOrder(String orderId) {
        return orderRepository.findOrder(orderId);
    }
}
