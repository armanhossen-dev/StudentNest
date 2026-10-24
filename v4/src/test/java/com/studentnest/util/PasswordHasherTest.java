package com.studentnest.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {
    @Test
    void hashesAreSaltedAndVerifiable() {
        String hash1 = PasswordHasher.hash("student123");
        String hash2 = PasswordHasher.hash("student123");
        assertNotEquals(hash1, hash2);
        assertTrue(PasswordHasher.verify("student123", hash1));
        assertTrue(PasswordHasher.verify("student123", hash2));
        assertFalse(PasswordHasher.verify("wrong", hash1));
        assertFalse(PasswordHasher.verify("student123", "not-a-hash"));
        assertFalse(PasswordHasher.verify(null, hash1));
    }

    @Test
    void rejectsBlankPasswords() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hash(""));
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hash(null));
    }
}
