package com.multipartyloops.evochia.core.identity.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @Mock
    private PasswordEncoder passwordEncoderMock;

    @Mock
    private Random randomMock;

    private PasswordService passwordService;

    @BeforeEach
    void setup() {
        passwordService = new PasswordService(passwordEncoderMock, randomMock);
    }


    @Test
    void generatesARandomPasswordOfADefinedSize() {
        given(randomMock.ints(5, 33, 122)).willReturn(IntStream.of(35, 55, 58, 70, 116));

        String randomPassword = passwordService.generateRandomPassword(5);

        assertThat(randomPassword).isEqualTo("#7:Ft");
    }

    @Test
    void encodesPasswordUsingPasswordEncoder() {
        given(passwordEncoderMock.encode("aPassword")).willReturn("anEncodedPassword");

        String encodedPassword = passwordService.hashPassword("aPassword");

        assertThat(encodedPassword).isEqualTo("anEncodedPassword");
    }

}