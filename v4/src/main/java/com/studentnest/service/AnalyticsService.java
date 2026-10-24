package com.studentnest.service;

import com.studentnest.dao.FeedbackDao;
import com.studentnest.dao.RoomDao;
import com.studentnest.dao.UserDao;
import com.studentnest.model.DashboardStats;
import com.studentnest.model.UserType;

public class AnalyticsService {
    private final UserDao userDao;
    private final RoomDao roomDao;
    private final FeedbackDao feedbackDao;

    public AnalyticsService() {
        this(new UserDao(), new RoomDao(), new FeedbackDao());
    }

    public AnalyticsService(UserDao userDao, RoomDao roomDao, FeedbackDao feedbackDao) {
        this.userDao = userDao;
        this.roomDao = roomDao;
        this.feedbackDao = feedbackDao;
    }

    public DashboardStats load() {
        DashboardStats stats = new DashboardStats();
        stats.setRegisteredUsers(userDao.countAll());
        stats.setRegisteredStudents(userDao.countByType(UserType.STUDENT));
        stats.setHouseOwners(userDao.countByType(UserType.OWNER));
        stats.setAdmins(userDao.countByType(UserType.ADMIN));
        stats.setActiveListings(roomDao.countAll());
        stats.setVerifiedListings(roomDao.countVerified());
        stats.setAvailableListings(roomDao.countAvailable());
        stats.setCommunityReviews(feedbackDao.countApproved());
        stats.setListingsByLocation(roomDao.countByLocation());
        return stats;
    }
}
