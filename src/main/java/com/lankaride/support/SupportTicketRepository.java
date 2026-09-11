package com.lankaride.support;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByCustomerUsernameOrderByCreatedAtDesc(String username);
    List<SupportTicket> findAllByOrderByCreatedAtDesc();
}
