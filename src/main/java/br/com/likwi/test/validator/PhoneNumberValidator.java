package br.com.likwi.test.validator;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PhoneNumberValidator implements Predicate<String> {

    //https://www.baeldung.com/java-regex-validate-phone-numbers

    @Override
    public boolean test(String phoneNumber) {
        Pattern pattern = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
