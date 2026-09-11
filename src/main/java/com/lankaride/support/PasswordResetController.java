package com.lankaride.support;

import com.lankaride.auth.UserAccount;
import com.lankaride.auth.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class PasswordResetController {

    private final UserAccountRepository userAccountRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public PasswordResetController(UserAccountRepository userAccountRepository,
                                   PasswordResetTokenRepository passwordResetTokenRepository,
                                   PasswordEncoder passwordEncoder,
                                   NotificationService notificationService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @GetMapping("/forgot-password")
    public String forgotForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgot(@RequestParam String usernameOrEmail,
                         RedirectAttributes redirectAttributes) {
        var userOpt = userAccountRepository.findByUsername(usernameOrEmail.trim());
        if (userOpt.isEmpty()) {
            userOpt = userAccountRepository.findByEmail(usernameOrEmail.trim());
        }
        if (userOpt.isPresent()) {
            UserAccount user = userOpt.get();
            PasswordResetToken token = new PasswordResetToken();
            token.setToken(UUID.randomUUID().toString().replace("-", ""));
            token.setUser(user);
            token.setExpiresAt(LocalDateTime.now().plusHours(2));
            token.setUsed(false);
            passwordResetTokenRepository.save(token);
            notificationService.email(user.getEmail(), "Password reset",
                    "Use this reset link token in the app: " + token.getToken()
                            + " (valid 2 hours). Or open /reset-password?token=" + token.getToken());
        }
        redirectAttributes.addFlashAttribute("message",
                "If that account exists, a reset token was sent (check notification log / console).");
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetForm(@RequestParam(required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String reset(@RequestParam String token,
                        @RequestParam String password,
                        @RequestParam String confirmPassword,
                        RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/reset-password?token=" + token;
        }
        PasswordResetToken reset = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        if (reset.isUsed() || reset.getExpiresAt().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Token expired or already used");
            return "redirect:/forgot-password";
        }
        UserAccount user = reset.getUser();
        user.setPasswordHash(passwordEncoder.encode(password));
        userAccountRepository.save(user);
        reset.setUsed(true);
        passwordResetTokenRepository.save(reset);
        redirectAttributes.addFlashAttribute("message", "Password updated. Please sign in.");
        return "redirect:/login";
    }
}
