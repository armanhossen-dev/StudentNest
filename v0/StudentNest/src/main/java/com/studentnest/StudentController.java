package com.studentnest;

import com.studentnest.model.StudentHome;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.event.ActionEvent; // Import ActionEvent

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // For stream API filtering

public class StudentController {
    @FXML
    private ComboBox<String> locationComboBox;

    @FXML
    private ListView<String> homeListView;

    // A list to hold all the available student homes
    private final List<StudentHome> allHomes = new ArrayList<>();

    // Variable to store the currently selected price range (e.g., "1000-2000", "4000-MAX")
    private String selectedPriceRange = null;

    @FXML
    public void initialize() {
        // Initialize sample data (in the future, this would come from a database)
        populateSampleData();

        // Populate location combo box
        locationComboBox.getItems().addAll("Khagan", "Candgaon", "Charabag", "Kumkumari", "Dattopara", "Shadhupara");
        locationComboBox.getSelectionModel().selectFirst(); // Select first item by default

        // Add listener to location ComboBox to filter homes when location changes
        locationComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            filterHomes();
        });

        // Initial display of all homes (or based on initial location selection)
        filterHomes();
    }

    /**
     * Handles clicks on the price range buttons.
     * The `userData` property of the button is used to pass the price range string.
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    protected void onPriceRangeButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        selectedPriceRange = (String) clickedButton.getUserData();
        filterHomes(); // Re-filter homes based on the new price range
    }

    /**
     * Clears the selected price filter and re-filters the homes.
     */
    @FXML
    protected void onClearPriceFilter() {
        selectedPriceRange = null; // Reset the price range filter
        filterHomes(); // Re-filter homes
    }

    /**
     * Filters the list of homes based on the selected location and price range.
     */
    private void filterHomes() {
        String selectedLocation = locationComboBox.getSelectionModel().getSelectedItem();

        List<StudentHome> filteredList = allHomes.stream()
                .filter(home -> {
                    // Apply location filter
                    boolean locationMatch = (selectedLocation == null || selectedLocation.isEmpty() || selectedLocation.equals("Select Location")) || home.getLocation().equals(selectedLocation);
                    return locationMatch;
                })
                .filter(home -> {
                    // Apply price filter if a range is selected
                    if (selectedPriceRange == null) {
                        return true; // No price filter applied
                    }

                    try {
                        String[] parts = selectedPriceRange.split("-");
                        double minPrice = Double.parseDouble(parts[0]);
                        double maxPrice;

                        if (parts[1].equals("MAX")) {
                            maxPrice = Double.MAX_VALUE; // For "4000+" range
                        } else {
                            maxPrice = Double.parseDouble(parts[1]);
                        }

                        return home.getPrice() >= minPrice && home.getPrice() <= maxPrice;
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing price range: " + selectedPriceRange + " - " + e.getMessage());
                        return false; // Should not happen with well-defined userData
                    }
                })
                .collect(Collectors.toList());

        updateListView(filteredList);
    }

    /**
     * Updates the ListView with the given list of homes.
     * @param homes The list of StudentHome objects to display.
     */
    private void updateListView(List<StudentHome> homes) {
        ObservableList<String> homeDescriptions = FXCollections.observableArrayList();
        if (homes.isEmpty()) {
            homeDescriptions.add("No homes found matching your criteria.");
        } else {
            for (StudentHome home : homes) {
                String description = String.format("Location: %s | Price: ৳%.0f | Availability: %s | Details: %s",
                        home.getLocation(), home.getPrice(), home.getAvailability(), home.getDescription());
                homeDescriptions.add(description);
            }
        }
        homeListView.setItems(homeDescriptions);
    }

    /**
     * Populates the allHomes list with sample data.
     * In a real application, this data would be loaded from a database.
     */
    private void populateSampleData() {
        allHomes.add(new StudentHome("Khagan", 4500, "1 seat available", "Spacious room with attached bath."));
        allHomes.add(new StudentHome("Candgaon", 3500, "2 seats available", "Shared room, 5 mins walk to campus."));
        allHomes.add(new StudentHome("Charabag", 5000, "Room available", "Single room, includes food and laundry service."));
        allHomes.add(new StudentHome("Khagan", 1800, "1 seat available", "Budget-friendly, shared with 3 others."));
        allHomes.add(new StudentHome("Dattopara", 2800, "Seat available", "Quiet neighborhood, ideal for study, 2 mins by rickshaw."));
        allHomes.add(new StudentHome("Shadhupara", 5200, "Room available", "Premium single room with AC, attached balcony, and private kitchen."));
        allHomes.add(new StudentHome("Kumkumari", 3200, "2 seats available", "Good environment, friendly landlord."));
        allHomes.add(new StudentHome("Candgaon", 2200, "1 seat available", "Close to local market, shared room, fan included."));
        allHomes.add(new StudentHome("Charabag", 3800, "Room available", "Independent room, suitable for final year students, quiet area."));
        allHomes.add(new StudentHome("Dattopara", 1500, "Seat available", "Very basic, but affordable, 10 mins walk to main campus."));

        allHomes.add(new StudentHome("Khagan", 2500, "2 seats available", "Simple room, near bus stand, shared bathroom."));
        allHomes.add(new StudentHome("Candgaon", 4000, "Room available", "Well-lit single room with study table, close to campus."));
        allHomes.add(new StudentHome("Charabag", 3000, "1 seat available", "Shared room, includes Wi-Fi and filtered water."));
        allHomes.add(new StudentHome("Dattopara", 4500, "Room available", "Quiet area, good for group study, separate kitchen."));
        allHomes.add(new StudentHome("Shadhupara", 6000, "Premium room", "AC, attached bath, free Wi-Fi, near library."));
        allHomes.add(new StudentHome("Kumkumari", 2800, "2 seats available", "Shared with 2 others, kitchen access, clean environment."));
        allHomes.add(new StudentHome("Khagan", 3300, "Seat available", "Close to grocery shops, includes basic furniture."));
        allHomes.add(new StudentHome("Candgaon", 2000, "1 seat available", "Low budget shared room, fan only."));
        allHomes.add(new StudentHome("Charabag", 5000, "Room available", "Independent single room, balcony with good view."));
        allHomes.add(new StudentHome("Dattopara", 2600, "Seat available", "Budget friendly, quiet area, close to tea stalls."));

        allHomes.add(new StudentHome("Shadhupara", 4800, "Room available", "AC room, includes Wi-Fi and cleaning service."));
        allHomes.add(new StudentHome("Kumkumari", 3200, "2 seats available", "Common kitchen, friendly environment, 5 mins to bus stand."));
        allHomes.add(new StudentHome("Khagan", 3700, "Seat available", "Spacious shared room with attached toilet."));
        allHomes.add(new StudentHome("Candgaon", 2400, "Seat available", "Budget-friendly, includes water and Wi-Fi."));
        allHomes.add(new StudentHome("Charabag", 5200, "Room available", "Premium single with attached kitchen, balcony."));
        allHomes.add(new StudentHome("Dattopara", 1900, "Seat available", "Very basic, shared bathroom, student-friendly landlord."));
        allHomes.add(new StudentHome("Shadhupara", 5600, "Room available", "Well furnished, AC, attached bathroom."));
        allHomes.add(new StudentHome("Kumkumari", 3100, "2 seats available", "Shared room, includes basic furniture."));
        allHomes.add(new StudentHome("Khagan", 4000, "Seat available", "Single room, 10 mins to university."));
        allHomes.add(new StudentHome("Candgaon", 2700, "Seat available", "Close to campus, includes Wi-Fi and fan."));

        allHomes.add(new StudentHome("Charabag", 4800, "Room available", "AC room, near campus, quiet study environment."));
        allHomes.add(new StudentHome("Dattopara", 3000, "Seat available", "Shared with 2 students, fan, water filter included."));
        allHomes.add(new StudentHome("Shadhupara", 5000, "Room available", "Premium single with AC and balcony."));
        allHomes.add(new StudentHome("Kumkumari", 2800, "2 seats available", "Clean environment, kitchen access."));
        allHomes.add(new StudentHome("Khagan", 3500, "Seat available", "Shared room, includes Wi-Fi."));
        allHomes.add(new StudentHome("Candgaon", 3200, "Room available", "Near campus, quiet place, balcony."));
        allHomes.add(new StudentHome("Charabag", 2600, "Seat available", "Budget friendly shared room."));
        allHomes.add(new StudentHome("Dattopara", 1800, "Seat available", "Very basic, low rent, 20 mins walk."));
        allHomes.add(new StudentHome("Shadhupara", 5400, "Room available", "Premium AC room, fully furnished."));
        allHomes.add(new StudentHome("Kumkumari", 3000, "2 seats available", "Shared room, study table, fan included."));

        allHomes.add(new StudentHome("Khagan", 4200, "Seat available", "Single room, quiet, close to library."));
        allHomes.add(new StudentHome("Candgaon", 2500, "Seat available", "Budget friendly, fan only."));
        allHomes.add(new StudentHome("Charabag", 4900, "Room available", "Independent AC room."));
        allHomes.add(new StudentHome("Dattopara", 2100, "Seat available", "Shared room, budget rent."));
        allHomes.add(new StudentHome("Shadhupara", 5800, "Room available", "Premium AC room with study desk."));
        allHomes.add(new StudentHome("Kumkumari", 2700, "2 seats available", "Budget shared room."));
        allHomes.add(new StudentHome("Khagan", 3600, "Seat available", "Near campus, Wi-Fi available."));
        allHomes.add(new StudentHome("Candgaon", 2900, "Room available", "Close to market, shared room."));
        allHomes.add(new StudentHome("Charabag", 5100, "Room available", "Single AC room, furnished."));
        allHomes.add(new StudentHome("Dattopara", 2300, "Seat available", "Budget shared room, fan included."));
    }


    /**
     * Future Feature: Method to load data from a database.
     * This method is commented out for now as per the project requirements.
     */
    /*
    private void loadDataFromDatabase() {
        // Pseudo-code for future implementation
        // try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_db", "user", "password")) {
        //     String sql = "SELECT location, price, availability, description FROM student_homes";
        //     PreparedStatement pstmt = conn.prepareStatement(sql);
        //     ResultSet rs = pstmt.executeQuery();
        //     while (rs.next()) {
        //         String location = rs.getString("location");
        //         double price = rs.getDouble("price");
        //         String availability = rs.getString("availability");
        //         String description = rs.getString("description");
        //         allHomes.add(new StudentHome(location, price, availability, description));
        //     }
        // } catch (SQLException e) {
        //     e.printStackTrace();
        // }
    }
    */
}
