package br.com.likwi.test.dao;

import br.com.likwi.test.model.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

// remember: JpaRepository extends PagingAndSortingRepository which in turn extends CrudRepository
public interface CustomerRepository extends CrudRepository<Customer, UUID> {
    @Query(value = "select id,name,phone_number from customer where " +
            "phone_nomber = :phone_number", nativeQuery = true)
    Optional<Customer> selectCustomerByPhoneNumber(@Param("phone_number") String phoneNumber);
}
