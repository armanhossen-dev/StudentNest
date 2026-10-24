package com.studentnest.controller;

import com.studentnest.model.Room;

public interface ViewHost {
    void showHome();

    void showBrowse();

    void showFavorites();

    void showListProperty();

    void showMyListings();

    void showAdmin();

    void showFeedback();

    void showAbout();

    void showLogin();

    void openPropertyDetails(Room room);

    void openListEditor(Room room);

    void copyPhone(String phone);

    void openMap(Room room);
}
