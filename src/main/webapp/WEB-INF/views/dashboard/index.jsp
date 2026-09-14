<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="pageTitle" value="Staff dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-4">
    <div>
        <h1 class="h2 mb-1">Operations dashboard</h1>
        <p class="text-muted mb-0">Welcome, <strong>${username}</strong>
            <span class="badge badge-lr ms-1">${roles}</span>
        </p>
    </div>
    <a class="btn btn-outline-primary btn-sm" href="<c:url value='/vehicles'/>">Browse fleet</a>
</div>

<div class="row g-3 mb-4">
    <div class="col-md-4 col-lg">
        <div class="card stat-card shadow-sm h-100">
            <div class="card-body">
                <div class="text-muted small">Total vehicles</div>
                <div class="fs-3 fw-semibold">${totalVehicles}</div>
            </div>
        </div>
    </div>
    <div class="col-md-4 col-lg">
        <div class="card stat-card shadow-sm h-100">
            <div class="card-body">
                <div class="text-muted small">Available</div>
                <div class="fs-3 fw-semibold">${availableVehicles}</div>
            </div>
        </div>
    </div>
    <div class="col-md-4 col-lg">
        <div class="card stat-card shadow-sm h-100">
            <div class="card-body">
                <div class="text-muted small">In maintenance</div>
                <div class="fs-3 fw-semibold">${maintenanceVehicles}</div>
            </div>
        </div>
    </div>
    <div class="col-md-4 col-lg">
        <div class="card stat-card shadow-sm h-100">
            <div class="card-body">
                <div class="text-muted small">Pending bookings</div>
                <div class="fs-3 fw-semibold">${pendingBookings}</div>
            </div>
        </div>
    </div>
    <div class="col-md-4 col-lg">
        <div class="card stat-card shadow-sm h-100">
            <div class="card-body">
                <div class="text-muted small">Branches</div>
                <div class="fs-3 fw-semibold">${branchCount}</div>
            </div>
        </div>
    </div>
</div>

<h2 class="h5 mb-3">Your workspace</h2>
<div class="row g-3 mb-4">
    <sec:authorize access="hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')">
        <div class="col-md-6 col-lg-4">
            <a class="card shadow-sm text-decoration-none h-100" href="<c:url value='/bookings/pending'/>">
                <div class="card-body">
                    <h3 class="h6 text-dark">Pending approvals</h3>
                    <p class="small text-muted mb-0">${pendingBookings} waiting for a decision</p>
                </div>
            </a>
        </div>
    </sec:authorize>
    <sec:authorize access="hasAnyRole('ADMIN','FLEET_COORDINATOR','OPERATIONS_MANAGER')">
        <div class="col-md-6 col-lg-4">
            <a class="card shadow-sm text-decoration-none h-100" href="<c:url value='/vehicles/new'/>">
                <div class="card-body">
                    <h3 class="h6 text-dark">Add vehicle</h3>
                    <p class="small text-muted mb-0">Create a new fleet record</p>
                </div>
            </a>
        </div>
    </sec:authorize>
    <sec:authorize access="hasAnyRole('ADMIN','FLEET_COORDINATOR')">
        <div class="col-md-6 col-lg-4">
            <a class="card shadow-sm text-decoration-none h-100" href="<c:url value='/maintenance'/>">
                <div class="card-body">
                    <h3 class="h6 text-dark">Maintenance</h3>
                    <p class="small text-muted mb-0">Service records and availability flags</p>
                </div>
            </a>
        </div>
    </sec:authorize>
    <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')">
        <div class="col-md-6 col-lg-4">
            <a class="card shadow-sm text-decoration-none h-100" href="<c:url value='/payments'/>">
                <div class="card-body">
                    <h3 class="h6 text-dark">Payments ledger</h3>
                    <p class="small text-muted mb-0">Transactions, refunds, invoices</p>
                </div>
            </a>
        </div>
    </sec:authorize>
    <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER','OPERATIONS_MANAGER')">
        <div class="col-md-6 col-lg-4">
            <a class="card shadow-sm text-decoration-none h-100" href="<c:url value='/reports'/>">
                <div class="card-body">
                    <h3 class="h6 text-dark">Reports</h3>
                    <p class="small text-muted mb-0">Daily / monthly / annual + utilization</p>
                </div>
            </a>
        </div>
    </sec:authorize>
    <sec:authorize access="hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')">
        <div class="col-md-6 col-lg-4">
            <a class="card shadow-sm text-decoration-none h-100" href="<c:url value='/support'/>">
                <div class="card-body">
                    <h3 class="h6 text-dark">Support tickets</h3>
                    <p class="small text-muted mb-0">Respond to customer issues</p>
                </div>
            </a>
        </div>
    </sec:authorize>
    <sec:authorize access="hasRole('ADMIN')">
        <div class="col-md-6 col-lg-4">
            <a class="card shadow-sm text-decoration-none h-100" href="<c:url value='/admin'/>">
                <div class="card-body">
                    <h3 class="h6 text-dark">Admin</h3>
                    <p class="small text-muted mb-0">Add branches and staff users</p>
                </div>
            </a>
        </div>
    </sec:authorize>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
