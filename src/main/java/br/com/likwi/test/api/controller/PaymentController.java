package br.com.likwi.test.api.controller;

import br.com.likwi.test.bean.PaymentRequest;
import br.com.likwi.test.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/payment")
class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RequestMapping("/{customerId}")
    @PostMapping
    void makePayment(@PathVariable("customerId") UUID customerId,
                     @Valid @RequestBody PaymentRequest request) {
        this.paymentService.chargeCard(customerId, request);
    }
}
