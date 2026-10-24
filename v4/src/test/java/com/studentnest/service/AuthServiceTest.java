package com.studentnest.service;

import com.studentnest.dao.UserDao;
import com.studentnest.db.TestDatabase;
import com.studentnest.model.User;
import com.studentnest.model.UserType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceTest {
    private final AuthService authService = new AuthService();
    private final UserDao userDao = new UserDao();

    @BeforeAll
    static void setup() {
        TestDatabase.ensure();
    }

    @Test
    void demoStudentCanSignIn() {
        User user = authService.login("student", "student123");
        assertEquals(UserType.STUDENT, user.getUserType());
        assertEquals("student", user.getUsername());
    }

    @Test
    void rejectsWrongPassword() {
        assertThrows(IllegalArgumentException.class, () -> authService.login("student", "nope"));
    }

    @Test
    void blocksPublicAdminRegistration() {
        assertThrows(AuthorizationException.class, () ->
                authService.register("Ada Admin", "ada@diu.edu.bd", "01711999999",
                        "adaadmin", "secret12", UserType.ADMIN));
    }

    @Test
    void registersUniqueStudent() {
        String username = "stu" + System.nanoTime();
        User created = authService.register("Test Student", username + "@diu.edu.bd",
                "01711888881", username, "secret12", UserType.STUDENT);
        assertEquals(UserType.STUDENT, created.getUserType());
        assertTrue(userDao.existsByUsername(username));
        assertThrows(IllegalArgumentException.class, () ->
                authService.register("Dup", username + "@diu.edu.bd", "01711888882",
                        username, "secret12", UserType.STUDENT));
    }

    @Test
    void preventsDeletingLastAdministrator() {
        User admin = authService.login("admin", "admin123");
        assertThrows(AuthorizationException.class, () -> authService.deleteUser(admin, admin.getId()));
        User student = userDao.findByUsername("student").orElseThrow();
        assertThrows(AuthorizationException.class, () -> authService.deleteUser(student, admin.getId()));
    }
}
