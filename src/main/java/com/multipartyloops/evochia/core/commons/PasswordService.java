package com.multipartyloops.evochia.core.commons;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PasswordService {

    public static final int UNICODE_ACCEPTED_CHARACTERS_BEGINNING_VALUE = 33;
    public static final int UNICODE_ACCEPTED_CHARACTERS_END_VALUE = 122;

    private final PasswordEncoder passwordEncoder;
    private final Random random;

    public PasswordService(PasswordEncoder passwordEncoder, Random random) {
        this.passwordEncoder = passwordEncoder;
        this.random = random;
    }

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean passwordsAreTheSame(String unHashed, String hashed) {
        return passwordEncoder.matches(unHashed, hashed);
    }

    public String generateRandomPassword(int size) {
        return random
                .ints(size, UNICODE_ACCEPTED_CHARACTERS_BEGINNING_VALUE, UNICODE_ACCEPTED_CHARACTERS_END_VALUE)
                .mapToObj(i -> Character.toString((char) i))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

}
