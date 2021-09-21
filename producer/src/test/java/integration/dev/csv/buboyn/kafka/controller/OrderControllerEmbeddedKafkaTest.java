package integration.dev.csv.buboyn.kafka.controller;

import dev.csv.buboyn.kafka.KafkaProducerApplication;
import dev.csv.buboyn.kafka.domain.Customer;
import dev.csv.buboyn.kafka.domain.Order;
import dev.csv.buboyn.kafka.domain.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = KafkaProducerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"orders-test"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class OrderControllerEmbeddedKafkaTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("Test publishing a message to Embedded Kafka")
    void postOrderRequest_callingEmbeddedKafka() {

        //given
        List<OrderItem> orderItems = List.of(
                OrderItem.builder().itemName("Game&Watch").amount(2000).build(),
                OrderItem.builder().itemName("PS5").amount(33000).build()
        );
        Order order = Order.builder()
                .items(orderItems)
                .customer(Customer.builder()
                        .name("Keefe Navaja")
                        .address("Coronado St, Mandaluyong")
                        .build())
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("content-type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Order> request = new HttpEntity<>(order, httpHeaders);

        //when
        ResponseEntity<String> responseEntity = testRestTemplate.exchange("/api/v1/orders", HttpMethod.POST, request, String.class);

        //then
        assertThat(HttpStatus.CREATED).isEqualTo(responseEntity.getStatusCode());
    }

}