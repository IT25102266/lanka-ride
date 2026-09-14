package com.lankaride.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Set;

@Component
public class RoleBasedAuthSuccessHandler implements AuthenticationSuccessHandler {

    private static final Set<String> STAFF_ROLES = Set.of(
            "ROLE_ADMIN",
            "ROLE_BOOKING_SUPERVISOR",
            "ROLE_FLEET_COORDINATOR",
            "ROLE_FINANCE_MANAGER",
            "ROLE_OPERATIONS_MANAGER"
    );

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        boolean staff = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(STAFF_ROLES::contains);
        String target = staff ? "/dashboard" : "/app";
        response.sendRedirect(request.getContextPath() + target);
    }
}
