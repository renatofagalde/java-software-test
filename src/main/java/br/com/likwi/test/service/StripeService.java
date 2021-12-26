package br.com.likwi.test.service;

import br.com.likwi.test.enums.Currency;
import br.com.likwi.test.interfacing.payment.CardPaymentCharger;
import br.com.likwi.test.payment.CardPaymentCharge;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService implements CardPaymentCharger {


    private StripeAPI stripeAPI;

    @Autowired
    public StripeService(StripeAPI stripeAPI) {
        this.stripeAPI = stripeAPI;
    }

    private final static RequestOptions requestOptions = RequestOptions.builder()
            .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")
            .build();

    @Override
    public CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency currency, String description) {

        // `source` is obtained with Stripe.js; see https://stripe.com/docs/payments/accept-a-payment-charges#web-create-token
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("source", cardSource);
        params.put("description", description);

        try {
/*
            Charge charge = Charge.create(params, this.requestOptions);
            final Boolean chargePaid = charge.getPaid();
            return new CardPaymentCharge(chargePaid);
*/
            final Charge charge = this.stripeAPI.create(params, requestOptions);
            final Boolean chargePaid = charge.getPaid();
            return new CardPaymentCharge(chargePaid);
        } catch (StripeException e) {
            throw new IllegalStateException("Cannot make stripe charge", e);
        }
    }
}
