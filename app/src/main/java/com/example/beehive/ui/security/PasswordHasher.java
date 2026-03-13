// Файл: PasswordHasher.java
package com.example.beehive.ui.security;

import android.util.Base64;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher {

    private static final int ITERATION_COUNT = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    public static String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            String saltBase64 = Base64.encodeToString(salt, Base64.DEFAULT);
            String hashBase64 = Base64.encodeToString(hash, Base64.DEFAULT);

            return saltBase64 + ":" + hashBase64;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Ошибка хеширования пароля", e);
        }
    }

    public static boolean checkPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }
            byte[] salt = Base64.decode(parts[0], Base64.DEFAULT);
            byte[] expectedHash = Base64.decode(parts[1], Base64.DEFAULT);

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] actualHash = factory.generateSecret(spec).getEncoded();

            return java.util.Arrays.equals(expectedHash, actualHash);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return false;
        }
    }
}