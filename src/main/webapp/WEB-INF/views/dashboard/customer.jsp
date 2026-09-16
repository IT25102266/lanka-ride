<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="My trips" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
    <div>
        <h1 class="page-title">My trips</h1>
        <p class="page-lead">Welcome back, ${username}. Your trips hub — track bookings, pay invoices, and get support.</p>
    </div>
    <a class="btn btn-primary" href="<c:url value='/'/>"><i class="bi bi-search"></i> Find a ride</a>
</div>

<div class="row g-3 mb-4">
    <div class="col-sm-4">
        <div class="stat-mini">
            <div class="label">Active / pending</div>
            <div class="value">${activeTrips}</div>
        </div>
    </div>
    <div class="col-sm-4">
        <div class="stat-mini">
            <div class="label">Awaiting payment</div>
            <div class="value">${awaitingPayment}</div>
        </div>
    </div>
    <div class="col-sm-4">
        <div class="stat-mini">
            <div class="label">Help</div>
            <div class="d-flex flex-wrap gap-2 mt-2">
                <a class="btn btn-sm btn-outline-primary" href="<c:url value='/bookings'/>">All bookings</a>
                <a class="btn btn-sm btn-outline-primary" href="<c:url value='/support'/>">Support</a>
            </div>
        </div>
    </div>
</div>

<div class="d-flex justify-content-between align-items-baseline mb-3">
    <h2 class="h4 mb-0">Recent trips</h2>
    <a class="small" href="<c:url value='/bookings'/>">View all</a>
</div>

<c:choose>
    <c:when test="${empty bookings}">
        <div class="panel">
            <div class="empty-state">
                <p class="mb-3">No trips yet.</p>
                <a class="btn btn-primary" href="<c:url value='/'/>">Search vehicles</a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="item-stack">
            <c:forEach items="${bookings}" var="b">
                <c:set var="st" value="${fn:toLowerCase(b.status.name())}"/>
                <c:set var="ps" value="${fn:toLowerCase(fn:replace(b.paymentStatus.name(), '_', '-'))}"/>
                <article class="item-card">
                    <div>
                        <div class="item-kicker">Booking #${b.id} · ${b.pickupBranch.name}</div>
                        <h3 class="item-title">${b.vehicle.brand} ${b.vehicle.model}</h3>
                        <div class="item-meta">
                            <span><i class="bi bi-calendar3"></i> ${b.pickupDate} → ${b.returnDate}</span>
                            <span><i class="bi bi-car-front"></i> ${b.vehicle.registrationNumber}</span>
                        </div>
                    </div>
                    <div>
                        <div class="item-badges">
                            <span class="status-pill status-${st}">${b.status}</span>
                            <span class="status-pill status-${ps}">${b.paymentStatus}</span>
                        </div>
                        <div class="item-actions">
                            <a class="btn btn-sm btn-outline-primary" href="<c:url value='/bookings/${b.id}'/>">Details</a>
                            <c:if test="${b.status.name() == 'APPROVED' && b.paymentStatus.name() == 'PENDING_PAYMENT'}">
                                <a class="pay-cta btn-sm py-1 px-3" href="<c:url value='/payments/booking/${b.id}'/>">Pay now</a>
                            </c:if>
                            <c:if test="${b.paymentStatus.name() == 'PAID' || b.paymentStatus.name() == 'REFUNDED'}">
                                <a class="btn btn-sm btn-outline-secondary" href="<c:url value='/payments/booking/${b.id}'/>">Invoice</a>
                            </c:if>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
