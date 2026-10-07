package com.lankaride.vehicle;

import com.lankaride.common.FuelType;
import com.lankaride.common.GearboxType;
import com.lankaride.common.VehicleStatus;
import java.math.BigDecimal;

/**
 * Builder for the many fields on a vehicle. The form already binds one
 * {@link Vehicle}; this copies those checked fields onto the saved row
 * in a single place so create and update do not each list every setter.
 */
public final class VehicleBuilder {

    private String registrationNumber;
    private String category;
    private String brand;
    private String model;
    private int seats;
    private GearboxType gearbox;
    private FuelType fuelType;
    private String features;
    private String photoUrl;
    private BigDecimal pricePerDay;
    private BigDecimal depositAmount;
    private VehicleStatus status;
    private String currentLocation;

    private VehicleBuilder() {
    }

    public static VehicleBuilder from(Vehicle source) {
        return new VehicleBuilder()
                .registrationNumber(source.getRegistrationNumber())
                .category(source.getCategory())
                .brand(source.getBrand())
                .model(source.getModel())
                .seats(source.getSeats())
                .gearbox(source.getGearbox())
                .fuelType(source.getFuelType())
                .features(source.getFeatures())
                .photoUrl(source.getPhotoUrl())
                .pricePerDay(source.getPricePerDay())
                .depositAmount(source.getDepositAmount())
                .status(source.getStatus())
                .currentLocation(source.getCurrentLocation());
    }

    public VehicleBuilder registrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
        return this;
    }

    public VehicleBuilder category(String category) {
        this.category = category;
        return this;
    }

    public VehicleBuilder brand(String brand) {
        this.brand = brand;
        return this;
    }

    public VehicleBuilder model(String model) {
        this.model = model;
        return this;
    }

    public VehicleBuilder seats(int seats) {
        this.seats = seats;
        return this;
    }

    public VehicleBuilder gearbox(GearboxType gearbox) {
        this.gearbox = gearbox;
        return this;
    }

    public VehicleBuilder fuelType(FuelType fuelType) {
        this.fuelType = fuelType;
        return this;
    }

    public VehicleBuilder features(String features) {
        this.features = features;
        return this;
    }

    public VehicleBuilder photoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return this;
    }

    public VehicleBuilder pricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
        return this;
    }

    public VehicleBuilder depositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
        return this;
    }

    public VehicleBuilder status(VehicleStatus status) {
        this.status = status;
        return this;
    }

    public VehicleBuilder currentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
        return this;
    }

    public void applyTo(Vehicle target) {
        target.setRegistrationNumber(registrationNumber);
        target.setCategory(category);
        target.setBrand(brand);
        target.setModel(model);
        target.setSeats(seats);
        target.setGearbox(gearbox);
        target.setFuelType(fuelType);
        target.setFeatures(features);
        target.setPhotoUrl(photoUrl);
        target.setPricePerDay(pricePerDay);
        target.setDepositAmount(depositAmount);
        target.setStatus(status);
        target.setCurrentLocation(currentLocation);
    }
}
