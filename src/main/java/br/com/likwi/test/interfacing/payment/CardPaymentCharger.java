package br.com.likwi.test.interfacing.payment;

import br.com.likwi.test.enums.Currency;
import br.com.likwi.test.payment.CardPaymentCharge;

import java.math.BigDecimal;

public interface CardPaymentCharger {

    CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency currency, String description);


}
