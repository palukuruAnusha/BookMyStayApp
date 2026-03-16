package com.bookmystay.model;

/**
 * Base abstraction for all room types in the booking domain.
 */
public abstract class Room {

    private final String roomType;
    private final int beds;
    private final int sizeSqFt;
    private final double pricePerNight;

    protected Room(String roomType, int beds, int sizeSqFt, double pricePerNight) {
        this.roomType = roomType;
        this.beds = beds;
        this.sizeSqFt = sizeSqFt;
        this.pricePerNight = pricePerNight;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getBeds() {
        return beds;
    }

    public int getSizeSqFt() {
        return sizeSqFt;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public String getDetails() {
        return String.format("Type: %s | Beds: %d | Size: %d sq.ft | Price: $%.2f/night", roomType, beds, sizeSqFt, pricePerNight);
    }
}
