package com.lankaride.dashboard;

import com.lankaride.common.FuelType;
import com.lankaride.common.GearboxType;
import jakarta.persistence.*;

@Entity
@Table(name = "fleet_defaults")
public class FleetDefault {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DefaultKind kind;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String brand;

    @Column(length = 50)
    private String categoryName;

    private Integer seats;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private GearboxType gearbox;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FuelType fuelType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DefaultKind getKind() {
        return kind;
    }

    public void setKind(DefaultKind kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
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
}
