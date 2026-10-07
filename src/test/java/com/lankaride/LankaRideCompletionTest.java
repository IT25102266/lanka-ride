package com.lankaride;

import com.lankaride.booking.Booking;
import com.lankaride.booking.BookingService;
import com.lankaride.common.BookingStatus;
import com.lankaride.common.VehicleStatus;
import com.lankaride.fleet.MaintenanceService;
import com.lankaride.common.FuelType;
import com.lankaride.common.GearboxType;
import com.lankaride.dashboard.DefaultKind;
import com.lankaride.dashboard.FleetDefaultService;
import com.lankaride.payment.DummyGateway;
import com.lankaride.payment.PaymentGateway;
import com.lankaride.payment.PaymentService;
import com.lankaride.payment.PaymentTransaction;
import com.lankaride.support.NotificationLog;
import com.lankaride.support.NotificationLogRepository;
import com.lankaride.support.NotificationService;
import com.lankaride.vehicle.BranchRepository;
import com.lankaride.vehicle.Vehicle;
import com.lankaride.vehicle.VehicleRepository;
import com.lankaride.vehicle.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LankaRideCompletionTest {

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private MaintenanceService maintenanceService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationLogRepository notificationLogRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DummyGateway dummyGateway;
    @Autowired
    private FleetDefaultService fleetDefaultService;

    @Test
    void branchTransferWritesTimestampedLog() {
        Vehicle vehicle = vehicleRepository.findByRegistrationNumber("CAB-1001").orElseThrow();
        Long kandyId = branchRepository.findByName("Kandy").orElseThrow().getId();

        vehicleService.transfer(vehicle.getId(), kandyId, "fleet", "Depot move");

        Vehicle moved = vehicleService.getById(vehicle.getId());
        assertEquals("Kandy", moved.getBranch().getName());
        assertEquals("Kandy", moved.getCurrentLocation());
        assertEquals(1, vehicleService.listTransfers(vehicle.getId()).size());
        assertEquals("fleet", vehicleService.listTransfers(vehicle.getId()).get(0).getTransferredBy());
    }

    @Test
    void returnDiscrepancyOpensInspectionAndAuditTrail() {
        Vehicle vehicle = vehicleRepository.findByRegistrationNumber("CAB-2002").orElseThrow();
        Long branchId = vehicle.getBranch().getId();
        Booking created = bookingService.create(
                "customer", vehicle.getId(), branchId, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        bookingService.approve(created.getId(), "supervisor", "ok");
        paymentService.payApprovedBooking(created.getId(), "customer", false);
        paymentService.recordPickup(created.getId(), 10000, "FULL");
        Booking returned = paymentService.completeReturn(
                created.getId(), 10200, "EMPTY", BigDecimal.ZERO, BigDecimal.ZERO, "fleet");

        assertEquals(BookingStatus.COMPLETED, returned.getStatus());
        assertTrue(returned.isDiscrepancyFlag());
        assertTrue(returned.getDiscrepancyNote().contains("Fuel drop"));
        assertEquals(VehicleStatus.MAINTENANCE, vehicleRepository.findById(vehicle.getId()).orElseThrow().getStatus());

        boolean linked = maintenanceService.listByVehicle(vehicle.getId()).stream()
                .anyMatch(record -> created.getId().equals(record.getSourceBookingId()));
        assertTrue(linked);

        for (PaymentTransaction tx : paymentService.listForBooking(created.getId())) {
            assertEquals("customer", tx.getActorUsername());
        }
        assertTrue(maintenanceService.sendDueReminders() >= 1);
    }

    @Test
    void cleanReturnDoesNotFlagDiscrepancy() {
        Vehicle vehicle = vehicleRepository.findByRegistrationNumber("CAB-3003").orElseThrow();
        Booking created = bookingService.create(
                "customer", vehicle.getId(), vehicle.getBranch().getId(),
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(1));
        bookingService.approve(created.getId(), "supervisor", "ok");
        paymentService.payApprovedBooking(created.getId(), "customer", false);
        paymentService.recordPickup(created.getId(), 5000, "FULL");
        Booking returned = paymentService.completeReturn(
                created.getId(), 5200, "3/4", BigDecimal.ZERO, BigDecimal.ZERO, "fleet");

        assertFalse(returned.isDiscrepancyFlag());
        assertEquals(VehicleStatus.AVAILABLE, vehicleRepository.findById(vehicle.getId()).orElseThrow().getStatus());
    }

    @Test
    void failedNotificationCanBeRetried() {
        NotificationLog failed = new NotificationLog();
        failed.setChannel("EMAIL");
        failed.setRecipient("fleet@lankaride.lk");
        failed.setSubject("Queued reminder");
        failed.setBody("Retry me");
        failed.setDeliveryStatus("FAILED");
        notificationLogRepository.save(failed);

        assertEquals(1, notificationService.retryFailed());
        NotificationLog saved = notificationLogRepository.findById(failed.getId()).orElseThrow();
        assertEquals("SENT", saved.getDeliveryStatus());
        assertEquals(1, saved.getRetryCount());
    }

    @Test
    @WithMockUser(username = "operations", roles = "OPERATIONS_MANAGER")
    void operationsMonitorAndReportExportAreAvailable() throws Exception {
        mockMvc.perform(get("/bookings/monitor"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/monitor"));

        mockMvc.perform(get("/reports/export").with(user("finance").roles("FINANCE_MANAGER")))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("lanka-ride-report.csv")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("summary")));
    }

    @Test
    void overlappingDatesAndInvalidVehicleFieldsAreRejected() {
        Vehicle vehicle = vehicleRepository.findByRegistrationNumber("CAB-3003").orElseThrow();
        Long branchId = vehicle.getBranch().getId();
        LocalDate start = LocalDate.now().plusDays(40);
        LocalDate end = start.plusDays(2);
        bookingService.create("customer", vehicle.getId(), branchId, start, end);

        IllegalArgumentException overlap = assertThrows(IllegalArgumentException.class, () ->
                bookingService.create("customer", vehicle.getId(), branchId, start.plusDays(1), end.plusDays(1)));
        assertTrue(overlap.getMessage().contains("already booked"));

        Vehicle bad = new Vehicle();
        bad.setRegistrationNumber("TEMP-1");
        bad.setCategory("Sedan");
        bad.setBrand("!");
        bad.setModel("Swift");
        bad.setSeats(5);
        bad.setPricePerDay(new BigDecimal("1000.00"));
        bad.setDepositAmount(new BigDecimal("1000.00"));
        assertThrows(IllegalArgumentException.class, () -> vehicleService.create(bad, branchId));
    }

    @Test
    void permanentDeleteRemovesTheVehicleRow() {
        Vehicle vehicle = vehicleRepository.findByRegistrationNumber("CAB-1001").orElseThrow();
        Long branchId = vehicle.getBranch().getId();
        Vehicle extra = new Vehicle();
        extra.setRegistrationNumber("DEL-9001");
        extra.setCategory("Sedan");
        extra.setBrand("Test");
        extra.setModel("Delete");
        extra.setSeats(4);
        extra.setPricePerDay(new BigDecimal("5000.00"));
        extra.setDepositAmount(new BigDecimal("8000.00"));
        extra.setStatus(VehicleStatus.AVAILABLE);
        Vehicle saved = vehicleService.create(extra, branchId);

        vehicleService.deletePermanently(saved.getId());

        assertTrue(vehicleRepository.findByRegistrationNumber("DEL-9001").isEmpty());
    }

    @Test
    void lankaPayAcceptsAVisaCardAndDeclinesTheSandboxCard() throws Exception {
        PaymentGateway.GatewayDecision approved = dummyGateway.charge(
                "VISA", "4242 4242 4242 4242", "Demo Customer", "12/30", "123");
        assertFalse(approved.declined());
        assertEquals("4242", approved.last4());

        PaymentGateway.GatewayDecision declined = dummyGateway.charge(
                "VISA", "4000000000000002", "Demo Customer", "12/30", "123");
        assertTrue(declined.declined());
        assertThrows(IllegalArgumentException.class, () ->
                dummyGateway.charge("MASTERCARD", "4242424242424242", "Demo Customer", "12/30", "123"));

        Vehicle vehicle = vehicleRepository.findByRegistrationNumber("CAB-1001").orElseThrow();
        Booking created = bookingService.create(
                "customer", vehicle.getId(), vehicle.getBranch().getId(),
                LocalDate.now().plusDays(12), LocalDate.now().plusDays(13));
        bookingService.approve(created.getId(), "supervisor", "ok");

        mockMvc.perform(get("/payments/booking/" + created.getId() + "/checkout")
                        .with(user("customer").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(view().name("payment/gateway"));

        mockMvc.perform(post("/payments/booking/" + created.getId() + "/checkout")
                        .with(user("customer").roles("CUSTOMER"))
                        .with(csrf())
                        .param("method", "VISA")
                        .param("cardNumber", "4242424242424242")
                        .param("holder", "Demo Customer")
                        .param("expiry", "12/30")
                        .param("cvv", "123"))
                .andExpect(status().is3xxRedirection());

        assertEquals(com.lankaride.common.PaymentStatus.PAID,
                bookingService.getById(created.getId()).getPaymentStatus());
    }

    @Test
    void operationsCanCreateAndDeleteFleetDefaults() throws Exception {
        var saved = fleetDefaultService.create(
                DefaultKind.MODEL, "Wagon", "Toyota", "Sedan", 5, GearboxType.AUTOMATIC, FuelType.PETROL);
        assertEquals("Wagon", fleetDefaultService.list(DefaultKind.MODEL).stream()
                .filter(item -> item.getId().equals(saved.getId()))
                .findFirst().orElseThrow().getName());

        mockMvc.perform(get("/dashboard/catalog").with(user("operations").roles("OPERATIONS_MANAGER")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/catalog"));
        mockMvc.perform(get("/dashboard/catalog").with(user("finance").roles("FINANCE_MANAGER")))
                .andExpect(status().isForbidden());

        fleetDefaultService.delete(saved.getId());
        assertTrue(fleetDefaultService.list(DefaultKind.MODEL).stream()
                .noneMatch(item -> "Wagon".equals(item.getName())));
    }

    @Test
    void customerCannotOpenOperationsMonitor() throws Exception {
        mockMvc.perform(get("/bookings/monitor").with(user("customer").roles("CUSTOMER")))
                .andExpect(status().isForbidden());
    }
}
