package br.com.likwi.test.dao;

import br.com.likwi.test.model.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
}
