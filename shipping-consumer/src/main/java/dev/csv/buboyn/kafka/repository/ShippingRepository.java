package dev.csv.buboyn.kafka.repository;

import dev.csv.buboyn.kafka.domain.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ShippingRepository {
    private Map<String, Order> orderStorage = new HashMap<String, Order>();


    public void addOrder(Order order) {
        orderStorage.put(order.getUuid(), order);
    }


    public Order findOrder(String orderId) {
        return orderStorage.get(orderId);
    }


    public List<Order> findOrders() {
        return orderStorage.values().stream().collect(Collectors.toList());
    }
}
