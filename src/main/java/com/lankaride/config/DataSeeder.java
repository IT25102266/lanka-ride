package com.lankaride.config;

import com.lankaride.auth.Role;
import com.lankaride.auth.RoleRepository;
import com.lankaride.auth.UserAccount;
import com.lankaride.auth.UserAccountRepository;
import com.lankaride.common.*;
import com.lankaride.vehicle.Branch;
import com.lankaride.vehicle.BranchRepository;
import com.lankaride.vehicle.Vehicle;
import com.lankaride.vehicle.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.util.Set;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(RoleRepository roleRepository,
                               UserAccountRepository userAccountRepository,
                               BranchRepository branchRepository,
                               VehicleRepository vehicleRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            for (RoleName roleName : RoleName.values()) {
                roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(new Role(roleName)));
            }

            Branch colombo = branchRepository.findByName("Colombo")
                    .orElseGet(() -> branchRepository.save(new Branch("Colombo", "Colombo Fort Depot")));
            Branch kandy = branchRepository.findByName("Kandy")
                    .orElseGet(() -> branchRepository.save(new Branch("Kandy", "Kandy City Branch")));
            Branch galle = branchRepository.findByName("Galle")
                    .orElseGet(() -> branchRepository.save(new Branch("Galle", "Galle Face Branch")));

            if (!userAccountRepository.existsByUsername("admin")) {
                UserAccount admin = new UserAccount();
                admin.setUsername("admin");
                admin.setEmail("admin@lankaride.lk");
                admin.setFullName("System Administrator");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setRoles(Set.of(
                        roleRepository.findByName(RoleName.ADMIN).orElseThrow(),
                        roleRepository.findByName(RoleName.FLEET_COORDINATOR).orElseThrow()
                ));
                userAccountRepository.save(admin);
            }

            if (!userAccountRepository.existsByUsername("fleet")) {
                UserAccount fleet = new UserAccount();
                fleet.setUsername("fleet");
                fleet.setEmail("fleet@lankaride.lk");
                fleet.setFullName("Fleet Coordinator");
                fleet.setPasswordHash(passwordEncoder.encode("fleet123"));
                fleet.setRoles(Set.of(roleRepository.findByName(RoleName.FLEET_COORDINATOR).orElseThrow()));
                userAccountRepository.save(fleet);
            }

            if (!userAccountRepository.existsByUsername("customer")) {
                UserAccount customer = new UserAccount();
                customer.setUsername("customer");
                customer.setEmail("customer@example.com");
                customer.setFullName("Demo Customer");
                customer.setPasswordHash(passwordEncoder.encode("customer123"));
                customer.setRoles(Set.of(roleRepository.findByName(RoleName.CUSTOMER).orElseThrow()));
                userAccountRepository.save(customer);
            }

            if (!userAccountRepository.existsByUsername("supervisor")) {
                UserAccount supervisor = new UserAccount();
                supervisor.setUsername("supervisor");
                supervisor.setEmail("supervisor@lankaride.lk");
                supervisor.setFullName("Booking Supervisor");
                supervisor.setPasswordHash(passwordEncoder.encode("super123"));
                supervisor.setRoles(Set.of(roleRepository.findByName(RoleName.BOOKING_SUPERVISOR).orElseThrow()));
                userAccountRepository.save(supervisor);
            }

            if (!userAccountRepository.existsByUsername("finance")) {
                UserAccount finance = new UserAccount();
                finance.setUsername("finance");
                finance.setEmail("finance@lankaride.lk");
                finance.setFullName("Finance Manager");
                finance.setPasswordHash(passwordEncoder.encode("finance123"));
                finance.setRoles(Set.of(roleRepository.findByName(RoleName.FINANCE_MANAGER).orElseThrow()));
                userAccountRepository.save(finance);
            }

            if (!userAccountRepository.existsByUsername("operations")) {
                UserAccount operations = new UserAccount();
                operations.setUsername("operations");
                operations.setEmail("operations@lankaride.lk");
                operations.setFullName("Operations Manager");
                operations.setPasswordHash(passwordEncoder.encode("ops123"));
                operations.setRoles(Set.of(roleRepository.findByName(RoleName.OPERATIONS_MANAGER).orElseThrow()));
                userAccountRepository.save(operations);
            }

            if (vehicleRepository.count() == 0) {
                vehicleRepository.save(sample("CAB-1001", "Sedan", "Toyota", "Axio", 5,
                        GearboxType.AUTOMATIC, FuelType.PETROL, "AC, GPS",
                        new BigDecimal("8500.00"), new BigDecimal("20000.00"), colombo));
                vehicleRepository.save(sample("CAB-2002", "SUV", "Honda", "CR-V", 7,
                        GearboxType.AUTOMATIC, FuelType.HYBRID, "AC, 4WD",
                        new BigDecimal("12000.00"), new BigDecimal("30000.00"), kandy));
                vehicleRepository.save(sample("CAB-3003", "Van", "Nissan", "Caravan", 12,
                        GearboxType.MANUAL, FuelType.DIESEL, "AC",
                        new BigDecimal("15000.00"), new BigDecimal("35000.00"), galle));
            }
        };
    }

    private static Vehicle sample(String reg, String category, String brand, String model, int seats,
                                  GearboxType gearbox, FuelType fuel, String features,
                                  BigDecimal price, BigDecimal deposit, Branch branch) {
        Vehicle v = new Vehicle();
        v.setRegistrationNumber(reg);
        v.setCategory(category);
        v.setBrand(brand);
        v.setModel(model);
        v.setSeats(seats);
        v.setGearbox(gearbox);
        v.setFuelType(fuel);
        v.setFeatures(features);
        v.setPhotoUrl("https://placehold.co/600x400?text=" + brand + "+" + model);
        v.setPricePerDay(price);
        v.setDepositAmount(deposit);
        v.setBranch(branch);
        v.setCurrentLocation(branch.getName());
        v.setStatus(VehicleStatus.AVAILABLE);
        return v;
    }
}
