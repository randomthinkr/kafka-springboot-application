package dev.csv.buboyn.kafka.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.csv.buboyn.kafka.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
public class OrderDeserializer implements Deserializer<Order> {

    @Override
    public Order deserialize(String topic, byte[] orderBytes) {

        try {
            if (null == orderBytes){
                log.info("Null received at deserializing");
                return null;
            }
            log.info("Deserializing...");
            return new ObjectMapper().readValue(new String(orderBytes, "UTF-8"), Order.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to Order");
        }
    }
}
