package br.com.likwi.test.service;

import br.com.likwi.test.bean.CustomerRegistrationRequest;
import br.com.likwi.test.dao.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustumerRegistrationService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustumerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        //this.customerRepository.selectCustomerByPhoneNumber();
    }
}
