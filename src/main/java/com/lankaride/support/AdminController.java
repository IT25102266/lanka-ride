package com.lankaride.support;

import com.lankaride.auth.RoleRepository;
import com.lankaride.auth.UserAccount;
import com.lankaride.auth.UserAccountRepository;
import com.lankaride.common.RoleName;
import com.lankaride.vehicle.Branch;
import com.lankaride.vehicle.BranchRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final BranchRepository branchRepository;
    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(BranchRepository branchRepository,
                           UserAccountRepository userAccountRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.branchRepository = branchRepository;
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String adminHome(Model model) {
        model.addAttribute("branches", branchRepository.findAll());
        model.addAttribute("users", userAccountRepository.findAll());
        model.addAttribute("roles", RoleName.values());
        return "support/admin";
    }

    @PostMapping("/branches")
    public String addBranch(@RequestParam String name,
                            @RequestParam String address,
                            RedirectAttributes redirectAttributes) {
        if (branchRepository.findByName(name.trim()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Branch already exists");
            return "redirect:/admin";
        }
        branchRepository.save(new Branch(name.trim(), address.trim()));
        redirectAttributes.addFlashAttribute("message", "Branch added");
        return "redirect:/admin";
    }

    @PostMapping("/users")
    public String addUser(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String fullName,
                          @RequestParam String password,
                          @RequestParam RoleName role,
                          RedirectAttributes redirectAttributes) {
        if (userAccountRepository.existsByUsername(username) || userAccountRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Username or email already exists");
            return "redirect:/admin";
        }
        UserAccount user = new UserAccount();
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setFullName(fullName.trim());
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRoles(Set.of(roleRepository.findByName(role).orElseThrow()));
        userAccountRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "User created");
        return "redirect:/admin";
    }
}
