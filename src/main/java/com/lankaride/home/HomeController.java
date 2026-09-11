package com.lankaride.home;

import com.lankaride.vehicle.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDate;

@Controller
public class HomeController {

    private final VehicleService vehicleService;

    public HomeController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("branches", vehicleService.listBranches());
        model.addAttribute("pickupDate", LocalDate.now().plusDays(1));
        model.addAttribute("returnDate", LocalDate.now().plusDays(3));
        return "home/index";
    }
}
