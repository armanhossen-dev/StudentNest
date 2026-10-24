package com.studentnest.service;

import com.studentnest.dao.RoomDao;
import com.studentnest.dao.UserDao;
import com.studentnest.db.TestDatabase;
import com.studentnest.model.Inquiry;
import com.studentnest.model.InquiryStatus;
import com.studentnest.model.Room;
import com.studentnest.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InquiryFeedbackAuthzTest {
    private final InquiryService inquiryService = new InquiryService();
    private final FeedbackService feedbackService = new FeedbackService();
    private final UserDao userDao = new UserDao();
    private final RoomDao roomDao = new RoomDao();

    @BeforeAll
    static void setup() {
        TestDatabase.ensure();
    }

    @Test
    void guestsCannotInquireOrReview() {
        User guest = User.guest();
        Room room = roomDao.findAll().getFirst();
        assertThrows(AuthorizationException.class, () ->
                inquiryService.submit(guest, room.getId(), "Guest", "01711000001", "Is this available still?"));
        assertThrows(AuthorizationException.class, () ->
                feedbackService.submit(guest, 5, "Great campus housing app overall."));
    }

    @Test
    void studentInquiryIsPersistedAndOwnerCanRespond() {
        User student = userDao.findByUsername("student").orElseThrow();
        User owner = userDao.findByUsername("owner").orElseThrow();
        Room room = roomDao.findByOwner(owner.getId()).getFirst();
        Inquiry inquiry = inquiryService.submit(student, room.getId(), student.getName(),
                student.getPhone(), "I would like to visit this weekend if possible.");
        assertEquals(InquiryStatus.PENDING, inquiry.getStatus());
        inquiryService.updateStatus(owner, inquiry.getId(), InquiryStatus.ACCEPTED);
        assertTrue(inquiryService.listFor(owner).stream()
                .anyMatch(item -> item.getId() == inquiry.getId() && item.getStatus() == InquiryStatus.ACCEPTED));
    }

    @Test
    void inquiryForMissingListingIsRejected() {
        User student = userDao.findByUsername("student").orElseThrow();
        assertThrows(IllegalArgumentException.class, () ->
                inquiryService.submit(student, 9_999_999L, "Maya", "01711000001", "Still available please?"));
    }

    @Test
    void onlyApprovedReviewsAppearPublicly() {
        User student = userDao.findByUsername("nabila").orElseGet(() -> userDao.findByUsername("student").orElseThrow());
        int before = feedbackService.publicFeed().size();
        feedbackService.submit(student, 4, "Filters helped me compare Candgaon and Khagan quickly.");
        assertEquals(before, feedbackService.publicFeed().size());
        User stranger = userDao.findByUsername("student").orElseThrow();
        assertThrows(AuthorizationException.class, () ->
                feedbackService.approve(stranger, 1));
    }
}
