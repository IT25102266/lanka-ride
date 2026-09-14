package com.lankaride.booking;

import com.lankaride.auth.UserAccount;
import com.lankaride.common.BookingStatus;
import com.lankaride.common.PaymentStatus;
import com.lankaride.vehicle.Branch;
import com.lankaride.vehicle.Vehicle;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private UserAccount customer;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "pickup_branch_id", nullable = false)
    private Branch pickupBranch;

    @Column(nullable = false)
    private LocalDate pickupDate;

    @Column(nullable = false)
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Column(length = 500)
    private String decisionReason;

    @Column(length = 80)
    private String decidedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Integer pickupMileage;

    private Integer returnMileage;

    @Column(length = 20)
    private String pickupFuelLevel;

    @Column(length = 20)
    private String returnFuelLevel;

    @Column(precision = 12, scale = 2)
    private java.math.BigDecimal lateFeeAmount;

    @Column(precision = 12, scale = 2)
    private java.math.BigDecimal damageChargeAmount;

    private LocalDateTime returnedAt;

    private String invoiceNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getCustomer() {
        return customer;
    }

    public void setCustomer(UserAccount customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Branch getPickupBranch() {
        return pickupBranch;
    }

    public void setPickupBranch(Branch pickupBranch) {
        this.pickupBranch = pickupBranch;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }

    public String getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(String decidedBy) {
        this.decidedBy = decidedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getPickupMileage() {
        return pickupMileage;
    }

    public void setPickupMileage(Integer pickupMileage) {
        this.pickupMileage = pickupMileage;
    }

    public Integer getReturnMileage() {
        return returnMileage;
    }

    public void setReturnMileage(Integer returnMileage) {
        this.returnMileage = returnMileage;
    }

    public String getPickupFuelLevel() {
        return pickupFuelLevel;
    }

    public void setPickupFuelLevel(String pickupFuelLevel) {
        this.pickupFuelLevel = pickupFuelLevel;
    }

    public String getReturnFuelLevel() {
        return returnFuelLevel;
    }

    public void setReturnFuelLevel(String returnFuelLevel) {
        this.returnFuelLevel = returnFuelLevel;
    }

    public java.math.BigDecimal getLateFeeAmount() {
        return lateFeeAmount;
    }

    public void setLateFeeAmount(java.math.BigDecimal lateFeeAmount) {
        this.lateFeeAmount = lateFeeAmount;
    }

    public java.math.BigDecimal getDamageChargeAmount() {
        return damageChargeAmount;
    }

    public void setDamageChargeAmount(java.math.BigDecimal damageChargeAmount) {
        this.damageChargeAmount = damageChargeAmount;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
