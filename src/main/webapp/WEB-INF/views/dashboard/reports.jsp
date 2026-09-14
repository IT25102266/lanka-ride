<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Reports" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
    <div>
        <h1 class="h3 mb-1">Reports</h1>
        <p class="text-muted mb-0">${label}</p>
    </div>
</div>

<div class="card shadow-sm mb-4">
    <div class="card-body">
        <form method="get" class="row g-2 align-items-end">
            <div class="col-md-3">
                <label class="form-label">Period</label>
                <select class="form-select" name="period">
                    <option value="daily" ${period == 'daily' ? 'selected' : ''}>Daily</option>
                    <option value="monthly" ${period == 'monthly' ? 'selected' : ''}>Monthly</option>
                    <option value="annual" ${period == 'annual' ? 'selected' : ''}>Annual</option>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label">Reference date</label>
                <input class="form-control" type="date" name="date" value="${refDate}"/>
            </div>
            <div class="col-md-3">
                <label class="form-label">Branch filter</label>
                <select class="form-select" name="branchId">
                    <option value="">All branches</option>
                    <c:forEach items="${branches}" var="b">
                        <option value="${b.id}" ${branchId == b.id ? 'selected' : ''}>${b.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-3">
                <button class="btn btn-primary" type="submit">Apply</button>
            </div>
        </form>
    </div>
</div>

<div class="row g-3 mb-4">
    <div class="col-md-3">
        <div class="card shadow-sm h-100"><div class="card-body">
            <div class="text-muted small">Collected</div>
            <div class="fs-4 text-primary fw-semibold">LKR ${collected}</div>
        </div></div>
    </div>
    <div class="col-md-3">
        <div class="card shadow-sm h-100"><div class="card-body">
            <div class="text-muted small">Refunds</div>
            <div class="fs-4 fw-semibold">LKR ${refunds}</div>
        </div></div>
    </div>
    <div class="col-md-3">
        <div class="card shadow-sm h-100"><div class="card-body">
            <div class="text-muted small">Net</div>
            <div class="fs-4 fw-semibold">LKR ${net}</div>
        </div></div>
    </div>
    <div class="col-md-3">
        <div class="card shadow-sm h-100"><div class="card-body">
            <div class="text-muted small">Bookings / completed</div>
            <div class="fs-4 fw-semibold">${bookingsInPeriod} / ${completedInPeriod}</div>
        </div></div>
    </div>
</div>

<h2 class="h5 mb-3">Branch comparison</h2>
<div class="card shadow-sm mb-4">
    <div class="table-responsive">
        <table class="table mb-0">
            <thead class="table-light">
            <tr>
                <th>Branch</th>
                <th>Revenue</th>
                <th>Active fleet</th>
                <th>Bookings in period</th>
                <th>Utilization %</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${branchRows}" var="row">
                <tr>
                    <td>${row.branch.name}</td>
                    <td>LKR ${row.revenue}</td>
                    <td>${row.fleetSize}</td>
                    <td>${row.bookings}</td>
                    <td>${row.utilization}%</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<h2 class="h5 mb-3">Vehicle locations</h2>
<div class="card shadow-sm">
    <div class="table-responsive">
        <table class="table table-sm mb-0">
            <thead class="table-light">
            <tr><th>Reg</th><th>Vehicle</th><th>Home branch</th><th>Current location</th><th>Status</th></tr>
            </thead>
            <tbody>
            <c:forEach items="${locations}" var="loc">
                <tr>
                    <td>${loc.vehicle.registrationNumber}</td>
                    <td>${loc.vehicle.brand} ${loc.vehicle.model}</td>
                    <td>${loc.vehicle.branch.name}</td>
                    <td>${loc.vehicle.currentLocation}</td>
                    <td>${loc.vehicle.status}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<div class="mt-3">
    <button class="btn btn-outline-secondary btn-sm" onclick="window.print()">Print / export</button>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
