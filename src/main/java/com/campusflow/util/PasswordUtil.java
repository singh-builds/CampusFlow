package com.campusflow.util;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Password handling: salted PBKDF2 hash. Plain passwords are never stored. Format: iterations:salt:hash */
public final class PasswordUtil {
    private static final int ITERATIONS = 120_000;
    private static final int KEY_BITS = 256;

    private PasswordUtil() {}

    public static String hash(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] h = pbkdf2(password.toCharArray(), salt, ITERATIONS);
        return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(h);
    }

    public static boolean verify(String password, String stored) {
        try {
            String[] p = stored.split(":");
            byte[] salt = Base64.getDecoder().decode(p[1]);
            byte[] expected = Base64.getDecoder().decode(p[2]);
            byte[] actual = pbkdf2(password.toCharArray(), salt, Integer.parseInt(p[0]));
            int diff = expected.length ^ actual.length;
            for (int i = 0; i < expected.length && i < actual.length; i++) diff |= expected[i] ^ actual[i];
            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] pw, byte[] salt, int iterations) {
        try {
            KeySpec spec = new PBEKeySpec(pw, salt, iterations, KEY_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
