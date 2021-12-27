package br.com.likwi.test.utils;

import br.com.likwi.test.validator.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @CsvSource({"+5511975995897,true","1-525-953-a579,false","680.436.5127,true","1-645-963-9823,false"})
    void itShouldValidatePhoneNumber(String phoneNumber, boolean expected) {
        //Given
//        String phoneNumber = "+4470";

        //When
        boolean isValid = this.underTest.test(phoneNumber);

        //Then
        assertThat(isValid).isEqualTo(expected);
    }

    @Test
    @DisplayName("Bigger than 5")
    void itShouldValidatePhoneNumberWhenIncorrectAndHasLenghtBiggerThan5() {
        //Given
        String phoneNumber = "+44706";

        //When
        boolean isValid = this.underTest.test(phoneNumber);

        //Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should fail when does not start with +")
    void itShouldValidatePhoneNumberWhenIncorrectNotStartWithStar() {
        //Given
        String phoneNumber = "44706";

        //When
        boolean isValid = this.underTest.test(phoneNumber);

        //Then
        assertThat(isValid).isFalse();
    }
}
