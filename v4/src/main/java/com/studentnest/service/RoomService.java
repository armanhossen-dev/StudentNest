package com.studentnest.service;

import com.studentnest.dao.RoomDao;
import com.studentnest.model.Room;
import com.studentnest.model.SearchFilter;
import com.studentnest.model.User;
import com.studentnest.model.UserType;
import com.studentnest.util.ImageStorage;
import com.studentnest.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoomService {
    private final RoomDao roomDao;

    public RoomService() {
        this(new RoomDao());
    }

    public RoomService(RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    public List<Room> findAll() {
        return roomDao.findAll();
    }

    public Optional<Room> findById(long id) {
        return roomDao.findById(id);
    }

    public List<Room> findByOwner(long ownerId) {
        return roomDao.findByOwner(ownerId);
    }

    public List<Room> search(SearchFilter filter) {
        List<Room> rooms = roomDao.findAll();
        return rooms.stream()
                .filter(room -> matchesKeyword(room, filter.getKeyword()))
                .filter(room -> matchesLocation(room, filter.getLocation()))
                .filter(room -> room.getPrice() >= filter.getRentRange().getMin()
                        && room.getPrice() <= filter.getRentRange().getMax())
                .filter(room -> filter.getRoomType() == null || room.getRoomType() == filter.getRoomType())
                .filter(room -> room.getBedrooms() >= filter.getBedrooms().getMin()
                        && room.getBedrooms() <= filter.getBedrooms().getMax())
                .sorted(comparator(filter.getSortOption()))
                .collect(Collectors.toList());
    }

    public Room create(User actor, Room draft) {
        requireCanManage(actor);
        validate(draft);
        if (actor.getUserType() == UserType.OWNER) {
            draft.setOwnerId(actor.getId());
            draft.setOwnerName(actor.getName());
        } else if (draft.getOwnerId() <= 0) {
            draft.setOwnerId(actor.getId());
            draft.setOwnerName(actor.getName());
        }
        draft.setVerified(false);
        if (draft.getCreatedAt() == null) {
            draft.setCreatedAt(LocalDateTime.now());
        }
        roomDao.insert(draft);
        return draft;
    }

    public Room update(User actor, Room room) {
        Room existing = roomDao.findById(room.getId())
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        assertCanModify(actor, existing);
        validate(room);
        room.setOwnerId(existing.getOwnerId());
        room.setOwnerName(existing.getOwnerName());
        room.setVerified(existing.isVerified());
        room.setCreatedAt(existing.getCreatedAt());
        roomDao.update(room);
        return room;
    }

    public void delete(User actor, long roomId) {
        Room existing = roomDao.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        assertCanModify(actor, existing);
        roomDao.delete(roomId);
        ImageStorage.deleteIfOwned(existing.getImage1Path());
        ImageStorage.deleteIfOwned(existing.getImage2Path());
        ImageStorage.deleteIfOwned(existing.getImage3Path());
    }

    public void toggleAvailability(User actor, long roomId) {
        Room existing = roomDao.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        assertCanModify(actor, existing);
        roomDao.setAvailable(roomId, !existing.isAvailable());
    }

    public void setVerified(User actor, long roomId, boolean verified) {
        requireAdmin(actor);
        roomDao.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        roomDao.setVerified(roomId, verified);
    }

    public int countVerified() {
        return roomDao.countVerified();
    }

    public int countAvailable() {
        return roomDao.countAvailable();
    }

    public int countAll() {
        return roomDao.countAll();
    }

    private void validate(Room room) {
        room.setTitle(ValidationUtils.requireText(room.getTitle(), "Title", 4, 180));
        room.setDescription(ValidationUtils.requireText(room.getDescription(), "Description", 10, 2000));
        room.setLocation(ValidationUtils.requireLocation(room.getLocation()));
        if (room.getPrice() <= 0) {
            throw new IllegalArgumentException("Monthly rent must be greater than zero");
        }
        room.setContactNumber(ValidationUtils.requirePhone(room.getContactNumber()));
        room.setRoomType(ValidationUtils.requireRoomType(room.getRoomType()));
        room.setBedrooms(ValidationUtils.requireBedrooms(room.getBedrooms()));
        room.setMapLink(ValidationUtils.optionalHttpUrl(room.getMapLink()));
        if (room.getAmenities() == null) {
            room.setAmenities("");
        }
    }

    private void assertCanModify(User actor, Room room) {
        if (actor == null || actor.isGuest()) {
            throw new AuthorizationException("Sign in to manage listings");
        }
        if (actor.getUserType() == UserType.ADMIN) {
            return;
        }
        if (actor.getUserType() != UserType.OWNER || actor.getId() != room.getOwnerId()) {
            throw new AuthorizationException("You may only manage your own listings");
        }
    }

    private void requireCanManage(User actor) {
        if (actor == null || !actor.getUserType().canManageOwnListings()) {
            throw new AuthorizationException("Only house owners can create listings");
        }
    }

    private void requireAdmin(User actor) {
        if (actor == null || !actor.getUserType().isAdmin()) {
            throw new AuthorizationException("Administrator access is required");
        }
    }

    private boolean matchesKeyword(Room room, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String haystack = (safe(room.getTitle()) + " " + safe(room.getLocation()) + " "
                + safe(room.getDescription()) + " " + safe(room.getRoomType().getDisplayName())
                + " " + safe(room.getAmenities())).toLowerCase(Locale.ROOT);
        return haystack.contains(keyword.toLowerCase(Locale.ROOT));
    }

    private boolean matchesLocation(Room room, String location) {
        if (location == null || location.isBlank() || "All locations".equalsIgnoreCase(location)) {
            return true;
        }
        return room.getLocation() != null && room.getLocation().toLowerCase(Locale.ROOT).contains(location.toLowerCase(Locale.ROOT));
    }

    private Comparator<Room> comparator(SearchFilter.SortOption option) {
        return switch (option) {
            case PRICE_LOW_HIGH -> Comparator.comparingDouble(Room::getPrice);
            case PRICE_HIGH_LOW -> Comparator.comparingDouble(Room::getPrice).reversed();
            case MOST_RECENT -> Comparator.comparing(Room::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
            default -> Comparator.comparing(Room::isVerified).reversed()
                    .thenComparing(Room::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        };
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
