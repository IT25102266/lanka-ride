package com.lankaride.fleet;

import com.lankaride.booking.Booking;
import com.lankaride.common.MaintenanceStatus;
import com.lankaride.vehicle.VehicleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/maintenance")
@PreAuthorize("hasAnyRole('ADMIN','FLEET_COORDINATOR')")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final VehicleService vehicleService;

    public MaintenanceController(MaintenanceService maintenanceService, VehicleService vehicleService) {
        this.maintenanceService = maintenanceService;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("records", maintenanceService.listAll());
        return "fleet/list";
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
                         @ModelAttribute("record") MaintenanceRecord record,
                         @RequestParam(defaultValue = "false") boolean force,
                         Model model,
                         RedirectAttributes redirectAttributes) {
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
                         @ModelAttribute("record") MaintenanceRecord record,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        try {
            maintenanceService.update(id, record);
            redirectAttributes.addFlashAttribute("message", "Maintenance record updated");
            return "redirect:/maintenance/" + id;
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("record", maintenanceService.getById(id));
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
}
