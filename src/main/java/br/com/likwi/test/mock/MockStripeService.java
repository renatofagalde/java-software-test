package br.com.likwi.test.mock;

import br.com.likwi.test.enums.Currency;
import br.com.likwi.test.interfacing.payment.CardPaymentCharger;
import br.com.likwi.test.payment.CardPaymentCharge;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@ConditionalOnProperty(value = "stripe.enabled", havingValue = "false")
public class MockStripeService implements CardPaymentCharger {
    @Override
    public CardPaymentCharge chargeCard(String cardSource,
                                        BigDecimal amount,
                                        Currency currency,
                                        String description) {

        final CardPaymentCharge cardPaymentCharge = new CardPaymentCharge(Boolean.TRUE);
        return cardPaymentCharge;
    }
}
