package br.com.likwi.test.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Builder
public class Customer {

    @Id
    private UUID id;

    @NotBlank(message = "Name is mandatory! ")
    private String name;

    @NotBlank(message = "Phone number is mandatory! ")
    private String phoneNumber;

}
