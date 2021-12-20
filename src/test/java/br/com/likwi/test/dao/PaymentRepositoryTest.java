package br.com.likwi.test.dao;

import br.com.likwi.test.enums.Currency;
import br.com.likwi.test.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository underTest;

    Payment payment_1;

    @BeforeEach
    public void setUnderTest() {
        payment_1 = Payment.builder()
                .paymentUId(1L)
                .customerId(UUID.randomUUID())
                .amount(BigDecimal.TEN)
                .currency(Currency.USD)
                .source("card123")
                .description("Donation")
                .build();

    }

    @Test
    void itShouldInsertPayment() {
        //Given
        //set in setUndetTest()

        //When
        this.underTest.save(payment_1);

        //Then
        final Optional<Payment> optionalPayment = this.underTest.findById(payment_1.getPaymentUId());
        assertThat(optionalPayment)
                .isPresent()
                .hasValueSatisfying(p -> {
                    assertThat(p).isEqualTo(payment_1); //need equals and hashcode
//                    assertThat(p).isEqualToComparingFieldByField(payment_1);
                });
    }
}
