package com.lankaride.dashboard;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FleetDefaultRepository extends JpaRepository<FleetDefault, Long> {

    List<FleetDefault> findByKindOrderByNameAsc(DefaultKind kind);
}
