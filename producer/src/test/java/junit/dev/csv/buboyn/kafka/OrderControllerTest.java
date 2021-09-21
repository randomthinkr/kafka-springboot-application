package junit.dev.csv.buboyn.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.csv.buboyn.kafka.controller.OrderController;
import dev.csv.buboyn.kafka.domain.Customer;
import dev.csv.buboyn.kafka.domain.Order;
import dev.csv.buboyn.kafka.domain.OrderItem;
import dev.csv.buboyn.kafka.service.OrderProducerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = OrderController.class)
@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
class OrderControllerTest {

    @MockBean
    OrderProducerService orderProducerService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Test publishing a message to Kafka")
    void postOrderRequest_callingActualBroker() throws Exception {

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

        String jsonRequest = objectMapper.writeValueAsString(order);

        //when and then
           mockMvc.perform(post("/api/v1/orders")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated());

    }

}