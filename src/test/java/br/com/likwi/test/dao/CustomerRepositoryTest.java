package br.com.likwi.test.dao;

import br.com.likwi.test.model.Customer;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    private final Faker faker = new Faker();
    private Customer customer;

    @BeforeEach
    public void setUnderTest() {
        customer = Customer.builder()
                .name(this.faker.name().fullName())
                .phoneNumber(this.faker.phoneNumber().phoneNumber())
                .build();
    }

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        //custumer is created in setUnderTest

        // When
        this.underTest.save(customer);

        // Then
        Optional<Customer> optionalCustomer = this.underTest.selectCustomerByPhoneNumber(customer.getPhoneNumber());
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldSaveCustomer() {
        //Given
        //custumer is created in setUnderTest

        //When
        this.underTest.save(customer);

        //Then
        final Optional<Customer> optionalCustomer = this.underTest.findById(customer.getId());
        assertThat(optionalCustomer).isPresent()
                .hasValueSatisfying(c -> {
//                    assertThat(c.getId()).isEqualTo(customer.getId());
//                    assertThat(c.getName()).isEqualTo(customer.getName());
//                    assertThat(c.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        // Given
        customer = Customer.builder()
                .phoneNumber(this.faker.phoneNumber().phoneNumber())
                .name(null)
                .build();


        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : br.com.likwi.test.model.Customer.name")
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        // Given
        customer = Customer.builder()
                .id(UUID.randomUUID())
                .phoneNumber(null)
                .name(this.faker.name().fullName())
                .build();

        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : br.com.likwi.test.model.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);

    }

}
