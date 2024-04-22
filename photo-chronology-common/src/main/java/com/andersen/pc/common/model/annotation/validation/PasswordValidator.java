package com.andersen.pc.common.model.annotation.validation;

import com.andersen.pc.common.model.annotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PasswordValidator implements ConstraintValidator<ValidPassword, char[]> {

    @Override
    public boolean isValid(char[] password, ConstraintValidatorContext constraintValidatorContext) {
        if (ArrayUtils.isEmpty(password)) {
            return true;
        }
        return isValid(password);
    }

    private static final String VALIDATION_REGEXP =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[,!@#?\\]])(?=\\S+$)[0-9A-Za-z,!@#?\\]]{14,64}$";
    private static final String DUPLICATES_REGEXP = ".*(.)\\1{3}.*";
    private static final Integer NUMBER_OF_CHARS = 4;
    private static final String NUMBERS = "1234567890";
    private static final String KEYBOARD = "qwertyuiopasdfghjklzxcvbnm";
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final Set<String> FORBIDDEN_WORDS = Set.of("qwerty", "welcome", "password",
            "1q2w3e4r", "zaq12wsx");

    public static boolean isValid(char[] password) {
        return isValidSymbols(password) && isStrong(password);
    }

    private static boolean isValidSymbols(char[] password) {
        return matchPasswordWithRegexp(password, VALIDATION_REGEXP);
    }

    private static boolean matchPasswordWithRegexp(char[] password, String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        StringBuilder builder = new StringBuilder().append(password);
        Matcher matcher = pattern.matcher(builder);
        return matcher.matches();
    }

    private static boolean isStrong(char[] password) {
        char[] checkedPassword = Arrays.copyOf(password, password.length);
        toLowerCase(checkedPassword);
        return !isPasswordContainsRangeOfDuplicates(checkedPassword) && !isPasswordContainsForbiddenWords(checkedPassword)
                && !isPasswordSequential(checkedPassword);
    }

    private static void toLowerCase(char[] password) {
        for (int i = 0; i < password.length; i++) {
            password[i] = Character.toLowerCase(password[i]);
        }
    }

    private static boolean isPasswordContainsRangeOfDuplicates(char[] password) {
        return matchPasswordWithRegexp(password, DUPLICATES_REGEXP);
    }

    private static boolean isPasswordContainsForbiddenWords(char[] password) {
        Set<char[]> forbiddenWords = FORBIDDEN_WORDS.stream()
                .map(String::toCharArray)
                .collect(Collectors.toSet());
        return forbiddenWords.stream().anyMatch(forbiddenWord -> containsCharArray(password, forbiddenWord));
    }

    private static boolean containsCharArray(char[] password, char[] set) {
        if (password.length < set.length) {
            return false;
        }
        for (int i = 0; i <= password.length - set.length; i++) {
            boolean found = true;
            for (int j = 0; j < set.length; j++) {
                if (password[i + j] != set[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPasswordSequential(char[] password) {
        boolean result = false;
        char[] numbers = NUMBERS.toCharArray();
        char[] keyboard = KEYBOARD.toCharArray();
        char[] alphabet = ALPHABET.toCharArray();
        for (int i = 0; i <= password.length - NUMBER_OF_CHARS; i++) {
            char[] iterationChars = Arrays.copyOfRange(password, i, i + NUMBER_OF_CHARS);
            if (contains(numbers, iterationChars) || contains(keyboard, iterationChars) || contains(alphabet, iterationChars)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static boolean contains(char[] sequence, char[] symbols) {
        return IntStream.range(0, sequence.length - symbols.length + 1)
                .anyMatch(i -> IntStream.range(0, symbols.length)
                        .allMatch(j -> sequence[i + j] == symbols[j]));
    }
}
