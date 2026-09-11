package com.lankaride.support;

import com.lankaride.auth.UserAccount;
import com.lankaride.auth.UserAccountRepository;
import com.lankaride.common.TicketStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/support")
public class SupportController {

    private final SupportTicketRepository supportTicketRepository;
    private final UserAccountRepository userAccountRepository;
    private final NotificationService notificationService;
    private final NotificationLogRepository notificationLogRepository;

    public SupportController(SupportTicketRepository supportTicketRepository,
                             UserAccountRepository userAccountRepository,
                             NotificationService notificationService,
                             NotificationLogRepository notificationLogRepository) {
        this.supportTicketRepository = supportTicketRepository;
        this.userAccountRepository = userAccountRepository;
        this.notificationService = notificationService;
        this.notificationLogRepository = notificationLogRepository;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String list(Authentication auth, Model model) {
        boolean staff = isStaff(auth);
        model.addAttribute("staff", staff);
        model.addAttribute("tickets", staff
                ? supportTicketRepository.findAllByOrderByCreatedAtDesc()
                : supportTicketRepository.findByCustomerUsernameOrderByCreatedAtDesc(auth.getName()));
        return "support/list";
    }

    @GetMapping("/new")
    @PreAuthorize("isAuthenticated()")
    public String createForm() {
        return "support/form";
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String create(@RequestParam String subject,
                         @RequestParam String message,
                         Authentication auth,
                         RedirectAttributes redirectAttributes) {
        UserAccount customer = userAccountRepository.findByUsername(auth.getName()).orElseThrow();
        SupportTicket ticket = new SupportTicket();
        ticket.setCustomer(customer);
        ticket.setSubject(subject.trim());
        ticket.setMessage(message.trim());
        ticket.setStatus(TicketStatus.OPEN);
        SupportTicket saved = supportTicketRepository.save(ticket);
        notificationService.email(customer.getEmail(), "Support ticket #" + saved.getId() + " opened",
                "We received: " + saved.getSubject());
        redirectAttributes.addFlashAttribute("message", "Ticket #" + saved.getId() + " submitted");
        return "redirect:/support/" + saved.getId();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String detail(@PathVariable Long id, Authentication auth, Model model) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        boolean staff = isStaff(auth);
        if (!staff && !ticket.getCustomer().getUsername().equals(auth.getName())) {
            return "redirect:/support";
        }
        model.addAttribute("ticket", ticket);
        model.addAttribute("staff", staff);
        model.addAttribute("statuses", TicketStatus.values());
        return "support/detail";
    }

    @PostMapping("/{id}/respond")
    @PreAuthorize("hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')")
    public String respond(@PathVariable Long id,
                          @RequestParam String staffResponse,
                          @RequestParam TicketStatus status,
                          RedirectAttributes redirectAttributes) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        ticket.setStaffResponse(staffResponse);
        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);
        notificationService.email(ticket.getCustomer().getEmail(),
                "Support ticket #" + ticket.getId() + " updated",
                "Status: " + status + ". " + staffResponse);
        redirectAttributes.addFlashAttribute("message", "Ticket updated");
        return "redirect:/support/" + id;
    }

    @GetMapping("/notifications")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
    public String notifications(Model model) {
        model.addAttribute("notifications", notificationLogRepository.findAllByOrderByCreatedAtDesc());
        return "support/notifications";
    }

    private boolean isStaff(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_BOOKING_SUPERVISOR"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OPERATIONS_MANAGER"));
    }
}
