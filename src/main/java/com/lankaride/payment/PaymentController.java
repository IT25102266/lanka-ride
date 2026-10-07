package com.lankaride.payment;

import com.lankaride.booking.Booking;
import com.lankaride.booking.BookingService;
import com.lankaride.common.BookingStatus;
import com.lankaride.common.PaymentStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final PaymentGateway paymentGateway;

    public PaymentController(PaymentService paymentService, BookingService bookingService,
                             PaymentGateway paymentGateway) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.paymentGateway = paymentGateway;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE_MANAGER','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')")
    public String list(Model model) {
        model.addAttribute("payments", paymentService.listAll());
        return "payment/list";
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public String bookingPayments(@PathVariable Long bookingId, Authentication auth, Model model) {
        Booking booking = bookingService.getById(bookingId);
        if (!canView(auth, booking)) {
            return "redirect:/bookings";
        }
        model.addAttribute("booking", booking);
        model.addAttribute("payments", paymentService.listForBooking(bookingId));
        model.addAttribute("totalDue", paymentService.totalDueForApproval(booking));
        model.addAttribute("rentalAmount", paymentService.rentalDaysAmount(booking));
        model.addAttribute("staff", isFinanceStaff(auth));
        return "payment/invoice";
    }

    @GetMapping("/booking/{bookingId}/checkout")
    @PreAuthorize("isAuthenticated()")
    public String checkout(@PathVariable Long bookingId, Authentication auth, Model model) {
        Booking booking = bookingService.getById(bookingId);
        if (!booking.getCustomer().getUsername().equals(auth.getName())) {
            return "redirect:/bookings";
        }
        if (booking.getStatus() != BookingStatus.APPROVED
                || booking.getPaymentStatus() != PaymentStatus.PENDING_PAYMENT) {
            return "redirect:/payments/booking/" + bookingId;
        }
        addCheckout(model, booking);
        return "payment/gateway";
    }

    @PostMapping("/booking/{bookingId}/checkout")
    @PreAuthorize("isAuthenticated()")
    public String charge(@PathVariable Long bookingId,
                         @RequestParam String method,
                         @RequestParam String cardNumber,
                         @RequestParam String holder,
                         @RequestParam String expiry,
                         @RequestParam String cvv,
                         Authentication auth,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Booking booking = bookingService.getById(bookingId);
        if (!booking.getCustomer().getUsername().equals(auth.getName())) {
            return "redirect:/bookings";
        }
        try {
            PaymentGateway.GatewayDecision decision = paymentGateway.charge(method, cardNumber, holder, expiry, cvv);
            paymentService.payApprovedBooking(bookingId, auth.getName(), decision.declined(),
                    decision.brand(), decision.last4());
            redirectAttributes.addFlashAttribute("paidBrand", decision.brand());
            redirectAttributes.addFlashAttribute("paidLast4", decision.last4());
            return "redirect:/payments/booking/" + bookingId + "/checkout/success";
        } catch (IllegalArgumentException ex) {
            addCheckout(model, booking);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("method", method);
            model.addAttribute("holder", holder);
            model.addAttribute("expiry", expiry);
            return "payment/gateway";
        }
    }

    @GetMapping("/booking/{bookingId}/checkout/success")
    @PreAuthorize("isAuthenticated()")
    public String checkoutSuccess(@PathVariable Long bookingId, Authentication auth, Model model) {
        Booking booking = bookingService.getById(bookingId);
        if (!booking.getCustomer().getUsername().equals(auth.getName())) {
            return "redirect:/bookings";
        }
        if (booking.getPaymentStatus() != PaymentStatus.PAID) {
            return "redirect:/payments/booking/" + bookingId + "/checkout";
        }
        model.addAttribute("booking", booking);
        return "payment/gateway-success";
    }

    private void addCheckout(Model model, Booking booking) {
        model.addAttribute("booking", booking);
        model.addAttribute("totalDue", paymentService.totalDueForApproval(booking));
        model.addAttribute("rentalAmount", paymentService.rentalDaysAmount(booking));
    }

    @PostMapping("/booking/{bookingId}/pickup")
    @PreAuthorize("hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER','FLEET_COORDINATOR')")
    public String pickup(@PathVariable Long bookingId,
                         @RequestParam Integer pickupMileage,
                         @RequestParam String pickupFuelLevel,
                         RedirectAttributes redirectAttributes) {
        try {
            paymentService.recordPickup(bookingId, pickupMileage, pickupFuelLevel);
            redirectAttributes.addFlashAttribute("message", "Pickup mileage/fuel recorded");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/bookings/" + bookingId;
    }

    @PostMapping("/booking/{bookingId}/return")
    @PreAuthorize("hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER','FLEET_COORDINATOR','FINANCE_MANAGER')")
    public String completeReturn(@PathVariable Long bookingId,
                                 @RequestParam Integer returnMileage,
                                 @RequestParam String returnFuelLevel,
                                 @RequestParam(required = false) BigDecimal lateFee,
                                 @RequestParam(required = false) BigDecimal damageCharge,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        try {
            paymentService.completeReturn(bookingId, returnMileage, returnFuelLevel,
                    lateFee, damageCharge, auth.getName());
            redirectAttributes.addFlashAttribute("message", "Return completed; fees recorded if any");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/payments/booking/" + bookingId;
    }

    @PostMapping("/booking/{bookingId}/refund")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE_MANAGER')")
    public String refund(@PathVariable Long bookingId,
                         @RequestParam String reason,
                         Authentication auth,
                         RedirectAttributes redirectAttributes) {
        try {
            paymentService.refund(bookingId, reason, auth.getName());
            redirectAttributes.addFlashAttribute("message", "Refund processed");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/payments/booking/" + bookingId;
    }

    private boolean canView(Authentication auth, Booking booking) {
        return isFinanceStaff(auth) || booking.getCustomer().getUsername().equals(auth.getName());
    }

    private boolean isFinanceStaff(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FINANCE_MANAGER"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_BOOKING_SUPERVISOR"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OPERATIONS_MANAGER"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FLEET_COORDINATOR"));
    }
}
