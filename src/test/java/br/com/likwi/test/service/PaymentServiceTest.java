package br.com.likwi.test.service;

import br.com.likwi.test.bean.PaymentRequest;
import br.com.likwi.test.dao.CustomerRepository;
import br.com.likwi.test.dao.PaymentRepository;
import br.com.likwi.test.enums.Currency;
import br.com.likwi.test.interfacing.payment.CardPaymentCharger;
import br.com.likwi.test.model.Customer;
import br.com.likwi.test.model.Payment;
import br.com.likwi.test.payment.CardPaymentCharge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService underTest;

    //private List<Currency> ACCEPTED_CURRENCIES = Arrays.asList(Currency.values());

    @BeforeEach
    void setup() {

        MockitoAnnotations.initMocks(this);

        this.underTest = new PaymentService(this.customerRepository,
                this.paymentRepository,
                this.cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        //Given
        UUID customerId = UUID.randomUUID();
        // customer exits
        given(this.customerRepository.findById(customerId)).willReturn(
                Optional.of(mock(Customer.class))
        );

        Currency currency = Currency.USD;
        //payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                Payment.builder()
                        .paymentUId(null)
                        .customerId(null)
                        .amount(new BigDecimal("100.00"))
                        .currency(currency)
                        .source("card123xxx")
                        .description("donate under test")
                        .build()
        );

        //to test #3, need a mock chargeCard method of cardPaymentCharger in PaymentService.
        //card is charged successfully
        given(this.cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(Boolean.TRUE));

        //When
        underTest.chargeCard(customerId, paymentRequest);

        //Then`
        //capture .save in PaymentService
        final ArgumentCaptor<Payment> paymentRequestArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        then(this.paymentRepository).should().save(paymentRequestArgumentCaptor.capture());

        final Payment paymentArgumentCaptorValue = paymentRequestArgumentCaptor.getValue();
        //assertThat(paymentArgumentCaptorValue).isEqualToComparingFieldByField(paymentRequest.getPayment());
        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void itShouldShouldThrowWhenCardIsNotCharged() {
        //Given
        UUID customerId = UUID.randomUUID();
        // customer exits
        given(this.customerRepository.findById(customerId)).willReturn(
                Optional.of(mock(Customer.class))
        );

        Currency currency = Currency.USD;
        //payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                Payment.builder()
                        .paymentUId(null)
                        .customerId(null)
                        .amount(new BigDecimal("100.00"))
                        .currency(currency)
                        .source("card123xxx")
                        .description("donate under test")
                        .build()
        );

        //to test #3, need a mock chargeCard method of cardPaymentCharger in PaymentService.
        //card is charged successfully
        given(this.cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(Boolean.FALSE));

        //When
        //Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(MessageFormat.format("Card not debited for customer {0}",
                        customerId));
        then(this.paymentRepository).should(never()).save(any(Payment.class));

    }

    @Test
    void itShouldShouldNotChargeCardAndThrowWhenCurrencyNotSupported() {
        //Given
        UUID customerId = UUID.randomUUID();
        // customer exits
        given(this.customerRepository.findById(customerId)).willReturn(
                Optional.of(mock(Customer.class))
        );

        Currency currency = Currency.NONE;
        //payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                Payment.builder()
                        .paymentUId(null)
                        .customerId(null)
                        .amount(new BigDecimal("100.00"))
                        .currency(currency)
                        .source("card123xxx")
                        .description("donate under test")
                        .build()
        );

        //When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(MessageFormat.format("Currency {0} not supported",
                        paymentRequest.getPayment().getCurrency()));


        //Then
        //no interaction with cardPaymentCharger
        then(this.cardPaymentCharger).shouldHaveNoInteractions();

        //no interaction with repository
        then(this.paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldShouldNotChargeAndThrowWhenCustomerNotFound() {
        //Given
        final UUID customerId = UUID.randomUUID();
        given(this.customerRepository.findById(customerId))
                .willReturn(Optional.empty());

        //When customer not found
        //Then
        assertThatThrownBy(() ->
                this.underTest.chargeCard(customerId, new PaymentRequest(new Payment())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(MessageFormat.format(
                                "Customer with id {0} not found",
                                customerId
                        )
                );

        //no interactions  with PaymentCharger nor PaymentRepository
        then(this.paymentRepository).shouldHaveNoInteractions();
        then(this.cardPaymentCharger).shouldHaveNoInteractions();
    }
}
