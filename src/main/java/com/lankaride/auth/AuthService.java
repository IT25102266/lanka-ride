package com.lankaride.auth;

import com.lankaride.common.RoleName;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository userAccountRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount registerCustomer(String username, String email, String fullName, String rawPassword) {
        if (userAccountRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userAccountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseThrow(() -> new IllegalStateException("CUSTOMER role missing"));

        UserAccount user = new UserAccount();
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setFullName(fullName.trim());
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.getRoles().add(customerRole);
        return userAccountRepository.save(user);
    }
}
