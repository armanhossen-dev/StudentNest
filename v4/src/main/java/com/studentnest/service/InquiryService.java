package com.studentnest.service;

import com.studentnest.dao.InquiryDao;
import com.studentnest.dao.RoomDao;
import com.studentnest.model.Inquiry;
import com.studentnest.model.InquiryStatus;
import com.studentnest.model.Room;
import com.studentnest.model.User;
import com.studentnest.model.UserType;
import com.studentnest.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.List;

public class InquiryService {
    private final InquiryDao inquiryDao;
    private final RoomDao roomDao;

    public InquiryService() {
        this(new InquiryDao(), new RoomDao());
    }

    public InquiryService(InquiryDao inquiryDao, RoomDao roomDao) {
        this.inquiryDao = inquiryDao;
        this.roomDao = roomDao;
    }

    public Inquiry submit(User actor, long roomId, String name, String contact, String message) {
        if (actor == null || actor.isGuest() || !actor.getUserType().canSubmitInquiry()) {
            throw new AuthorizationException("Sign in as a student to send an inquiry");
        }
        Room room = roomDao.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("That listing no longer exists"));
        Inquiry inquiry = new Inquiry();
        inquiry.setRoomId(room.getId());
        inquiry.setRoomTitle(room.getTitle());
        inquiry.setStudentName(ValidationUtils.requireText(name, "Name", 2, 120));
        inquiry.setStudentContact(ValidationUtils.requirePhone(contact));
        inquiry.setMessage(ValidationUtils.requireText(message, "Message", 8, 1000));
        inquiry.setStatus(InquiryStatus.PENDING);
        inquiry.setCreatedAt(LocalDateTime.now());
        inquiry.setStudentId(actor.getId());
        inquiryDao.insert(inquiry);
        return inquiry;
    }

    public List<Inquiry> listFor(User actor) {
        if (actor == null || actor.isGuest()) {
            throw new AuthorizationException("Sign in to view inquiries");
        }
        if (actor.getUserType() == UserType.ADMIN) {
            return inquiryDao.findAll();
        }
        if (actor.getUserType() == UserType.OWNER) {
            return inquiryDao.findByOwner(actor.getId());
        }
        throw new AuthorizationException("Only owners and administrators can manage inquiries");
    }

    public void updateStatus(User actor, long inquiryId, InquiryStatus status) {
        Inquiry inquiry = inquiryDao.findById(inquiryId);
        if (inquiry == null) {
            throw new IllegalArgumentException("Inquiry not found");
        }
        Room room = roomDao.findById(inquiry.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        if (actor.getUserType() != UserType.ADMIN && actor.getId() != room.getOwnerId()) {
            throw new AuthorizationException("You may only respond to inquiries for your own listings");
        }
        inquiryDao.updateStatus(inquiryId, status);
    }
}
