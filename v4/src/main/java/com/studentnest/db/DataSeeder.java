package com.studentnest.db;

import com.studentnest.dao.FeedbackDao;
import com.studentnest.dao.InquiryDao;
import com.studentnest.dao.RoomDao;
import com.studentnest.dao.UserDao;
import com.studentnest.model.Feedback;
import com.studentnest.model.FeedbackStatus;
import com.studentnest.model.Inquiry;
import com.studentnest.model.InquiryStatus;
import com.studentnest.model.Room;
import com.studentnest.model.RoomType;
import com.studentnest.model.User;
import com.studentnest.model.UserType;
import com.studentnest.util.PasswordHasher;

import java.time.LocalDateTime;

public final class DataSeeder {
    private DataSeeder() {
    }

    public static void seed() {
        UserDao userDao = new UserDao();
        RoomDao roomDao = new RoomDao();
        FeedbackDao feedbackDao = new FeedbackDao();
        InquiryDao inquiryDao = new InquiryDao();

        User student = ensureUser(userDao, "student", "Maya Rahman", "maya.student@diu.edu.bd",
                "01711000001", "student123", UserType.STUDENT);
        User owner = ensureUser(userDao, "owner", "Karim Hossain", "karim.owner@studentnest.local",
                "01711000002", "owner123", UserType.OWNER);
        ensureUser(userDao, "admin", "StudentNest Admin", "admin@studentnest.local",
                "01711000003", "admin123", UserType.ADMIN);
        User extraStudent = ensureUser(userDao, "nabila", "Nabila Chowdhury", "nabila@diu.edu.bd",
                "01711000004", "student123", UserType.STUDENT);

        if (roomDao.countAll() == 0) {
            seedRooms(roomDao, owner);
        }
        if (feedbackDao.findAll().isEmpty()) {
            seedFeedback(feedbackDao, student, extraStudent, owner);
        }
        if (inquiryDao.findAll().isEmpty()) {
            roomDao.findAll().stream().findFirst().ifPresent(room -> {
                Inquiry inquiry = new Inquiry();
                inquiry.setRoomId(room.getId());
                inquiry.setRoomTitle(room.getTitle());
                inquiry.setStudentName(student.getName());
                inquiry.setStudentContact(student.getPhone());
                inquiry.setMessage("I am a DIU student looking for a quiet room near campus. Is this still available?");
                inquiry.setStatus(InquiryStatus.PENDING);
                inquiry.setStudentId(student.getId());
                inquiry.setCreatedAt(LocalDateTime.now().minusDays(1));
                inquiryDao.insert(inquiry);
            });
        }
    }

    private static User ensureUser(UserDao userDao, String username, String name, String email,
                                   String phone, String password, UserType type) {
        return userDao.findByUsername(username).orElseGet(() -> {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setUsername(username);
            user.setPasswordHash(PasswordHasher.hash(password));
            user.setUserType(type);
            user.setCreatedAt(LocalDateTime.now().minusDays(14));
            userDao.insert(user);
            return user;
        });
    }

