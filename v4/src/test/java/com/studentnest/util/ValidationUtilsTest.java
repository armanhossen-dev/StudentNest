package com.studentnest.util;

import com.studentnest.model.RoomType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationUtilsTest {
    @Test
    void acceptsValidBangladeshiPhones() {
        assertEquals("01711000001", ValidationUtils.requirePhone("01711000001"));
        assertEquals("+8801711000001", ValidationUtils.requirePhone("+8801711000001"));
    }

    @Test
    void rejectsInvalidPhoneAndEmail() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requirePhone("123"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireEmail("not-an-email"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireUsername("ab"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requirePassword("123"));
    }

    @Test
    void validatesPriceBedroomsRatingAndLocation() {
        assertEquals(4500.0, ValidationUtils.requirePositivePrice("4500"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requirePositivePrice("0"));
        assertEquals(3, ValidationUtils.requireBedrooms(3));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireBedrooms(0));
        assertEquals(5, ValidationUtils.requireRating(5));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireRating(6));
        assertEquals("Khagan", ValidationUtils.requireLocation("Khagan"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireLocation("Dhaka"));
        assertEquals(RoomType.STUDIO, ValidationUtils.requireRoomType(RoomType.STUDIO));
    }

    @Test
    void onlyAllowsHttpMapLinks() {
        assertEquals("https://maps.google.com/?q=Khagan",
                ValidationUtils.optionalHttpUrl("https://maps.google.com/?q=Khagan"));
        assertNull(ValidationUtils.optionalHttpUrl(" "));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.optionalHttpUrl("ftp://example.com"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.optionalHttpUrl("javascript:alert(1)"));
        assertTrue(ValidationUtils.mapsSearchUrl("Khagan").startsWith("https://www.google.com/maps/search/"));
    }

    @Test
    void sanitizesAmenitiesAndImages() {
        assertEquals(List.of("WiFi", "Kitchen"),
                ValidationUtils.sanitizeAmenities(List.of("WiFi", "Kitchen", "Pool", "WiFi")));
        assertTrue(ValidationUtils.isAllowedImage("room.PNG"));
        assertFalse(ValidationUtils.isAllowedImage("notes.pdf"));
    }
}
