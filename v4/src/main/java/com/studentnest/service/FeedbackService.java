package com.studentnest.service;

import com.studentnest.dao.FeedbackDao;
import com.studentnest.model.Feedback;
import com.studentnest.model.FeedbackStatus;
import com.studentnest.model.User;
import com.studentnest.model.UserType;
import com.studentnest.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.List;

public class FeedbackService {
    private final FeedbackDao feedbackDao;

    public FeedbackService() {
        this(new FeedbackDao());
    }

    public FeedbackService(FeedbackDao feedbackDao) {
        this.feedbackDao = feedbackDao;
    }

    public Feedback submit(User actor, int rating, String comment) {
        if (actor == null || actor.isGuest() || !actor.getUserType().canSubmitReview()) {
            throw new AuthorizationException("Sign in to submit a review");
        }
        Feedback feedback = new Feedback();
        feedback.setUserId(actor.getId());
        feedback.setUserName(actor.getName());
        feedback.setUserRole(actor.getUserType().displayName());
        feedback.setRating(ValidationUtils.requireRating(rating));
        feedback.setComment(ValidationUtils.requireText(comment, "Comment", 8, 800));
        feedback.setStatus(FeedbackStatus.PENDING);
        feedback.setCreatedAt(LocalDateTime.now());
        feedbackDao.insert(feedback);
        return feedback;
    }

    public List<Feedback> publicFeed() {
        return feedbackDao.findApproved();
    }

    public List<Feedback> moderateList(User actor) {
        requireAdmin(actor);
        return feedbackDao.findAll();
    }

    public void approve(User actor, long id) {
        requireAdmin(actor);
        feedbackDao.updateStatus(id, FeedbackStatus.APPROVED);
    }

    public void reject(User actor, long id) {
        requireAdmin(actor);
        feedbackDao.updateStatus(id, FeedbackStatus.REJECTED);
    }

    public void delete(User actor, long id) {
        requireAdmin(actor);
        feedbackDao.delete(id);
    }

    public int countApproved() {
        return feedbackDao.countApproved();
    }

    private void requireAdmin(User actor) {
        if (actor == null || actor.getUserType() != UserType.ADMIN) {
            throw new AuthorizationException("Administrator access is required");
        }
    }
}
