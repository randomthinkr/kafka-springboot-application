package dev.csv.buboyn.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;


/**
 * NOTE: This is only meant for development. In production, the bootstrap servers and the topics, partitions, and replicas are set up
 */

@Configuration
@Profile("dev")
public class AutoCreateTopicConfiguration {

    @Bean
    public NewTopic orderEvents() {
        return TopicBuilder.name("orders")
                .partitions(3)
                .replicas(3)
                .build();
    }
}
