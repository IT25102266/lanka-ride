<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Reports" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
    <div>
        <h1 class="page-title">Reports</h1>
        <p class="page-lead">${label}</p>
    </div>
    <button class="btn btn-outline-secondary btn-sm" type="button" onclick="window.print()">
        <i class="bi bi-printer"></i> Print / export
    </button>
</div>

<form method="get" class="filter-bar mb-4">
    <div class="row g-2 align-items-end">
        <div class="col-md-3">
            <label class="form-label small">Period</label>
            <select class="form-select form-select-sm" name="period">
                <option value="daily" ${period == 'daily' ? 'selected' : ''}>Daily</option>
                <option value="monthly" ${period == 'monthly' ? 'selected' : ''}>Monthly</option>
                <option value="annual" ${period == 'annual' ? 'selected' : ''}>Annual</option>
            </select>
        </div>
        <div class="col-md-3">
            <label class="form-label small">Reference date</label>
            <input class="form-control form-control-sm" type="date" name="date" value="${refDate}"/>
        </div>
        <div class="col-md-3">
            <label class="form-label small">Branch filter</label>
            <select class="form-select form-select-sm" name="branchId">
                <option value="">All branches</option>
                <c:forEach items="${branches}" var="b">
                    <option value="${b.id}" ${branchId == b.id ? 'selected' : ''}>${b.name}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-3">
            <button class="btn btn-primary btn-sm" type="submit">Apply filters</button>
        </div>
    </div>
</form>

<div class="row g-3 mb-4">
    <div class="col-md-3">
        <div class="stat-mini">
            <div class="label">Collected</div>
            <div class="value" style="font-size:1.55rem;">LKR ${collected}</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-mini">
            <div class="label">Refunds</div>
            <div class="value" style="font-size:1.55rem;">LKR ${refunds}</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-mini">
            <div class="label">Net</div>
            <div class="value" style="font-size:1.55rem;">LKR ${net}</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-mini">
            <div class="label">Bookings / completed</div>
            <div class="value" style="font-size:1.55rem;">${bookingsInPeriod} / ${completedInPeriod}</div>
        </div>
    </div>
</div>

<h2 class="h4 mb-3">Branch comparison</h2>
<div class="item-stack mb-4">
    <c:forEach items="${branchRows}" var="row">
        <article class="item-card">
            <div>
                <div class="item-kicker">Branch</div>
                <h3 class="item-title">${row.branch.name}</h3>
                <div class="item-meta">
                    <span><i class="bi bi-cash"></i> Revenue LKR ${row.revenue}</span>
                    <span><i class="bi bi-car-front"></i> Fleet ${row.fleetSize}</span>
                    <span><i class="bi bi-calendar3"></i> Bookings ${row.bookings}</span>
                </div>
            </div>
            <div>
                <div class="item-badges">
                    <span class="status-pill status-ongoing">${row.utilization}% util</span>
                </div>
            </div>
        </article>
    </c:forEach>
    <c:if test="${empty branchRows}">
        <div class="panel"><div class="empty-state">No branch data for this period.</div></div>
    </c:if>
</div>

<h2 class="h4 mb-3">Vehicle locations</h2>
<div class="item-stack">
    <c:forEach items="${locations}" var="loc">
        <c:set var="vs" value="${fn:toLowerCase(loc.vehicle.status.name())}"/>
        <article class="item-card">
            <div>
                <div class="item-kicker">${loc.vehicle.registrationNumber} · ${loc.vehicle.branch.name}</div>
                <h3 class="item-title">${loc.vehicle.brand} ${loc.vehicle.model}</h3>
                <div class="item-meta">
                    <span><i class="bi bi-geo-alt"></i> ${loc.vehicle.currentLocation}</span>
                </div>
            </div>
            <div class="item-badges">
                <span class="status-pill status-${vs}">${loc.vehicle.status}</span>
            </div>
        </article>
    </c:forEach>
    <c:if test="${empty locations}">
        <div class="panel"><div class="empty-state">No vehicles to show.</div></div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
