package integration.dev.csv.buboyn.kafka.controller;

import dev.csv.buboyn.kafka.KafkaConsumerApplication;
import dev.csv.buboyn.kafka.deserializer.OrderDeserializer;
import dev.csv.buboyn.kafka.domain.Customer;
import dev.csv.buboyn.kafka.domain.Order;
import dev.csv.buboyn.kafka.domain.OrderItem;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = KafkaConsumerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"orders-test"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class OrderConsumerServiceEmbeddedKafkaTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, Order> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("order-test-group", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<String, Order>(configs, new StringDeserializer(), new OrderDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    /**
     *
     * This test doesn't currently work because there is the producer is separate
     * from the consumer. Make a way for the TestRestTemplate to work by publishing a topic.
     */

    @Test
    @DisplayName("Test consuming a message from Embedded Kafka")
    //@Timeout(7)
    void consumingAMessageFromEmbeddedKafka() throws InterruptedException {

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

        ConsumerRecord<String, Order> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "orders-test");

        String message = consumerRecord.value().toString();
        String expectedMessage = "";
        //then
        assertThat(HttpStatus.CREATED).isEqualTo(responseEntity.getStatusCode());
        assertThat(expectedMessage).isEqualTo(message);
    }

}