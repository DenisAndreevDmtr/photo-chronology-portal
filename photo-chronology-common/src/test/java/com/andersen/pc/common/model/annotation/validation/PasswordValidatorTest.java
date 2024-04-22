package com.andersen.pc.common.model.annotation.validation;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {
    @Nested
    class isValid_Should {

        @ParameterizedTest
        @MethodSource("passwordProvider")
        void testPasswordValidity(String password, boolean expectedValidity) {
            char[] passwordChars = password.toCharArray();

            assertEquals(expectedValidity, PasswordValidator.isValid(passwordChars));
        }

        private static Stream<Arguments> passwordProvider() {
            return Stream.of(
                    Arguments.of("Phbklmg @15dgf", false),
                    Arguments.of("Phbklmg&15dgf", false),
                    Arguments.of("Phbklmg15dgf", false),
                    Arguments.of("Phbklmg@dgf", false),
                    Arguments.of("PHBKLMG@15DGF", false),
                    Arguments.of("phbklmg@15dgf", false),
                    Arguments.of("Phbklmg@1511dgfPhbklmg@1511dgfPhbklmg@1511dgfPhbklmg@1511dgfPhbklmg@1511dgf", false),
                    Arguments.of("Phbklmg@15dgf", false),
                    Arguments.of("Password@123567", false),
                    Arguments.of("221111PassGet@12786", false),
                    Arguments.of("Fghj123@pass", false),
                    Arguments.of("Bcde123@pass", false),
                    Arguments.of("Pass1234@pass", false),
                    Arguments.of("StrongPass@1239870", true)
            );
        }
    }
}