package com.lankaride.fleet;

import com.lankaride.booking.Booking;
import com.lankaride.common.MaintenanceStatus;
import com.lankaride.dashboard.DefaultKind;
import com.lankaride.dashboard.FleetDefault;
import com.lankaride.dashboard.FleetDefaultService;
import com.lankaride.vehicle.VehicleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/maintenance")
@PreAuthorize("hasAnyRole('ADMIN','FLEET_COORDINATOR')")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final VehicleService vehicleService;
    private final FleetDefaultService fleetDefaultService;

    public MaintenanceController(MaintenanceService maintenanceService, VehicleService vehicleService,
                                 FleetDefaultService fleetDefaultService) {
        this.maintenanceService = maintenanceService;
        this.vehicleService = vehicleService;
        this.fleetDefaultService = fleetDefaultService;
    }

    @ModelAttribute("serviceTypes")
    public List<FleetDefault> serviceTypes() {
        return fleetDefaultService.list(DefaultKind.SERVICE_TYPE);
    }

    @ModelAttribute("today")
    public LocalDate today() {
        return LocalDate.now();
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("records", maintenanceService.listAll());
        return "fleet/list";
    }

    @PostMapping("/reminders")
    public String reminders(RedirectAttributes redirectAttributes) {
        int sent = maintenanceService.sendDueReminders();
        redirectAttributes.addFlashAttribute("message",
                sent == 0 ? "No maintenance reminders were due." : "Sent " + sent + " maintenance reminder(s).");
        return "redirect:/maintenance";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Long vehicleId, Model model) {
        model.addAttribute("record", new MaintenanceRecord());
        model.addAttribute("vehicles", vehicleService.search(null, null, null, null, null, null, null));
        model.addAttribute("statuses", new MaintenanceStatus[]{MaintenanceStatus.OPEN, MaintenanceStatus.IN_PROGRESS});
        model.addAttribute("selectedVehicleId", vehicleId);
        model.addAttribute("conflicts", List.of());
        return "fleet/form";
    }

    @PostMapping
    public String create(@RequestParam Long vehicleId,
                         @Valid @ModelAttribute("record") MaintenanceRecord record,
                         BindingResult bindingResult,
                         @RequestParam(defaultValue = "false") boolean force,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicles", vehicleService.search(null, null, null, null, null, null, null));
            model.addAttribute("statuses", new MaintenanceStatus[]{MaintenanceStatus.OPEN, MaintenanceStatus.IN_PROGRESS});
            model.addAttribute("selectedVehicleId", vehicleId);
            model.addAttribute("conflicts", List.of());
            return "fleet/form";
        }
        try {
            List<Booking> conflicts = maintenanceService.findConflictingBookings(vehicleId);
            if (!conflicts.isEmpty() && !force) {
                model.addAttribute("record", record);
                model.addAttribute("vehicles", vehicleService.search(null, null, null, null, null, null, null));
                model.addAttribute("statuses", new MaintenanceStatus[]{MaintenanceStatus.OPEN, MaintenanceStatus.IN_PROGRESS});
                model.addAttribute("selectedVehicleId", vehicleId);
                model.addAttribute("conflicts", conflicts);
                model.addAttribute("error",
                        "This vehicle has future/active bookings. Tick confirm to take it offline anyway.");
                return "fleet/form";
            }
            MaintenanceRecord saved = maintenanceService.create(vehicleId, record, force || conflicts.isEmpty());
            redirectAttributes.addFlashAttribute("message",
                    "Maintenance record #" + saved.getId() + " created. Vehicle marked MAINTENANCE.");
            return "redirect:/maintenance/" + saved.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("record", record);
            model.addAttribute("vehicles", vehicleService.search(null, null, null, null, null, null, null));
            model.addAttribute("statuses", new MaintenanceStatus[]{MaintenanceStatus.OPEN, MaintenanceStatus.IN_PROGRESS});
            model.addAttribute("selectedVehicleId", vehicleId);
            model.addAttribute("conflicts", maintenanceService.findConflictingBookings(vehicleId));
            return "fleet/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        MaintenanceRecord record = maintenanceService.getById(id);
        model.addAttribute("record", record);
        model.addAttribute("history", maintenanceService.listByVehicle(record.getVehicle().getId()));
        return "fleet/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        MaintenanceRecord record = maintenanceService.getById(id);
        model.addAttribute("record", record);
        model.addAttribute("vehicles", List.of(record.getVehicle()));
        model.addAttribute("statuses", new MaintenanceStatus[]{MaintenanceStatus.OPEN, MaintenanceStatus.IN_PROGRESS});
        model.addAttribute("selectedVehicleId", record.getVehicle().getId());
        model.addAttribute("conflicts", List.of());
        model.addAttribute("editing", true);
        return "fleet/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("record") MaintenanceRecord record,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicles", List.of(maintenanceService.getById(id).getVehicle()));
            model.addAttribute("statuses", new MaintenanceStatus[]{MaintenanceStatus.OPEN, MaintenanceStatus.IN_PROGRESS});
            model.addAttribute("selectedVehicleId", maintenanceService.getById(id).getVehicle().getId());
            model.addAttribute("editing", true);
            model.addAttribute("conflicts", List.of());
            return "fleet/form";
        }
        try {
            maintenanceService.update(id, record);
            redirectAttributes.addFlashAttribute("message", "Maintenance record updated");
            return "redirect:/maintenance/" + id;
        } catch (IllegalArgumentException ex) {
            MaintenanceRecord existing = maintenanceService.getById(id);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("record", record);
            model.addAttribute("vehicles", List.of(existing.getVehicle()));
            model.addAttribute("statuses", new MaintenanceStatus[]{MaintenanceStatus.OPEN, MaintenanceStatus.IN_PROGRESS});
            model.addAttribute("selectedVehicleId", existing.getVehicle().getId());
            model.addAttribute("conflicts", List.of());
            model.addAttribute("editing", true);
            return "fleet/form";
        }
    }

    @PostMapping("/{id}/close")
    public String close(@PathVariable Long id,
                        @RequestParam(required = false) BigDecimal finalCost,
                        RedirectAttributes redirectAttributes) {
        try {
            maintenanceService.close(id, finalCost);
            redirectAttributes.addFlashAttribute("message",
                    "Maintenance closed. Vehicle set back to AVAILABLE if no other open jobs.");
            return "redirect:/maintenance/" + id;
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/maintenance/" + id;
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        maintenanceService.deletePermanently(id);
        redirectAttributes.addFlashAttribute("message", "Maintenance record #" + id + " permanently deleted");
        return "redirect:/maintenance";
    }
}
