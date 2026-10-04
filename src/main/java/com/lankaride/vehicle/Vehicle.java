package com.lankaride.vehicle;

import com.lankaride.common.FuelType;
import com.lankaride.common.GearboxType;
import com.lankaride.common.VehicleStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Registration number is required")
    @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9\\-]{2,19}$",
            message = "Registration must be 3–20 letters, numbers, or hyphens")
    @Column(nullable = false, unique = true, length = 20)
    private String registrationNumber;

    @NotBlank(message = "Category is required")
    @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9 .'\\-]{1,49}$",
            message = "Category can only use letters, numbers, spaces, and hyphens")
    @Column(nullable = false, length = 50)
    private String category;

    @NotBlank(message = "Brand is required")
    @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9 .'\\-]{1,49}$",
            message = "Brand can only use letters, numbers, spaces, and hyphens")
    @Column(nullable = false, length = 50)
    private String brand;

    @NotBlank(message = "Model is required")
    @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9 .'\\-]{1,49}$",
            message = "Model can only use letters, numbers, spaces, and hyphens")
    @Column(nullable = false, length = 50)
    private String model;

    @Min(value = 1, message = "Seats must be at least 1")
    @Max(value = 20, message = "Seats cannot be more than 20")
    private int seats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GearboxType gearbox = GearboxType.AUTOMATIC;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FuelType fuelType = FuelType.PETROL;

    @Size(max = 500, message = "Features must be 500 characters or fewer")
    @Column(length = 500)
    private String features;

    @Size(max = 500, message = "Photo URL must be 500 characters or fewer")
    @Pattern(regexp = "^$|https?://\\S{4,490}$", message = "Photo URL must start with http:// or https://")
    @Column(length = 500)
    private String photoUrl;

    @NotNull(message = "Price per day is required")
    @DecimalMin(value = "0.01", message = "Price per day must be greater than zero")
    @DecimalMax(value = "10000000", message = "Price per day is too large")
    @Digits(integer = 8, fraction = 2, message = "Price per day can have at most 2 decimal places")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    @NotNull(message = "Deposit is required")
    @DecimalMin(value = "0.01", message = "Deposit must be greater than zero")
    @DecimalMax(value = "10000000", message = "Deposit is too large")
    @Digits(integer = 8, fraction = 2, message = "Deposit can have at most 2 decimal places")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal depositAmount;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Size(max = 100, message = "Location must be 100 characters or fewer")
    @Column(length = 100)
    private String currentLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public GearboxType getGearbox() {
        return gearbox;
    }

    public void setGearbox(GearboxType gearbox) {
        this.gearbox = gearbox;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }
}
