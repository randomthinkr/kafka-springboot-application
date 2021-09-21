package dev.csv.buboyn.kafka.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.csv.buboyn.kafka.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

@Slf4j
public class OrderSerializer implements Serializer<Order> {
    @Override
    public byte[] serialize(String topic, Order order) {

        try {
            if (null == order){
               log.info("Null received for serializing");
                return null;
            }
            log.info("Serializing...");
            return new ObjectMapper().writeValueAsBytes(order);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error when serializing Order to byte[]");
        }

    }
}
