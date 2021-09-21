package dev.csv.buboyn.kafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.csv.buboyn.kafka.domain.Order;
import dev.csv.buboyn.kafka.service.OrderProducerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderProducerService orderProducerService;

    public OrderController(OrderProducerService orderProducerService) {
        this.orderProducerService = orderProducerService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) throws JsonProcessingException {
        orderProducerService.sendOrder(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(order.getUuid());
    }

}