    private static void seedRooms(RoomDao roomDao, User owner) {
        insert(roomDao, owner, "Sunshine Student Hostel",
                "A bright shared hostel close to DIU Khagan campus. Demo listing for development only.",
                "Khagan", 4500, RoomType.SHARED_SEAT, 1,
                "WiFi, Furnished, Kitchen, Attached Bathroom", true, false,
                "https://www.google.com/maps/search/?api=1&query=Khagan+Daffodil+International+University",
                "/images/room-khagan.png", LocalDateTime.now().minusDays(12));
        insert(roomDao, owner, "Green Valley Master Bedroom",
                "Furnished master bedroom in Candgaon with a study desk and balcony. Demo listing.",
                "Candgaon", 8000, RoomType.MASTER_BED, 1,
                "WiFi, Furnished, Balcony, Parking", true, true,
                "https://www.google.com/maps/search/?api=1&query=Candgaon+Ashulia",
                "/images/room-candgaon.png", LocalDateTime.now().minusDays(10));
        insert(roomDao, owner, "DIU Smart Studio Apartment",
                "Compact studio near Khagan Gate 1 with kitchenette and reliable generator backup. Demo listing.",
                "Khagan Gate 1", 10500, RoomType.STUDIO, 1,
                "WiFi, Kitchen, Air Conditioning, Generator", true, true,
                "https://www.google.com/maps/search/?api=1&query=Khagan+Gate+1+DIU",
                "/images/room-studio.png", LocalDateTime.now().minusDays(8));
        insert(roomDao, owner, "Kumkumari Shared Student Flat",
                "Budget shared seat in Kumkumari with common kitchen. Demo listing, not independently inspected.",
                "Kumkumari", 3200, RoomType.SHARED_SEAT, 1,
                "WiFi, Kitchen, Furnished", true, false,
                null, "/images/room-kumkumari.png", LocalDateTime.now().minusDays(7));
        insert(roomDao, owner, "Charabag Quiet Study Flat",
                "Full flat in Charabag suited to senior students who need a quieter study space. Demo listing.",
                "Charabag", 14000, RoomType.FULL_FLAT, 3,
                "WiFi, Furnished, Kitchen, Air Conditioning, Parking, Generator", true, true,
                "https://www.google.com/maps/search/?api=1&query=Charabag+Ashulia",
                "/images/room-charabag.png", LocalDateTime.now().minusDays(6));
        insert(roomDao, owner, "Dattopara Budget Mess Room",
                "Affordable mess-style single room in Dattopara. Demo listing for filter testing.",
                "Dattopara", 2800, RoomType.SINGLE_ROOM, 1,
                "Kitchen, Attached Bathroom", true, false,
                null, "/images/room-dattopara.png", LocalDateTime.now().minusDays(5));
        insert(roomDao, owner, "Candgaon Lakeside Bachelor Residence",
                "Two-bedroom residence in Candgaon with parking. Demo listing.",
                "Candgaon", 6500, RoomType.FULL_FLAT, 2,
                "WiFi, Furnished, Kitchen, Parking, Balcony", true, false,
                "https://www.google.com/maps/search/?api=1&query=Candgaon+Lakeside",
                "/images/room-lakeside.png", LocalDateTime.now().minusDays(3));
        insert(roomDao, owner, "Khagan Central Deluxe Flat",
                "Premium three-bedroom flat near Khagan with generator and AC. Demo listing.",
                "Khagan", 16000, RoomType.FULL_FLAT, 3,
                "WiFi, Furnished, Kitchen, Air Conditioning, Attached Bathroom, Balcony, Parking, Generator",
                false, true,
                "https://www.google.com/maps/search/?api=1&query=Khagan+Ashulia",
                "/images/room-deluxe.png", LocalDateTime.now().minusDays(2));
    }

    private static void insert(RoomDao roomDao, User owner, String title, String description, String location,
                               double price, RoomType type, int bedrooms, String amenities, boolean available,
                               boolean verified, String mapLink, String image, LocalDateTime createdAt) {
        Room room = new Room();
        room.setTitle(title);
        room.setDescription(description);
        room.setLocation(location);
        room.setPrice(price);
        room.setContactNumber(owner.getPhone());
        room.setOwnerId(owner.getId());
        room.setOwnerName(owner.getName());
        room.setAvailable(available);
        room.setVerified(verified);
        room.setRoomType(type);
        room.setBedrooms(bedrooms);
        room.setAmenities(amenities);
        room.setMapLink(mapLink);
        room.setImage1Path(image);
        room.setCreatedAt(createdAt);
        roomDao.insert(room);
    }

    private static void seedFeedback(FeedbackDao dao, User student, User extra, User owner) {
        insertFeedback(dao, student, 5,
                "StudentNest made it easy to compare Khagan hostels before moving in. Demo review.",
                FeedbackStatus.APPROVED, LocalDateTime.now().minusDays(9));
        insertFeedback(dao, extra, 4,
                "Filters for rent and location are accurate. Waiting for more verified listings in Shadhupara.",
                FeedbackStatus.APPROVED, LocalDateTime.now().minusDays(4));
        insertFeedback(dao, owner, 5,
                "Listing my extra room took a few minutes and inquiries arrived the same day.",
                FeedbackStatus.PENDING, LocalDateTime.now().minusDays(1));
    }

    private static void insertFeedback(FeedbackDao dao, User user, int rating, String comment,
                                       FeedbackStatus status, LocalDateTime createdAt) {
        Feedback feedback = new Feedback();
        feedback.setUserId(user.getId());
        feedback.setUserName(user.getName());
        feedback.setUserRole(user.getUserType().displayName());
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedback.setStatus(status);
        feedback.setCreatedAt(createdAt);
        dao.insert(feedback);
    }
}
