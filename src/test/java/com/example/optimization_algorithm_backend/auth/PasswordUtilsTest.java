package com.example.optimization_algorithm_backend.auth;

import com.example.optimization_algorithm_backend.module.auth.util.PasswordUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PasswordUtilsTest {

    @Test
    void shouldGenerateStableSha256Hash() {
        String passwordHash = PasswordUtils.sha256("123456");

        Assertions.assertEquals("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", passwordHash);
        Assertions.assertTrue(PasswordUtils.matches("123456", passwordHash));
        Assertions.assertFalse(PasswordUtils.matches("654321", passwordHash));
    }
}
