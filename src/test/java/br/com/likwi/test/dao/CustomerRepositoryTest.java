package br.com.likwi.test.dao;

import br.com.likwi.test.model.Customer;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    private final Faker faker = new Faker();

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        //Given
        //When
        //Then
    }

    @Test
    void itShouldSaveCustomer() {
        //Given
        final Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name(this.faker.name().fullName())
                .phoneNumber(this.faker.phoneNumber().phoneNumber())
                .build();

        //When
        this.underTest.save(customer);

        //Then
        final Optional<Customer> optionalCustomer = this.underTest.findById(customer.getId());
        assertThat(optionalCustomer).isPresent()
                .hasValueSatisfying(c->{
//                    assertThat(c.getId()).isEqualTo(customer.getId());
//                    assertThat(c.getName()).isEqualTo(customer.getName());
//                    assertThat(c.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }
}
