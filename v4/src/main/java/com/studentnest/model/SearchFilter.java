package com.studentnest.model;

public class SearchFilter {
    public enum RentRange {
        ANY("Any rent", 0, Double.MAX_VALUE),
        UNDER_5000("Under ৳5,000", 0, 4999.99),
        FROM_5000_TO_8000("৳5,000–৳8,000", 5000, 8000),
        FROM_8000_TO_12000("৳8,000–৳12,000", 8000, 12000),
        OVER_12000("Over ৳12,000", 12000.01, Double.MAX_VALUE);

        private final String label;
        private final double min;
        private final double max;

        RentRange(String label, double min, double max) {
            this.label = label;
            this.min = min;
            this.max = max;
        }

        public String getLabel() {
            return label;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }
    }

    public enum SortOption {
        DEFAULT("Default"),
        PRICE_LOW_HIGH("Price Low–High"),
        PRICE_HIGH_LOW("Price High–Low"),
        MOST_RECENT("Most Recent");

        private final String label;

        SortOption(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum BedroomFilter {
        ANY("Any bedrooms", 0, Integer.MAX_VALUE),
        ONE("1", 1, 1),
        TWO("2", 2, 2),
        THREE_PLUS("3+", 3, Integer.MAX_VALUE);

        private final String label;
        private final int min;
        private final int max;

        BedroomFilter(String label, int min, int max) {
            this.label = label;
            this.min = min;
            this.max = max;
        }

        public String getLabel() {
            return label;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }

    private String keyword = "";
    private String location = "";
    private RentRange rentRange = RentRange.ANY;
    private RoomType roomType;
    private BedroomFilter bedrooms = BedroomFilter.ANY;
    private SortOption sortOption = SortOption.DEFAULT;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword == null ? "" : keyword.trim();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location == null ? "" : location.trim();
    }

    public RentRange getRentRange() {
        return rentRange;
    }

    public void setRentRange(RentRange rentRange) {
        this.rentRange = rentRange == null ? RentRange.ANY : rentRange;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public BedroomFilter getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(BedroomFilter bedrooms) {
        this.bedrooms = bedrooms == null ? BedroomFilter.ANY : bedrooms;
    }

    public SortOption getSortOption() {
        return sortOption;
    }

    public void setSortOption(SortOption sortOption) {
        this.sortOption = sortOption == null ? SortOption.DEFAULT : sortOption;
    }
}
