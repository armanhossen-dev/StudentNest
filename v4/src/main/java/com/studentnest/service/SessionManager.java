package com.studentnest.service;

import com.studentnest.model.User;
import com.studentnest.model.UserType;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.Set;

public final class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();

    private final ObjectProperty<User> currentUser = new SimpleObjectProperty<>(User.guest());
    private final ObservableSet<Long> favoriteIds = FXCollections.observableSet();
    private final IntegerProperty favoriteCount = new SimpleIntegerProperty(0);
    private final ObservableSet<Long> guestFavorites = FXCollections.observableSet();

    private SessionManager() {
        favoriteIds.addListener((javafx.collections.SetChangeListener<Long>) change -> favoriteCount.set(favoriteIds.size()));
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public ObjectProperty<User> currentUserProperty() {
        return currentUser;
    }

    public User getCurrentUser() {
        return currentUser.get();
    }

    public UserType getUserType() {
        User user = getCurrentUser();
        return user == null ? UserType.GUEST : user.getUserType();
    }

    public boolean isLoggedIn() {
        return getCurrentUser() != null && !getCurrentUser().isGuest();
    }

    public void login(User user) {
        currentUser.set(user);
    }

    public void logout() {
        currentUser.set(User.guest());
        favoriteIds.clear();
        favoriteIds.addAll(guestFavorites);
    }

    public void setGuest() {
        currentUser.set(User.guest());
    }

    public ObservableSet<Long> getFavoriteIds() {
        return favoriteIds;
    }

    public IntegerProperty favoriteCountProperty() {
        return favoriteCount;
    }

    public int getFavoriteCount() {
        return favoriteCount.get();
    }

    public boolean isFavorite(long roomId) {
        return favoriteIds.contains(roomId);
    }

    public void replaceFavorites(Set<Long> ids) {
        favoriteIds.clear();
        if (ids != null) {
            favoriteIds.addAll(ids);
        }
    }

    public void addFavorite(long roomId) {
        favoriteIds.add(roomId);
        if (!isLoggedIn()) {
            guestFavorites.add(roomId);
        }
    }

    public void removeFavorite(long roomId) {
        favoriteIds.remove(roomId);
        guestFavorites.remove(roomId);
    }

    public Set<Long> takeGuestFavorites() {
        return Set.copyOf(guestFavorites);
    }

    public void clearGuestFavorites() {
        guestFavorites.clear();
    }
}
