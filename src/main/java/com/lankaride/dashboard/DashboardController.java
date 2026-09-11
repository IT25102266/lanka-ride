package com.lankaride.dashboard;

import com.lankaride.booking.Booking;
import com.lankaride.booking.BookingService;
import com.lankaride.common.BookingStatus;
import com.lankaride.common.PaymentStatus;
import com.lankaride.common.VehicleStatus;
import com.lankaride.vehicle.BranchRepository;
import com.lankaride.vehicle.VehicleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final VehicleRepository vehicleRepository;
    private final BranchRepository branchRepository;
    private final BookingService bookingService;

    public DashboardController(VehicleRepository vehicleRepository,
                               BranchRepository branchRepository,
                               BookingService bookingService) {
        this.vehicleRepository = vehicleRepository;
        this.branchRepository = branchRepository;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','BOOKING_SUPERVISOR','FLEET_COORDINATOR','FINANCE_MANAGER','OPERATIONS_MANAGER')")
    public String dashboard(Authentication authentication, Model model) {
        long totalVehicles = vehicleRepository.count();
        long availableVehicles = vehicleRepository.countByStatus(VehicleStatus.AVAILABLE);
        long maintenanceVehicles = vehicleRepository.countByStatus(VehicleStatus.MAINTENANCE);
        long branchCount = branchRepository.count();
        long pendingBookings = bookingService.countPending();

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.replace("ROLE_", ""))
                .collect(Collectors.joining(", "));

        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", roles);
        model.addAttribute("totalVehicles", totalVehicles);
        model.addAttribute("availableVehicles", availableVehicles);
        model.addAttribute("maintenanceVehicles", maintenanceVehicles);
        model.addAttribute("branchCount", branchCount);
        model.addAttribute("pendingBookings", pendingBookings);
        return "dashboard/index";
    }

    @GetMapping("/app")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String customerHome(Authentication authentication, Model model) {
        List<Booking> bookings = bookingService.listForCustomer(authentication.getName());
        long awaitingPayment = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.APPROVED
                        && b.getPaymentStatus() == PaymentStatus.PENDING_PAYMENT)
                .count();
        long active = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.ONGOING
                        || b.getStatus() == BookingStatus.PENDING
                        || b.getStatus() == BookingStatus.APPROVED)
                .count();

        model.addAttribute("username", authentication.getName());
        model.addAttribute("bookings", bookings.stream().limit(8).toList());
        model.addAttribute("awaitingPayment", awaitingPayment);
        model.addAttribute("activeTrips", active);
        return "dashboard/customer";
    }
}
