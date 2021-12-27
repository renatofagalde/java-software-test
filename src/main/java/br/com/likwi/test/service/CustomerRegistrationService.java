package br.com.likwi.test.service;

import br.com.likwi.test.bean.CustomerRegistrationRequest;
import br.com.likwi.test.dao.CustomerRepository;
import br.com.likwi.test.model.Customer;
import br.com.likwi.test.validator.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository, PhoneNumberValidator phoneNumberValidator) {
        this.customerRepository = customerRepository;
        this.phoneNumberValidator = phoneNumberValidator;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {

        final String phoneNumber = request.getCustomer().getPhoneNumber();
        if (!this.phoneNumberValidator.test(phoneNumber)) {
            throw new IllegalStateException("Phone number " + phoneNumber + " is not valid");
        }

        //#1 phoneNumber is taken
        final Optional<Customer> optionalCustomer = this.customerRepository
                .selectCustomerByPhoneNumber(request.getCustomer().getPhoneNumber());
        //#2 if taken lets check if belongs to same customer
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            if (customer.getName().equals(request.getCustomer().getName()))
                return; //  #2.1 if yes return
            throw new IllegalStateException(MessageFormat
                    .format("Phone number {0} is taken",
                            request.getCustomer().getPhoneNumber())); //  #2.2 thrown an exception
        }

        //#3 save customer
        this.customerRepository.save(request.getCustomer());
    }


}
