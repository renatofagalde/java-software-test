package br.com.likwi.test.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * We need this class to keep all test without an external framework like: https://www.baeldung.com/intro-to-powermock
 */
@Service
public class StripeAPI {
    public Charge create(Map<String, Object> params, RequestOptions options) throws StripeException {
        return Charge.create(params, options);
    }
}
