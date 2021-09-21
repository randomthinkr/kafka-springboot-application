package dev.csv.buboyn.kafka.config;


import dev.csv.buboyn.kafka.deserializer.OrderDeserializer;
import dev.csv.buboyn.kafka.domain.Order;
import dev.csv.buboyn.kafka.service.OrderConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfiguration {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Autowired
    private OrderConsumerService consumerService;

    @Bean
    public ConsumerFactory<String, Order> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,  groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, OrderDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(Order.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Order>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Order> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        factory.setErrorHandler(((thrownException, data) -> {
            log.error("Exception in consumer configuration: {} with the record: {} ", thrownException.getMessage(), data);
        }));
        factory.setRetryTemplate(retryTemplate());
        factory.setRecoveryCallback((context -> {
            if(context.getLastThrowable().getCause() instanceof RecoverableDataAccessException){
                //here you can do your recovery mechanism where you can put back on to the topic using a Kafka producer
                ConsumerRecord<String, Order> consumerRecord = (ConsumerRecord<String, Order>) context.getAttribute("record");
                consumerService.handleRecovery(consumerRecord);
            } else{
                // here you can log things and throw some custom exception that Error handler will take care of
                throw new RuntimeException(context.getLastThrowable().getMessage());
            }
            return null;
        }));
        return factory;
    }

    private RetryTemplate retryTemplate() {
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        RetryTemplate retryTemplate = new RetryTemplate();
        fixedBackOffPolicy.setBackOffPeriod(1_000);
        retryTemplate.setRetryPolicy(retryPolicy());
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        return retryTemplate;
    }

    private RetryPolicy retryPolicy() {

        Map<Class<? extends Throwable>, Boolean> exceptions = new HashMap<>();
        exceptions.put(IllegalArgumentException.class, false); //no need to retry, invalid request data will always raise IllegalArgumentException
        exceptions.put(TimeoutException.class, true);
        exceptions.put(RecoverableDataAccessException.class, true); //temporary glitch like a temporary network issue, can recover, hence, true
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(3, exceptions, true); //traverse exceptions
        return simpleRetryPolicy;
    }
}
