<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="pageTitle" value="Invoice booking #${booking.id}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="mb-3">
    <a class="small text-decoration-none" href="<c:url value='/bookings/${booking.id}'/>">&larr; Back to booking</a>
</div>

<c:set var="st" value="${fn:toLowerCase(booking.status.name())}"/>
<c:set var="ps" value="${fn:toLowerCase(fn:replace(booking.paymentStatus.name(), '_', '-'))}"/>

<section class="detail-hero">
    <div class="d-flex flex-wrap justify-content-between gap-3 align-items-start">
        <div>
            <p class="muted small mb-1 text-uppercase" style="letter-spacing:.08em;">
                <c:out value="${booking.invoiceNumber}" default="Invoice pending"/>
            </p>
            <h1>Booking #${booking.id}</h1>
            <p class="muted mb-0">${booking.vehicle.brand} ${booking.vehicle.model} · ${booking.pickupDate} → ${booking.returnDate}</p>
        </div>
        <div class="d-flex flex-wrap gap-2">
            <span class="status-pill status-${st}">${booking.status}</span>
            <span class="status-pill status-${ps}">${booking.paymentStatus}</span>
        </div>
    </div>
</section>

<div class="row g-4">
    <div class="col-lg-7">
        <div class="detail-block">
            <h2>Invoice summary</h2>
            <dl class="detail-grid">
                <dt>Customer</dt>
                <dd>${booking.customer.fullName} (${booking.customer.email})</dd>
                <dt>Vehicle</dt>
                <dd>${booking.vehicle.registrationNumber} — ${booking.vehicle.brand} ${booking.vehicle.model}</dd>
                <dt>Dates</dt>
                <dd>${booking.pickupDate} → ${booking.returnDate}</dd>
                <dt>Deposit</dt>
                <dd>LKR ${booking.vehicle.depositAmount}</dd>
                <dt>Rental estimate</dt>
                <dd>LKR ${rentalAmount}</dd>
                <dt>Total due (pre-pay)</dt>
                <dd class="fw-semibold">LKR ${totalDue}</dd>
            </dl>
        </div>

        <div class="detail-block">
            <h2>Transactions</h2>
            <c:choose>
                <c:when test="${empty payments}">
                    <p class="text-muted mb-0 small">No transactions yet.</p>
                </c:when>
                <c:otherwise>
                    <div class="item-stack">
                        <c:forEach items="${payments}" var="p">
                            <div class="rail-item d-flex justify-content-between gap-2">
                                <div>
                                    <div class="fw-semibold">${p.type} · LKR ${p.amount}</div>
                                    <div class="small text-muted">${p.gatewayReference}
                                        <c:if test="${not empty p.note}"> · ${p.note}</c:if>
                                    </div>
                                </div>
                                <span class="status-pill ${p.success ? 'status-paid' : 'status-denied'}">
                                    ${p.success ? 'OK' : 'FAIL'}
                                </span>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="col-lg-5">
        <c:if test="${booking.status.name() == 'APPROVED' && booking.paymentStatus.name() == 'PENDING_PAYMENT'}">
            <div class="detail-block border border-warning-subtle">
                <h2>Pay now (sandbox)</h2>
                <p class="page-lead mb-3">Approved — pay deposit and rental to continue.</p>
                <form method="post" action="<c:url value='/payments/booking/${booking.id}/pay'/>" class="mb-2">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="pay-cta w-100 justify-content-center">
                        <i class="bi bi-credit-card"></i> Pay deposit + rental
                    </button>
                </form>
                <form method="post" action="<c:url value='/payments/booking/${booking.id}/pay'/>">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="hidden" name="fail" value="true"/>
                    <button type="submit" class="btn btn-outline-danger btn-sm w-100">Simulate gateway fail</button>
                </form>
            </div>
        </c:if>

        <c:if test="${booking.paymentStatus.name() == 'PAID' && booking.status.name() == 'ONGOING' && empty booking.pickupMileage}">
            <div class="alert alert-info py-2 small">Paid — awaiting pickup mileage/fuel checklist.</div>
        </c:if>

        <sec:authorize access="hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER','FLEET_COORDINATOR')">
            <c:if test="${booking.paymentStatus.name() == 'PAID' && booking.status.name() == 'ONGOING' && empty booking.pickupMileage}">
                <div class="detail-block">
                    <h2>Record pickup</h2>
                    <form method="post" action="<c:url value='/payments/booking/${booking.id}/pickup'/>">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <div class="mb-2">
                            <label class="form-label small">Mileage</label>
                            <input class="form-control form-control-sm" type="number" name="pickupMileage" required/>
                        </div>
                        <div class="mb-2">
                            <label class="form-label small">Fuel</label>
                            <select class="form-select form-select-sm" name="pickupFuelLevel">
                                <option>FULL</option><option>3/4</option><option>1/2</option><option>1/4</option><option>EMPTY</option>
                            </select>
                        </div>
                        <button class="btn btn-outline-primary btn-sm" type="submit">Save pickup</button>
                    </form>
                </div>
            </c:if>

            <c:if test="${booking.status.name() == 'ONGOING'}">
                <div class="detail-block">
                    <h2>Complete return</h2>
                    <form method="post" action="<c:url value='/payments/booking/${booking.id}/return'/>">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <div class="mb-2">
                            <label class="form-label small">Return mileage</label>
                            <input class="form-control form-control-sm" type="number" name="returnMileage" required/>
                        </div>
                        <div class="mb-2">
                            <label class="form-label small">Return fuel</label>
                            <select class="form-select form-select-sm" name="returnFuelLevel">
                                <option>FULL</option><option>3/4</option><option>1/2</option><option>1/4</option><option>EMPTY</option>
                            </select>
                        </div>
                        <div class="mb-2">
                            <label class="form-label small">Late fee (LKR)</label>
                            <input class="form-control form-control-sm" type="number" step="0.01" name="lateFee" value="0"/>
                        </div>
                        <div class="mb-2">
                            <label class="form-label small">Damage charge (LKR)</label>
                            <input class="form-control form-control-sm" type="number" step="0.01" name="damageCharge" value="0"/>
                        </div>
                        <button class="btn btn-success btn-sm" type="submit">Complete return</button>
                    </form>
                </div>
            </c:if>
        </sec:authorize>

        <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER')">
            <c:if test="${booking.paymentStatus.name() == 'PAID'}">
                <div class="detail-block">
                    <h2>Refund</h2>
                    <form method="post" action="<c:url value='/payments/booking/${booking.id}/refund'/>">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <div class="mb-2">
                            <label class="form-label small">Reason</label>
                            <input class="form-control form-control-sm" name="reason" required/>
                        </div>
                        <button class="btn btn-outline-danger btn-sm" type="submit">Process refund</button>
                    </form>
                </div>
            </c:if>
        </sec:authorize>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
