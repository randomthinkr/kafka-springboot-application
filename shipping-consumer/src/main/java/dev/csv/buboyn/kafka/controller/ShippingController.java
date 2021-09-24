package dev.csv.buboyn.kafka.controller;

import dev.csv.buboyn.kafka.domain.Order;
import dev.csv.buboyn.kafka.service.ShippingConsumerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipping")
public class ShippingController {

    private final ShippingConsumerService shippingConsumerService;

    public ShippingController(ShippingConsumerService shippingConsumerService) {
        this.shippingConsumerService = shippingConsumerService;
    }


    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {

        return ResponseEntity.ok().body(shippingConsumerService.getOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {

        return ResponseEntity.ok().body(shippingConsumerService.getOrder(orderId));
    }

}
