package com.studentnest.service;

import com.studentnest.dao.UserDao;
import com.studentnest.db.TestDatabase;
import com.studentnest.model.Room;
import com.studentnest.model.RoomType;
import com.studentnest.model.SearchFilter;
import com.studentnest.model.User;
import com.studentnest.model.UserType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomServiceTest {
    private final RoomService roomService = new RoomService();
    private final UserDao userDao = new UserDao();

    @BeforeAll
    static void setup() {
        TestDatabase.ensure();
    }

    @Test
    void searchCombinesKeywordLocationRentTypeAndSort() {
        SearchFilter filter = new SearchFilter();
        filter.setKeyword("hostel");
        filter.setLocation("Khagan");
        filter.setRentRange(SearchFilter.RentRange.UNDER_5000);
        filter.setRoomType(RoomType.SHARED_SEAT);
        filter.setSortOption(SearchFilter.SortOption.PRICE_LOW_HIGH);
        List<Room> rooms = roomService.search(filter);
        assertFalse(rooms.isEmpty());
        assertTrue(rooms.stream().allMatch(room ->
                room.getTitle().toLowerCase().contains("hostel")
                        && room.getLocation().toLowerCase().contains("khagan")
                        && room.getPrice() < 5000
                        && room.getRoomType() == RoomType.SHARED_SEAT));
    }

    @Test
    void priceSortsLowToHigh() {
        SearchFilter filter = new SearchFilter();
        filter.setSortOption(SearchFilter.SortOption.PRICE_LOW_HIGH);
        List<Room> rooms = roomService.search(filter);
        for (int i = 1; i < rooms.size(); i++) {
            assertTrue(rooms.get(i - 1).getPrice() <= rooms.get(i).getPrice());
        }
    }

    @Test
    void studentsCannotCreateListings() {
        User student = userDao.findByUsername("student").orElseThrow();
        Room draft = sample(student);
        assertThrows(AuthorizationException.class, () -> roomService.create(student, draft));
    }

    @Test
    void ownersCannotModifySomeoneElsessListing() {
        User owner = userDao.findByUsername("owner").orElseThrow();
        Room existing = roomService.findByOwner(owner.getId()).getFirst();
        User stranger = new User();
        stranger.setId(owner.getId() + 9999);
        stranger.setUserType(UserType.OWNER);
        stranger.setName("Other Owner");
        assertThrows(AuthorizationException.class, () -> roomService.delete(stranger, existing.getId()));
        assertThrows(AuthorizationException.class, () -> roomService.setVerified(owner, existing.getId(), true));
    }

    @Test
    void newOwnerListingStartsUnverified() {
        User owner = userDao.findByUsername("owner").orElseThrow();
        Room draft = sample(owner);
        draft.setTitle("Temporary Test Flat " + System.nanoTime());
        Room created = roomService.create(owner, draft);
        assertFalse(created.isVerified());
        assertTrue(created.getId() > 0);
        roomService.delete(owner, created.getId());
    }

    private Room sample(User owner) {
        Room room = new Room();
        room.setTitle("Quiet Khagan Study Room");
        room.setDescription("A compact room near campus used by automated tests.");
        room.setLocation("Khagan");
        room.setPrice(4100);
        room.setContactNumber(owner.getPhone() == null ? "01711000002" : owner.getPhone());
        room.setRoomType(RoomType.SINGLE_ROOM);
        room.setBedrooms(1);
        room.setAmenities("WiFi");
        return room;
    }
}
