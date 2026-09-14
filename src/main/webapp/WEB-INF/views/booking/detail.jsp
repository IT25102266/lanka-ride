<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="pageTitle" value="Booking #${booking.id}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="mb-3">
    <a class="small text-decoration-none" href="<c:url value='/bookings'/>">&larr; Back to bookings</a>
</div>

<c:set var="st" value="${fn:toLowerCase(booking.status.name())}"/>
<c:set var="ps" value="${fn:toLowerCase(fn:replace(booking.paymentStatus.name(), '_', '-'))}"/>

<section class="detail-hero">
    <div class="d-flex flex-wrap justify-content-between gap-3 align-items-start">
        <div>
            <p class="muted small mb-1 text-uppercase" style="letter-spacing:.08em;">Booking #${booking.id}</p>
            <h1>${booking.vehicle.brand} ${booking.vehicle.model}</h1>
            <p class="muted mb-0">${booking.pickupDate} → ${booking.returnDate} · ${booking.pickupBranch.name} · approval &amp; payment</p>
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
            <h2>Vehicle</h2>
            <dl class="detail-grid">
                <dt>Registration</dt>
                <dd>
                    <a href="<c:url value='/vehicles/${booking.vehicle.id}'/>">${booking.vehicle.registrationNumber}</a>
                </dd>
                <dt>Fleet status</dt>
                <dd>${booking.vehicle.status}</dd>
                <dt>Location</dt>
                <dd>${booking.vehicle.currentLocation}</dd>
                <dt>Rate / deposit</dt>
                <dd>LKR ${booking.vehicle.pricePerDay} / LKR ${booking.vehicle.depositAmount}</dd>
            </dl>
        </div>

        <div class="detail-block">
            <h2>Customer</h2>
            <dl class="detail-grid">
                <dt>Name</dt>
                <dd>${booking.customer.fullName}</dd>
                <dt>Account</dt>
                <dd>${booking.customer.username} · ${booking.customer.email}</dd>
            </dl>
        </div>

        <div class="detail-block">
            <h2>Reservation</h2>
            <dl class="detail-grid">
                <dt>Pickup branch</dt>
                <dd>${booking.pickupBranch.name}</dd>
                <dt>Dates</dt>
                <dd>${booking.pickupDate} → ${booking.returnDate}</dd>
                <dt>Requested</dt>
                <dd>${booking.createdAt}</dd>
                <c:if test="${not empty booking.decisionReason}">
                    <dt>Decision note</dt>
                    <dd>${booking.decisionReason}
                        <c:if test="${not empty booking.decidedBy}">
                            <span class="text-muted small">(by ${booking.decidedBy})</span>
                        </c:if>
                    </dd>
                </c:if>
                <c:if test="${not empty booking.pickupMileage}">
                    <dt>Pickup</dt>
                    <dd>${booking.pickupMileage} km · fuel ${booking.pickupFuelLevel}</dd>
                </c:if>
                <c:if test="${not empty booking.returnMileage}">
                    <dt>Return</dt>
                    <dd>${booking.returnMileage} km · fuel ${booking.returnFuelLevel}
                        · late LKR ${booking.lateFeeAmount} · damage LKR ${booking.damageChargeAmount}</dd>
                </c:if>
            </dl>

            <div class="d-flex flex-wrap gap-2 mt-3">
                <a class="btn btn-outline-primary btn-sm" href="<c:url value='/payments/booking/${booking.id}'/>">Payments / invoice</a>
                <c:if test="${booking.status.name() == 'APPROVED' && booking.paymentStatus.name() == 'PENDING_PAYMENT'}">
                    <a class="pay-cta" href="<c:url value='/payments/booking/${booking.id}'/>">
                        <i class="bi bi-credit-card"></i> Pay deposit &amp; rental
                    </a>
                </c:if>
                <c:if test="${booking.status.name() == 'ONGOING' && booking.paymentStatus.name() == 'PAID' && empty booking.pickupMileage}">
                    <span class="status-pill status-ongoing">Paid — awaiting pickup</span>
                </c:if>
                <c:if test="${booking.status.name() == 'ONGOING' && not empty booking.pickupMileage && empty booking.returnMileage}">
                    <span class="status-pill status-ongoing">On trip — return pending</span>
                </c:if>
            </div>
        </div>

        <c:if test="${staff && booking.status.name() == 'PENDING'}">
            <div class="detail-block border border-warning-subtle">
                <h2>Decide</h2>
                <form method="post" action="<c:url value='/bookings/${booking.id}/approve'/>" class="mb-3">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="mb-2">
                        <label class="form-label small" for="note">Optional note</label>
                        <input class="form-control form-control-sm" id="note" name="note"/>
                    </div>
                    <button type="submit" class="btn btn-success">Approve</button>
                </form>
                <form method="post" action="<c:url value='/bookings/${booking.id}/deny'/>">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="mb-2">
                        <label class="form-label small" for="reason">Denial reason (required)</label>
                        <input class="form-control form-control-sm" id="reason" name="reason" required/>
                    </div>
                    <button type="submit" class="btn btn-outline-danger">Deny</button>
                </form>
            </div>
        </c:if>

        <c:if test="${booking.status.name() == 'PENDING' || booking.status.name() == 'APPROVED'}">
            <form method="post" action="<c:url value='/bookings/${booking.id}/cancel'/>"
                  onsubmit="return confirm('Cancel this booking?');">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn btn-outline-secondary btn-sm">Cancel booking</button>
            </form>
        </c:if>
    </div>

    <div class="col-lg-5">
        <h2 class="h5 mb-3">Customer history</h2>
        <div class="history-rail">
            <c:forEach items="${customerHistory}" var="h">
                <c:set var="hst" value="${fn:toLowerCase(h.status.name())}"/>
                <div class="rail-item d-flex justify-content-between gap-2">
                    <div>
                        <div class="fw-semibold">#${h.id} · ${h.vehicle.registrationNumber}</div>
                        <div class="small text-muted">${h.pickupDate} → ${h.returnDate}</div>
                    </div>
                    <span class="status-pill status-${hst}">${h.status}</span>
                </div>
            </c:forEach>
            <c:if test="${empty customerHistory}">
                <div class="text-muted small">No other bookings.</div>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
