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
        allHomes.add(new StudentHome("Khagan", 4500, "1 seat available", "Spacious room with attached bath, near university gate."));
        allHomes.add(new StudentHome("Candgaon", 3500, "2 seats available", "Shared room, 5 mins walk to campus, includes basic utilities."));
        allHomes.add(new StudentHome("Charabag", 5000, "Room available", "Single room, includes food and laundry service."));
        allHomes.add(new StudentHome("Khagan", 1800, "1 seat available", "Budget-friendly, shared with 3 others, good for first-year students."));
        allHomes.add(new StudentHome("Dattopara", 2800, "Seat available", "Quiet neighborhood, ideal for study, 10 mins by rickshaw."));
        allHomes.add(new StudentHome("Shadhupara", 5200, "Room available", "Premium single room with AC, attached balcony, and private kitchen."));
        allHomes.add(new StudentHome("Kumkumari", 3200, "2 seats available", "Good environment, friendly landlord, common living area."));
        allHomes.add(new StudentHome("Candgaon", 2200, "1 seat available", "Close to local market, shared room, fan included."));
        allHomes.add(new StudentHome("Charabag", 3800, "Room available", "Independent room, suitable for final year students, quiet area."));
        allHomes.add(new StudentHome("Dattopara", 1500, "Seat available", "Very basic, but affordable, 20 mins walk to main campus."));
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
