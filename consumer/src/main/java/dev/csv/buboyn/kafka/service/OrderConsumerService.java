package dev.csv.buboyn.kafka.service;

import dev.csv.buboyn.kafka.domain.Order;

import java.util.List;

public interface OrderConsumerService {
    List<Order> getOrders();
    Order getOrder(String orderId);
}
