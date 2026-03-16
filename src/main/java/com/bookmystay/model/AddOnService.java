package com.bookmystay.model;

/**
 * Optional service attached to an existing reservation.
 */
public class AddOnService {

    private final String serviceName;
    private final double price;

    public AddOnService(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return serviceName + " ($" + String.format("%.2f", price) + ")";
    }
}
