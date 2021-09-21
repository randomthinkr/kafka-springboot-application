package dev.csv.buboyn.kafka.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.csv.buboyn.kafka.domain.Order;
import org.apache.kafka.common.serialization.Serializer;

public class OrderSerializer implements Serializer<Order> {
    @Override
    public byte[] serialize(String topic, Order order) {
        try {
            return new ObjectMapper().writeValueAsBytes(order);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
