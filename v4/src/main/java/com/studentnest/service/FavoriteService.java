package com.studentnest.service;

import com.studentnest.dao.FavoriteDao;
import com.studentnest.dao.RoomDao;
import com.studentnest.model.Room;
import com.studentnest.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FavoriteService {
    private final FavoriteDao favoriteDao;
    private final RoomDao roomDao;
    private final SessionManager session = SessionManager.getInstance();

    public FavoriteService() {
        this(new FavoriteDao(), new RoomDao());
    }

    public FavoriteService(FavoriteDao favoriteDao, RoomDao roomDao) {
        this.favoriteDao = favoriteDao;
        this.roomDao = roomDao;
    }

    public boolean toggle(long roomId) {
        User user = session.getCurrentUser();
        boolean currently = session.isFavorite(roomId);
        if (currently) {
            session.removeFavorite(roomId);
            if (user != null && !user.isGuest()) {
                favoriteDao.remove(user.getId(), roomId);
            }
            return false;
        }
        session.addFavorite(roomId);
        if (user != null && user.getUserType().canPersistFavorites()) {
            favoriteDao.add(user.getId(), roomId);
        }
        return true;
    }

    public List<Room> listFavoriteRooms() {
        Set<Long> ids = session.getFavoriteIds();
        return roomDao.findAll().stream()
                .filter(room -> ids.contains(room.getId()))
                .collect(Collectors.toList());
    }

    public void persistIfLoggedIn(long roomId) {
        User user = session.getCurrentUser();
        if (user != null && user.getUserType().canPersistFavorites()) {
            favoriteDao.add(user.getId(), roomId);
        }
    }
}
