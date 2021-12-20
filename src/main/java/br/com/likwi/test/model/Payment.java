package br.com.likwi.test.model;

import br.com.likwi.test.enums.Currency;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Builder
@EqualsAndHashCode
public class Payment {
    @Id
    @GeneratedValue
    private Long paymentUId;
    private UUID customerId;
    private BigDecimal amount;
    private Currency currency;
    private String source;
    private String description;
}
