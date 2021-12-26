package br.com.likwi.test.service;

import br.com.likwi.test.enums.Currency;
import br.com.likwi.test.payment.CardPaymentCharge;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class StripeServiceTest {

    private StripeService underTest;

    @Mock
    private StripeAPI stripeAPI;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.underTest = new StripeService(stripeAPI);
    }

    @Test
    void itShouldChargeCard() throws StripeException {
        //Given
        final String cardSource = "0x0x0x0";
        final BigDecimal amount = BigDecimal.TEN;
        final Currency currency = Currency.USD;
        final String description = "test";
        final Charge charge = new Charge();
        charge.setPaid(Boolean.TRUE);
        given(stripeAPI.create(anyMap(), any())).willReturn(charge);

        //When
        final CardPaymentCharge cardPaymentCharge = this.underTest.chargeCard(cardSource, amount, currency, description);

        //Then
        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<RequestOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);

        then(this.stripeAPI).should().create(mapArgumentCaptor.capture(),
                optionsArgumentCaptor.capture());

        final Map<String, Object> requestMap = mapArgumentCaptor.getValue();
        assertThat(requestMap.keySet()).hasSize(4);
        assertThat(requestMap.get("amount")).isEqualTo(amount);
        assertThat(requestMap.get("currency")).isEqualTo(currency);
        assertThat(requestMap.get("source")).isEqualTo(cardSource);
        assertThat(requestMap.get("description")).isEqualTo(description);

        final RequestOptions optionsArgumentCaptorValue = optionsArgumentCaptor.getValue();
        assertThat(optionsArgumentCaptorValue).isNotNull();

        assertThat(cardPaymentCharge).isNotNull();
        assertThat(cardPaymentCharge.isCardDebited()).isTrue();
    }
}
