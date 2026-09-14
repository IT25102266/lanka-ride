<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="pageTitle" value="Staff dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
    <div>
        <h1 class="page-title">Operations dashboard</h1>
        <p class="page-lead">Welcome, ${username}
            <span class="status-pill status-ongoing ms-1">${roles}</span>
        </p>
    </div>
    <div class="d-flex flex-wrap gap-2">
        <a class="btn btn-outline-primary btn-sm" href="<c:url value='/vehicles'/>">Browse fleet</a>
        <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER','OPERATIONS_MANAGER')">
            <a class="btn btn-primary btn-sm" href="<c:url value='/reports'/>">Open reports</a>
        </sec:authorize>
    </div>
</div>

<div class="row g-3 mb-4">
    <div class="col-md-4 col-lg">
        <div class="stat-mini">
            <div class="label">Total vehicles</div>
            <div class="value">${totalVehicles}</div>
        </div>
    </div>
    <div class="col-md-4 col-lg">
        <div class="stat-mini">
            <div class="label">Available</div>
            <div class="value">${availableVehicles}</div>
        </div>
    </div>
    <div class="col-md-4 col-lg">
        <div class="stat-mini">
            <div class="label">In maintenance</div>
            <div class="value">${maintenanceVehicles}</div>
        </div>
    </div>
    <div class="col-md-4 col-lg">
        <div class="stat-mini">
            <div class="label">Pending bookings</div>
            <div class="value">${pendingBookings}</div>
        </div>
    </div>
    <div class="col-md-4 col-lg">
        <div class="stat-mini">
            <div class="label">Branches</div>
            <div class="value">${branchCount}</div>
        </div>
    </div>
</div>

<h2 class="h4 mb-3">Your workspace</h2>
<div class="row g-3 mb-4">
    <sec:authorize access="hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')">
        <div class="col-md-6 col-lg-4">
            <a class="item-card text-decoration-none h-100" href="<c:url value='/bookings/pending'/>" style="display:block;">
                <div class="item-kicker">Bookings</div>
                <h3 class="item-title">Pending approvals</h3>
                <div class="item-meta"><span>${pendingBookings} waiting for a decision</span></div>
            </a>
        </div>
    </sec:authorize>
    <sec:authorize access="hasAnyRole('ADMIN','FLEET_COORDINATOR','OPERATIONS_MANAGER')">
        <div class="col-md-6 col-lg-4">
            <a class="item-card text-decoration-none h-100" href="<c:url value='/vehicles/new'/>" style="display:block;">
                <div class="item-kicker">Fleet</div>
                <h3 class="item-title">Add vehicle</h3>
                <div class="item-meta"><span>Create a new fleet record</span></div>
            </a>
        </div>
    </sec:authorize>
    <sec:authorize access="hasAnyRole('ADMIN','FLEET_COORDINATOR')">
        <div class="col-md-6 col-lg-4">
            <a class="item-card text-decoration-none h-100" href="<c:url value='/maintenance'/>" style="display:block;">
                <div class="item-kicker">Fleet</div>
                <h3 class="item-title">Maintenance</h3>
                <div class="item-meta"><span>Service records and availability</span></div>
            </a>
        </div>
    </sec:authorize>
    <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')">
        <div class="col-md-6 col-lg-4">
            <a class="item-card text-decoration-none h-100" href="<c:url value='/payments'/>" style="display:block;">
                <div class="item-kicker">Finance</div>
                <h3 class="item-title">Payments ledger</h3>
                <div class="item-meta"><span>Transactions, refunds, invoices</span></div>
            </a>
        </div>
    </sec:authorize>
    <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER','OPERATIONS_MANAGER')">
        <div class="col-md-6 col-lg-4">
            <a class="item-card text-decoration-none h-100" href="<c:url value='/reports'/>" style="display:block;">
                <div class="item-kicker">Reports</div>
                <h3 class="item-title">Branch &amp; period reports</h3>
                <div class="item-meta"><span>Daily / monthly / annual + utilization</span></div>
            </a>
        </div>
    </sec:authorize>
    <sec:authorize access="hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')">
        <div class="col-md-6 col-lg-4">
            <a class="item-card text-decoration-none h-100" href="<c:url value='/support'/>" style="display:block;">
                <div class="item-kicker">Support</div>
                <h3 class="item-title">Support tickets</h3>
                <div class="item-meta"><span>Respond to customer issues</span></div>
            </a>
        </div>
    </sec:authorize>
    <sec:authorize access="hasRole('ADMIN')">
        <div class="col-md-6 col-lg-4">
            <a class="item-card text-decoration-none h-100" href="<c:url value='/admin'/>" style="display:block;">
                <div class="item-kicker">Admin</div>
                <h3 class="item-title">System admin</h3>
                <div class="item-meta"><span>Add branches and staff users</span></div>
            </a>
        </div>
    </sec:authorize>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
