package com.lankaride.vehicle;

import com.lankaride.common.FuelType;
import com.lankaride.common.GearboxType;
import com.lankaride.common.VehicleStatus;
import com.lankaride.dashboard.DefaultKind;
import com.lankaride.dashboard.FleetDefault;
import com.lankaride.dashboard.FleetDefaultService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final FleetDefaultService fleetDefaultService;

    public VehicleController(VehicleService vehicleService, FleetDefaultService fleetDefaultService) {
        this.vehicleService = vehicleService;
        this.fleetDefaultService = fleetDefaultService;
    }

    @ModelAttribute("savedModels")
    public java.util.List<FleetDefault> savedModels() {
        return fleetDefaultService.list(DefaultKind.MODEL);
    }

    @ModelAttribute("savedCategories")
    public java.util.List<FleetDefault> savedCategories() {
        return fleetDefaultService.list(DefaultKind.CATEGORY);
    }

    @GetMapping
    public String list(@RequestParam(required = false) String category,
                       @RequestParam(required = false) GearboxType gearbox,
                       @RequestParam(required = false) FuelType fuelType,
                       @RequestParam(required = false) Long branchId,
                       @RequestParam(required = false) BigDecimal minPrice,
                       @RequestParam(required = false) BigDecimal maxPrice,
                       @RequestParam(required = false) VehicleStatus status,
                       @RequestParam(required = false) LocalDate pickupDate,
                       @RequestParam(required = false) LocalDate returnDate,
                       @RequestParam(required = false, defaultValue = "false") boolean availableOnly,
                       Model model) {
        boolean dateSearch = pickupDate != null && returnDate != null;
        if (dateSearch && returnDate.isBefore(pickupDate)) {
            model.addAttribute("error", "Return date must be on or after the pickup date");
            dateSearch = false;
            pickupDate = null;
            returnDate = null;
        } else if (dateSearch && pickupDate.isBefore(LocalDate.now())) {
            model.addAttribute("error", "Pickup date cannot be in the past");
            dateSearch = false;
        }
        model.addAttribute("vehicles", vehicleService.search(
                category, gearbox, fuelType, branchId, minPrice, maxPrice, status,
                pickupDate, returnDate, availableOnly || dateSearch));
        model.addAttribute("branches", vehicleService.listBranches());
        model.addAttribute("gearboxes", GearboxType.values());
        model.addAttribute("fuelTypes", FuelType.values());
        model.addAttribute("statuses", VehicleStatus.values());
        model.addAttribute("category", category);
        model.addAttribute("gearbox", gearbox);
        model.addAttribute("fuelType", fuelType);
        model.addAttribute("branchId", branchId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("status", status);
        model.addAttribute("pickupDate", pickupDate);
        model.addAttribute("returnDate", returnDate);
        model.addAttribute("availableOnly", availableOnly || dateSearch);
        return "vehicle/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(required = false) LocalDate pickupDate,
                         @RequestParam(required = false) LocalDate returnDate,
                         @RequestParam(required = false) Long branchId,
                         Model model) {
        model.addAttribute("vehicle", vehicleService.getById(id));
        model.addAttribute("transfers", vehicleService.listTransfers(id));
        model.addAttribute("branches", vehicleService.listBranches());
        model.addAttribute("pickupDate", pickupDate);
        model.addAttribute("returnDate", returnDate);
        model.addAttribute("branchId", branchId);
        return "vehicle/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("branches", vehicleService.listBranches());
        model.addAttribute("gearboxes", GearboxType.values());
        model.addAttribute("fuelTypes", FuelType.values());
        model.addAttribute("statuses", VehicleStatus.values());
        model.addAttribute("formAction", "/vehicles");
        return "vehicle/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("vehicle") Vehicle vehicle,
                         BindingResult bindingResult,
                         @RequestParam Long branchId,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateForm(model);
            model.addAttribute("formAction", "/vehicles");
            return "vehicle/form";
        }
        try {
            Vehicle saved = vehicleService.create(vehicle, branchId);
            redirectAttributes.addFlashAttribute("message", "Vehicle created: " + saved.getRegistrationNumber());
            return "redirect:/vehicles/" + saved.getId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            populateForm(model);
            model.addAttribute("formAction", "/vehicles");
            return "vehicle/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("vehicle", vehicleService.getById(id));
        populateForm(model);
        model.addAttribute("formAction", "/vehicles/" + id);
        return "vehicle/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("vehicle") Vehicle vehicle,
                         BindingResult bindingResult,
                         @RequestParam Long branchId,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateForm(model);
            model.addAttribute("formAction", "/vehicles/" + id);
            return "vehicle/form";
        }
        try {
            vehicleService.update(id, vehicle, branchId);
            redirectAttributes.addFlashAttribute("message", "Vehicle updated");
            return "redirect:/vehicles/" + id;
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            populateForm(model);
            model.addAttribute("formAction", "/vehicles/" + id);
            return "vehicle/form";
        }
    }

    @PostMapping("/{id}/transfer")
    public String transfer(@PathVariable Long id,
                           @RequestParam Long toBranchId,
                           @RequestParam(required = false) String note,
                           Authentication auth,
                           RedirectAttributes redirectAttributes) {
        try {
            BranchTransfer saved = vehicleService.transfer(id, toBranchId, auth.getName(), note);
            redirectAttributes.addFlashAttribute("message",
                    "Transferred to " + saved.getToBranch().getName() + " at " + saved.getTransferredAt());
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/vehicles/" + id;
    }

    @PostMapping("/{id}/retire")
    public String retire(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vehicleService.retire(id);
        redirectAttributes.addFlashAttribute("message", "Vehicle retired from active fleet");
        return "redirect:/vehicles";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String registration = vehicleService.getById(id).getRegistrationNumber();
        vehicleService.deletePermanently(id);
        redirectAttributes.addFlashAttribute("message",
                registration + " and its related bookings, payments, and maintenance were permanently deleted");
        return "redirect:/vehicles";
    }

    private void populateForm(Model model) {
        model.addAttribute("branches", vehicleService.listBranches());
        model.addAttribute("gearboxes", GearboxType.values());
        model.addAttribute("fuelTypes", FuelType.values());
        model.addAttribute("statuses", VehicleStatus.values());
    }
}
