package com.example.optimization_algorithm_backend.module.auth.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// 作用: 该类是一个密码工具类，提供了对密码进行SHA-256哈希加密和验证的方法。通过使用SHA-256算法，可以将原始密码转换为一个固定长度的哈希值，增强密码的安全性。
// matches方法用于验证用户输入的密码是否与存储的密码哈希值匹配，确保用户提供的密码正确。
// toHex方法用于将字节数组转换为十六进制字符串，方便存储和比较哈希值。这个工具类可以在认证模块中广泛使用，提高系统的安全性和代码的可维护性。
public final class PasswordUtils {

    private PasswordUtils() {
    }
    public static String sha256(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return toHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }

    public static boolean matches(String rawPassword, String passwordHash) {
        return sha256(rawPassword).equalsIgnoreCase(passwordHash);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }
}
