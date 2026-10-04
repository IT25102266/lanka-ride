<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Operations monitor" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
    <div>
        <h1 class="page-title">Operations monitor</h1>
        <p class="page-lead">Read-only booking board. ${flagged} return discrepancy flag(s).</p>
    </div>
    <a class="btn btn-outline-secondary btn-sm" href="<c:url value='/bookings'/>">Booking list</a>
</div>

<c:choose>
    <c:when test="${empty bookings}">
        <div class="panel"><div class="empty-state">No bookings yet.</div></div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive panel">
            <table class="table align-middle mb-0">
                <thead>
                <tr>
                    <th>Booking</th>
                    <th>Customer</th>
                    <th>Vehicle</th>
                    <th>Status</th>
                    <th>Payment</th>
                    <th>Return check</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${bookings}" var="b">
                    <tr>
                        <td><a href="<c:url value='/bookings/${b.id}'/>">#${b.id}</a><br>
                            <span class="small text-muted">${b.pickupDate} → ${b.returnDate}</span></td>
                        <td>${b.customer.fullName}</td>
                        <td>${b.vehicle.registrationNumber}<br>
                            <span class="small text-muted">${b.pickupBranch.name}</span></td>
                        <td>${b.status}</td>
                        <td>${b.paymentStatus}</td>
                        <td>
                            <c:choose>
                                <c:when test="${b.discrepancyFlag}">
                                    <span class="badge text-bg-danger">Flagged</span>
                                    <div class="small">${b.discrepancyNote}</div>
                                </c:when>
                                <c:when test="${b.status.name() == 'COMPLETED'}">
                                    <span class="badge text-bg-success">Clear</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted small">Not returned</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
