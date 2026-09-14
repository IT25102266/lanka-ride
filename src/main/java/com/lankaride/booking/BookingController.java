package com.lankaride.booking;

import com.lankaride.vehicle.VehicleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final VehicleService vehicleService;

    public BookingController(BookingService bookingService, VehicleService vehicleService) {
        this.bookingService = bookingService;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String list(Authentication auth, Model model) {
        boolean staff = isStaff(auth);
        model.addAttribute("staff", staff);
        if (staff) {
            model.addAttribute("bookings", bookingService.listAll());
            model.addAttribute("pendingCount", bookingService.countPending());
        } else {
            model.addAttribute("bookings", bookingService.listForCustomer(auth.getName()));
        }
        return "booking/list";
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')")
    public String pending(Model model) {
        model.addAttribute("bookings", bookingService.listPending());
        model.addAttribute("staff", true);
        model.addAttribute("pendingOnly", true);
        return "booking/list";
    }

    @GetMapping("/new")
    @PreAuthorize("isAuthenticated()")
    public String createForm(@RequestParam(required = false) Long vehicleId,
                             @RequestParam(required = false) Long branchId,
                             @RequestParam(required = false) LocalDate pickupDate,
                             @RequestParam(required = false) LocalDate returnDate,
                             Model model) {
        model.addAttribute("vehicles", vehicleService.search(null, null, null, null, null, null, null));
        model.addAttribute("branches", vehicleService.listBranches());
        model.addAttribute("selectedVehicleId", vehicleId);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("pickupDate", pickupDate != null ? pickupDate : LocalDate.now().plusDays(1));
        model.addAttribute("returnDate", returnDate != null ? returnDate : LocalDate.now().plusDays(3));
        return "booking/form";
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String create(@RequestParam Long vehicleId,
                         @RequestParam Long branchId,
                         @RequestParam LocalDate pickupDate,
                         @RequestParam LocalDate returnDate,
                         Authentication auth,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        try {
            Booking saved = bookingService.create(auth.getName(), vehicleId, branchId, pickupDate, returnDate);
            redirectAttributes.addFlashAttribute("message",
                    "Booking #" + saved.getId() + " submitted and awaiting approval.");
            return "redirect:/bookings/" + saved.getId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("vehicles", vehicleService.search(null, null, null, null, null, null, null));
            model.addAttribute("branches", vehicleService.listBranches());
            model.addAttribute("selectedVehicleId", vehicleId);
            model.addAttribute("pickupDate", pickupDate);
            model.addAttribute("returnDate", returnDate);
            return "booking/form";
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String detail(@PathVariable Long id, Authentication auth, Model model) {
        Booking booking = bookingService.getById(id);
        boolean staff = isStaff(auth);
        if (!staff && !booking.getCustomer().getUsername().equals(auth.getName())) {
            return "redirect:/bookings";
        }
        model.addAttribute("booking", booking);
        model.addAttribute("staff", staff);
        model.addAttribute("customerHistory",
                bookingService.listForCustomer(booking.getCustomer().getUsername()));
        return "booking/detail";
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')")
    public String approve(@PathVariable Long id,
                          @RequestParam(required = false) String note,
                          Authentication auth,
                          RedirectAttributes redirectAttributes) {
        try {
            bookingService.approve(id, auth.getName(), note);
            redirectAttributes.addFlashAttribute("message", "Booking approved");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/bookings/" + id;
    }

    @PostMapping("/{id}/deny")
    @PreAuthorize("hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')")
    public String deny(@PathVariable Long id,
                       @RequestParam String reason,
                       Authentication auth,
                       RedirectAttributes redirectAttributes) {
        try {
            bookingService.deny(id, auth.getName(), reason);
            redirectAttributes.addFlashAttribute("message", "Booking denied");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/bookings/" + id;
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public String cancel(@PathVariable Long id,
                         Authentication auth,
                         RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancel(id, auth.getName(), isStaff(auth));
            redirectAttributes.addFlashAttribute("message", "Booking cancelled");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/bookings/" + id;
    }

    private boolean isStaff(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_BOOKING_SUPERVISOR"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OPERATIONS_MANAGER"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FLEET_COORDINATOR"));
    }
}
