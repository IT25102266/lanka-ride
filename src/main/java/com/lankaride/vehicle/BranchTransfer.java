package com.lankaride.vehicle;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "branch_transfers")
public class BranchTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "from_branch_id", nullable = false)
    private Branch fromBranch;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "to_branch_id", nullable = false)
    private Branch toBranch;

    @Column(length = 100)
    private String fromLocation;

    @Column(nullable = false)
    private LocalDateTime transferredAt = LocalDateTime.now();

    @Column(nullable = false, length = 80)
    private String transferredBy;

    @Column(length = 300)
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Branch getFromBranch() {
        return fromBranch;
    }

    public void setFromBranch(Branch fromBranch) {
        this.fromBranch = fromBranch;
    }

    public Branch getToBranch() {
        return toBranch;
    }

    public void setToBranch(Branch toBranch) {
        this.toBranch = toBranch;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public LocalDateTime getTransferredAt() {
        return transferredAt;
    }

    public void setTransferredAt(LocalDateTime transferredAt) {
        this.transferredAt = transferredAt;
    }

    public String getTransferredBy() {
        return transferredBy;
    }

    public void setTransferredBy(String transferredBy) {
        this.transferredBy = transferredBy;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
