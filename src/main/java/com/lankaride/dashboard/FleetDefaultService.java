package com.lankaride.dashboard;

import com.lankaride.common.FuelType;
import com.lankaride.common.GearboxType;
import com.lankaride.common.InputChecks;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FleetDefaultService {

    private final FleetDefaultRepository repository;

    public FleetDefaultService(FleetDefaultRepository repository) {
        this.repository = repository;
    }

    public List<FleetDefault> list(DefaultKind kind) {
        return repository.findByKindOrderByNameAsc(kind);
    }

    @Transactional
    public FleetDefault create(DefaultKind kind, String name, String brand, String categoryName,
                               Integer seats, GearboxType gearbox, FuelType fuelType) {
        FleetDefault item = new FleetDefault();
        apply(item, kind, name, brand, categoryName, seats, gearbox, fuelType);
        return repository.save(item);
    }

    @Transactional
    public FleetDefault update(Long id, DefaultKind kind, String name, String brand, String categoryName,
                               Integer seats, GearboxType gearbox, FuelType fuelType) {
        FleetDefault item = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Default not found"));
        apply(item, kind, name, brand, categoryName, seats, gearbox, fuelType);
        return repository.save(item);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Default not found");
        }
        repository.deleteById(id);
    }

    private void apply(FleetDefault item, DefaultKind kind, String name, String brand, String categoryName,
                       Integer seats, GearboxType gearbox, FuelType fuelType) {
        if (kind == null) {
            throw new IllegalArgumentException("Choose a default type");
        }
        item.setKind(kind);
        item.setName(InputChecks.label(name, "Name"));
        if (kind == DefaultKind.MODEL) {
            item.setBrand(InputChecks.label(brand, "Brand"));
            item.setCategoryName(InputChecks.label(categoryName, "Category"));
            if (seats == null || seats < 1 || seats > 20) {
                throw new IllegalArgumentException("Seats must be between 1 and 20");
            }
            if (gearbox == null || fuelType == null) {
                throw new IllegalArgumentException("Choose a gearbox and a fuel type");
            }
            item.setSeats(seats);
            item.setGearbox(gearbox);
            item.setFuelType(fuelType);
        } else {
            item.setBrand("");
            item.setCategoryName(null);
            item.setSeats(null);
            item.setGearbox(null);
            item.setFuelType(null);
        }
        boolean duplicate = repository.findByKindOrderByNameAsc(kind).stream()
                .anyMatch(other -> (item.getId() == null || !item.getId().equals(other.getId()))
                        && other.getName().equalsIgnoreCase(item.getName())
                        && brandKey(other).equalsIgnoreCase(brandKey(item)));
        if (duplicate) {
            throw new IllegalArgumentException("That default already exists");
        }
    }

    private static String brandKey(FleetDefault item) {
        return item.getBrand() == null ? "" : item.getBrand();
    }
}
