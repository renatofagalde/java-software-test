package br.com.likwi.test.integrationTest;

import br.com.likwi.test.api.controller.CustomerRegistrationController;
import br.com.likwi.test.bean.CustomerRegistrationRequest;
import br.com.likwi.test.bean.PaymentRequest;
import br.com.likwi.test.dao.PaymentRepository;
import br.com.likwi.test.enums.Currency;
import br.com.likwi.test.model.Customer;
import br.com.likwi.test.model.Payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRegistrationController customerRegistrationController;

    private final Faker faker = new Faker();

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {
        //Given a customer
        final UUID customerId = UUID.randomUUID();
        final Customer customer = Customer.builder()
                .id(customerId)
                .name(this.faker.gameOfThrones().character())
                .phoneNumber(this.faker.number().digits(10))
                .build();

        final CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        //register mock
        final ResultActions customerRegistrationResulAction = this.mockMvc.perform(put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(customerRegistrationRequest))));
        System.out.println("customerRegistrationResulAction = " + customerRegistrationResulAction.toString());

        //payment
        final long paymentId = 1l;
        final Payment payment = new Payment(paymentId,
                customerId,
                new BigDecimal("10.00"),
                Currency.USD,
                "cardx1x1x1",
                "it test");
        final PaymentRequest paymentRequest = new PaymentRequest(payment);

        //When payment is sent
        final ResultActions paymentResultAction = mockMvc.perform(post("/api/v1/payment/{customerId}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(paymentRequest))));

        //Then both costomer registration and payment request are
        //200 status code
        customerRegistrationResulAction.andExpect(status().isOk());
        paymentResultAction.andExpect(status().isOk());

        //payment is stored in db
        //todo: do not use paymentRepostiry, this is an example
        assertThat(this.paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(p -> assertThat(p).isEqualToComparingFieldByField(payment));

        //todo sms is delivered

    }

    private String objectToJson(Object object) {

        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException jpe) {
            fail("Failed to convert object to json");
            return null;
        }
    }
}
