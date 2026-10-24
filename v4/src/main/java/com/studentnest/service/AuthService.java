package com.studentnest.service;

import com.studentnest.dao.FavoriteDao;
import com.studentnest.dao.UserDao;
import com.studentnest.model.User;
import com.studentnest.model.UserType;
import com.studentnest.util.PasswordHasher;
import com.studentnest.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

public class AuthService {
    private final UserDao userDao;
    private final FavoriteDao favoriteDao;
    private final SessionManager session = SessionManager.getInstance();

    public AuthService() {
        this(new UserDao(), new FavoriteDao());
    }

    public AuthService(UserDao userDao, FavoriteDao favoriteDao) {
        this.userDao = userDao;
        this.favoriteDao = favoriteDao;
    }

    public User login(String username, String password) {
        String normalized = ValidationUtils.requireUsername(username);
        if (ValidationUtils.isBlank(password)) {
            throw new IllegalArgumentException("Password is required");
        }
        User user = userDao.findByUsername(normalized)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        if (!PasswordHasher.verify(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        completeLogin(user);
        return user;
    }

    public User register(String name, String email, String phone, String username, String password, UserType type) {
        if (type == UserType.ADMIN || type == UserType.GUEST) {
            throw new AuthorizationException("Public registration as an administrator is not allowed");
        }
        User user = new User();
        user.setName(ValidationUtils.requireText(name, "Full name", 2, 120));
        user.setEmail(ValidationUtils.requireEmail(email));
        user.setPhone(ValidationUtils.requirePhone(phone));
        user.setUsername(ValidationUtils.requireUsername(username));
        ValidationUtils.requirePassword(password);
        user.setPasswordHash(PasswordHasher.hash(password));
        user.setUserType(type == null ? UserType.STUDENT : type);
        user.setCreatedAt(LocalDateTime.now());
        if (userDao.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("That username is already taken");
        }
        if (userDao.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("That email is already registered");
        }
        userDao.insert(user);
        completeLogin(user);
        return user;
    }

    public void logout() {
        session.logout();
    }

    public void continueAsGuest() {
        session.setGuest();
        session.replaceFavorites(session.takeGuestFavorites());
    }

    public void deleteUser(User actor, long userId) {
        requireAdmin(actor);
        User target = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (target.getUserType() == UserType.ADMIN && userDao.countAdmins() <= 1) {
            throw new AuthorizationException("The last administrator cannot be deleted");
        }
        if (actor.getId() == userId) {
            throw new AuthorizationException("You cannot delete your own account from the admin panel");
        }
        userDao.delete(userId);
    }

    public java.util.List<User> listUsers(User actor) {
        requireAdmin(actor);
        return userDao.findAll();
    }

    private void completeLogin(User user) {
        Set<Long> guestIds = session.takeGuestFavorites();
        session.login(user);
        Set<Long> persisted = favoriteDao.findRoomIdsByUser(user.getId());
        for (Long roomId : guestIds) {
            if (!persisted.contains(roomId)) {
                favoriteDao.add(user.getId(), roomId);
                persisted.add(roomId);
            }
        }
        session.replaceFavorites(persisted);
        session.clearGuestFavorites();
    }

    private void requireAdmin(User actor) {
        if (actor == null || actor.getUserType() != UserType.ADMIN) {
            throw new AuthorizationException("Administrator access is required");
        }
    }

    public static UserType parseRegistrationType(String raw) {
        if (raw == null) {
            return UserType.STUDENT;
        }
        String value = raw.trim().toUpperCase(Locale.ROOT);
        if (value.contains("OWNER")) {
            return UserType.OWNER;
        }
        return UserType.STUDENT;
    }
}
