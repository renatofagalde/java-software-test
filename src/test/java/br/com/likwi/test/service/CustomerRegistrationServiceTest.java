package br.com.likwi.test.service;

import br.com.likwi.test.bean.CustomerRegistrationRequest;
import br.com.likwi.test.dao.CustomerRepository;
import br.com.likwi.test.model.Customer;
import br.com.likwi.test.validator.PhoneNumberValidator;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    //#1
    //old school, I prefer with annotations
    //private CustomerRepository customerRepository = mock(CustomerRepository.class)

    //#2
    //If use @autowired in property and @DataJpaTest in class works, but this away
    //is slow

    //#3
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PhoneNumberValidator phoneNumberValidator;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    private CustomerRegistrationService underTest;
    private Customer customer_1;
    private Customer customer_2;
    private final Faker faker = new Faker();

    //fresh instance to each test
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.underTest = new CustomerRegistrationService(customerRepository, phoneNumberValidator);

        customer_1 = Customer.builder()
                .id(null)
                .name(this.faker.name().fullName())
                .phoneNumber(this.faker.phoneNumber().cellPhone())
                .build();

        System.out.println("customer_1.getPhoneNumber() = " + customer_1.getPhoneNumber());
    }

    @Test
    void itShouldSaveNewCustomer() {
        //Given
        final String phoneNumber = this.customer_1.getPhoneNumber();

        //request
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer_1);

        // no customer with phone number pass
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //When
        underTest.registerNewCustomer(registrationRequest);

        //Then
        //capture the save invoke inside in service method .save()
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        final Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToComparingFieldByField(customer_1);
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        //Given
        customer_1 = Customer.builder()
                .id(null)
                .name(this.faker.name().fullName())
                .phoneNumber(this.faker.phoneNumber().phoneNumber())
                .build();

        //request
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer_1);

        // no customer with phone number pass
        given(customerRepository.selectCustomerByPhoneNumber(
                this.customer_1.getPhoneNumber()))
                .willReturn(Optional.empty());
        given(phoneNumberValidator.test(customer_1.getPhoneNumber())).willReturn(true);

        //When
        underTest.registerNewCustomer(registrationRequest);

        //Then
        //capture the save invoke inside in service method .save()
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        final Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToComparingFieldByField(customer_1);
    }

    @Test
    void itShouldNotSaveCustomerWhenCustumerExists() {
        //Given
        final String phoneNumber = this.customer_1.getPhoneNumber();

        //request
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer_1);

        // an existing customer is returned
        given(customerRepository
                .selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer_1));
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);
        //When
        underTest.registerNewCustomer(registrationRequest);

        //Then
        //#1
        then(this.customerRepository).should(never()).save(any());
//        then(this.customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);
//        then(this.customerRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldThrowWhenPhoneNumberIsTaken() {
        //Given
        final String phoneNumberCustumer_1 = this.customer_1.getPhoneNumber();
        customer_2 = Customer.builder()
                .id(UUID.randomUUID())
                .name(this.faker.name().fullName())
                .phoneNumber(phoneNumberCustumer_1)
                .build();

        //request
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer_1);

        // an existing customer is returned
        given(customerRepository
                .selectCustomerByPhoneNumber(phoneNumberCustumer_1))
                .willReturn(Optional.of(customer_2));
        given(phoneNumberValidator.test(phoneNumberCustumer_1)).willReturn(true);

        //When
        //Then
        assertThatThrownBy(() -> this.underTest.registerNewCustomer(registrationRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        MessageFormat.format("Phone number {0} is taken",
                                customer_1.getPhoneNumber()));
        //finally
        then(this.customerRepository)
                .should(never())
                .save(any(Customer.class));

    }

    @Test
    void itShouldNotSaveNewCustomerWhenPhoneNumberIsInvalid() {
        //Given
        final String phoneNumber = this.customer_1.getPhoneNumber();

        //request
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer_1);

        // no customer with phone number pass
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        given(phoneNumberValidator.test(phoneNumber)).willReturn(false);

        //When
        assertThatThrownBy(() -> underTest.registerNewCustomer(registrationRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Phone number " + phoneNumber + " is not valid");


        //Then
        then(this.customerRepository).shouldHaveNoInteractions();
    }

}
