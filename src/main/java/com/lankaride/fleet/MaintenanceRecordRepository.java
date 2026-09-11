package com.lankaride.fleet;

import com.lankaride.common.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    List<MaintenanceRecord> findByVehicleIdOrderByServiceDateDesc(Long vehicleId);

    List<MaintenanceRecord> findAllByOrderByServiceDateDesc();

    long countByVehicleIdAndStatusIn(Long vehicleId, List<MaintenanceStatus> statuses);
}
