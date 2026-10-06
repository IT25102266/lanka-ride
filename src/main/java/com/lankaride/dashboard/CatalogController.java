package com.lankaride.dashboard;

import com.lankaride.common.FuelType;
import com.lankaride.common.GearboxType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard/catalog")
@PreAuthorize("hasAnyRole('ADMIN','OPERATIONS_MANAGER')")
public class CatalogController {

    private final FleetDefaultService fleetDefaultService;

    public CatalogController(FleetDefaultService fleetDefaultService) {
        this.fleetDefaultService = fleetDefaultService;
    }

    @GetMapping
    public String page(@RequestParam(required = false) Long edit, Model model) {
        model.addAttribute("categories", fleetDefaultService.list(DefaultKind.CATEGORY));
        model.addAttribute("models", fleetDefaultService.list(DefaultKind.MODEL));
        model.addAttribute("serviceTypes", fleetDefaultService.list(DefaultKind.SERVICE_TYPE));
        model.addAttribute("gearboxes", GearboxType.values());
        model.addAttribute("fuelTypes", FuelType.values());
        if (edit != null) {
            fleetDefaultService.list(DefaultKind.CATEGORY).stream()
                    .filter(item -> item.getId().equals(edit))
                    .findFirst()
                    .ifPresent(item -> model.addAttribute("editing", item));
            if (!model.containsAttribute("editing")) {
                fleetDefaultService.list(DefaultKind.MODEL).stream()
                        .filter(item -> item.getId().equals(edit))
                        .findFirst()
                        .ifPresent(item -> model.addAttribute("editing", item));
            }
            if (!model.containsAttribute("editing")) {
                fleetDefaultService.list(DefaultKind.SERVICE_TYPE).stream()
                        .filter(item -> item.getId().equals(edit))
                        .findFirst()
                        .ifPresent(item -> model.addAttribute("editing", item));
            }
        }
        return "dashboard/catalog";
    }

    @PostMapping
    public String create(@RequestParam DefaultKind kind,
                         @RequestParam String name,
                         @RequestParam(required = false) String brand,
                         @RequestParam(required = false) String categoryName,
                         @RequestParam(required = false) Integer seats,
                         @RequestParam(required = false) GearboxType gearbox,
                         @RequestParam(required = false) FuelType fuelType,
                         RedirectAttributes redirectAttributes) {
        try {
            fleetDefaultService.create(kind, name, brand, categoryName, seats, gearbox, fuelType);
            redirectAttributes.addFlashAttribute("message", "Default saved");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/dashboard/catalog";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam DefaultKind kind,
                         @RequestParam String name,
                         @RequestParam(required = false) String brand,
                         @RequestParam(required = false) String categoryName,
                         @RequestParam(required = false) Integer seats,
                         @RequestParam(required = false) GearboxType gearbox,
                         @RequestParam(required = false) FuelType fuelType,
                         RedirectAttributes redirectAttributes) {
        try {
            fleetDefaultService.update(id, kind, name, brand, categoryName, seats, gearbox, fuelType);
            redirectAttributes.addFlashAttribute("message", "Default updated");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/dashboard/catalog";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            fleetDefaultService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Default deleted. Vehicles already saved keep their own details.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/dashboard/catalog";
    }
}
