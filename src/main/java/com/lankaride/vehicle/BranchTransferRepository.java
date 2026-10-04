package com.lankaride.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BranchTransferRepository extends JpaRepository<BranchTransfer, Long> {
    List<BranchTransfer> findByVehicleIdOrderByTransferredAtDesc(Long vehicleId);

    void deleteByVehicleId(Long vehicleId);
}
