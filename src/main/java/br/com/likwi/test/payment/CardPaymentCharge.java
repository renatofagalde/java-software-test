package br.com.likwi.test.payment;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CardPaymentCharge {

    private final boolean isCardDebited;

    public CardPaymentCharge(boolean isCardDebited) {
        this.isCardDebited = isCardDebited;
    }
}
