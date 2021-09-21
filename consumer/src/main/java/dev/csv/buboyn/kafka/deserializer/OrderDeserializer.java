package dev.csv.buboyn.kafka.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.csv.buboyn.kafka.domain.Order;
import org.apache.kafka.common.serialization.Deserializer;

public class OrderDeserializer implements Deserializer<Order> {

    @Override
    public Order deserialize(String topic, byte[] order) {
        try {
            return new ObjectMapper().readValue(order, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
