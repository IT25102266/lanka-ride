<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="pageTitle" value="${pendingOnly ? 'Pending bookings' : 'Bookings'}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
    <div>
        <h1 class="page-title">${pendingOnly ? 'Pending approvals' : 'Bookings'}</h1>
        <p class="page-lead">
            <c:choose>
                <c:when test="${staff}">Review reservation requests and open approval screens.</c:when>
                <c:otherwise>Your reservation requests — pay when approved, then pick up at the branch.</c:otherwise>
            </c:choose>
        </p>
    </div>
    <div class="d-flex flex-wrap gap-2">
        <sec:authorize access="hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')">
            <a class="btn btn-outline-primary btn-sm ${pendingOnly ? 'active' : ''}" href="<c:url value='/bookings/pending'/>">Pending</a>
            <a class="btn btn-outline-secondary btn-sm ${empty pendingOnly ? 'active' : ''}" href="<c:url value='/bookings'/>">All</a>
        </sec:authorize>
        <a class="btn btn-primary btn-sm" href="<c:url value='/bookings/new'/>">New booking</a>
    </div>
</div>

<c:choose>
    <c:when test="${empty bookings}">
        <div class="panel">
            <div class="empty-state">No bookings found.</div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="item-stack">
            <c:forEach items="${bookings}" var="b">
                <c:set var="st" value="${fn:toLowerCase(b.status.name())}"/>
                <c:set var="ps" value="${fn:toLowerCase(fn:replace(b.paymentStatus.name(), '_', '-'))}"/>
                <article class="item-card">
                    <div>
                        <div class="item-kicker">#${b.id} · ${b.pickupBranch.name}</div>
                        <h3 class="item-title">${b.vehicle.brand} ${b.vehicle.model}</h3>
                        <div class="item-meta">
                            <span><i class="bi bi-calendar3"></i> ${b.pickupDate} → ${b.returnDate}</span>
                            <span><i class="bi bi-car-front"></i> ${b.vehicle.registrationNumber}</span>
                            <c:if test="${staff}">
                                <span><i class="bi bi-person"></i> ${b.customer.fullName}</span>
                            </c:if>
                        </div>
                    </div>
                    <div>
                        <div class="item-badges">
                            <span class="status-pill status-${st}">${b.status}</span>
                            <span class="status-pill status-${ps}">${b.paymentStatus}</span>
                        </div>
                        <div class="item-actions">
                            <a class="btn btn-sm btn-outline-primary" href="<c:url value='/bookings/${b.id}'/>">
                                <c:choose>
                                    <c:when test="${staff && b.status.name() == 'PENDING'}">Review</c:when>
                                    <c:otherwise>Open</c:otherwise>
                                </c:choose>
                            </a>
                            <c:if test="${b.status.name() == 'APPROVED' && b.paymentStatus.name() == 'PENDING_PAYMENT'}">
                                <a class="pay-cta btn-sm py-1 px-3" href="<c:url value='/payments/booking/${b.id}'/>">Pay</a>
                            </c:if>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
