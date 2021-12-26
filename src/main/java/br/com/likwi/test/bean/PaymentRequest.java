package br.com.likwi.test.bean;

import br.com.likwi.test.model.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PaymentRequest {

    private final Payment payment;

    public PaymentRequest(@JsonProperty("payment") Payment payment) {
        this.payment = payment;
    }
}
