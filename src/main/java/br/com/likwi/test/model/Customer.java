package br.com.likwi.test.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Remember: to use @NotBlank in test, you need this set:
 * @DataJpaTest(
 *         properties = {
 *                 "spring.jpa.properties.javax.persistence.validation.mode=none"
 *         }
 * )
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Builder
public class Customer {

    final static Logger logger = Logger.getLogger(Customer.class.toString());

    @Id
    @NotBlank
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @PrePersist
    public void whenSave() {
        if (this.id == null) this.id = UUID.randomUUID();

        logger.info(MessageFormat.format("Object Customer new ID {0}",
                this.id));
    }

    @PostLoad
    public void afterLoad() {
        logger.info(MessageFormat.format("Object Customer load {0}",
                this.id));
    }


}
