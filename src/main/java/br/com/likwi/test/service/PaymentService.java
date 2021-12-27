package br.com.likwi.test.service;

import br.com.likwi.test.bean.PaymentRequest;
import br.com.likwi.test.dao.CustomerRepository;
import br.com.likwi.test.dao.PaymentRepository;
import br.com.likwi.test.enums.Currency;
import br.com.likwi.test.interfacing.payment.CardPaymentCharger;
import br.com.likwi.test.model.Customer;
import br.com.likwi.test.payment.CardPaymentCharge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Service
public class PaymentService {

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;
    private final List<Currency> ACCEPTED_CURRENCIES = Arrays.asList(Currency.USD,Currency.GPB);
//    private final List<Currency> ACCEPTED_CURRENCIES = Arrays.asList(Currency.values());


    @Autowired
    public PaymentService(CustomerRepository customerRepository,
                          PaymentRepository paymentRepository,
                          CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    public void chargeCard(UUID customerId,
                    PaymentRequest paymentRequest) {
        //1# Does customer exists, if not throw
        final Customer customer = this.customerRepository.findById(customerId).orElseThrow(() -> {
            throw new IllegalStateException(MessageFormat.format(
                    "Customer with id {0} not found",
                    customerId
            ));
        });


        //2# Do we support the currency if not throw
        final Predicate<Currency> currencyPredicate = c -> c.equals(paymentRequest.getPayment().getCurrency());
        final boolean isCurrencySupported = ACCEPTED_CURRENCIES.stream().anyMatch(currencyPredicate);
        if (!isCurrencySupported)
            throw new IllegalStateException(MessageFormat.format("Currency {0} not supported",
                    paymentRequest.getPayment().getCurrency()));

        //3# Charge card
        final CardPaymentCharge cardPaymentCharge = this.cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );

        //4# if not debited throw
        if (!cardPaymentCharge.isCardDebited())
            throw new IllegalStateException(MessageFormat.format("Card not debited for customer {0}",
                    customerId));

        //5# insert payment
        paymentRequest.getPayment().setCustomerId(customerId);
        this.paymentRepository.save(paymentRequest.getPayment());

        //6# TODO: send sms

    }
}
