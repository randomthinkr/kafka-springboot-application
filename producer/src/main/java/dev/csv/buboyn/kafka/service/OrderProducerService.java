package dev.csv.buboyn.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.csv.buboyn.kafka.domain.Order;

public interface OrderProducerService {
    String sendOrder(Order order) throws JsonProcessingException;
}
