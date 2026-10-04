<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="pageTitle" value="${vehicle.brand} ${vehicle.model}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="mb-3">
    <a class="small text-decoration-none" href="<c:url value='/vehicles'/>">&larr; Back to search</a>
</div>

<div class="row g-4">
    <div class="col-lg-5">
        <c:choose>
            <c:when test="${not empty vehicle.photoUrl}">
                <img class="img-fluid rounded-4 border" src="${vehicle.photoUrl}" alt="${vehicle.brand} ${vehicle.model}"/>
            </c:when>
            <c:otherwise>
                <div class="bg-light border rounded-4 d-flex align-items-center justify-content-center" style="min-height:220px;">
                    <span class="text-muted">No photo</span>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="col-lg-7">
        <div class="d-flex flex-wrap justify-content-between gap-2 mb-2">
            <h1 class="h2 mb-0">${vehicle.brand} ${vehicle.model}</h1>
            <span class="badge badge-lr align-self-center">${vehicle.status}</span>
        </div>
        <p class="text-muted">${vehicle.registrationNumber} · ${vehicle.branch.name}</p>

        <dl class="row mb-4">
            <dt class="col-sm-4">Category</dt>
            <dd class="col-sm-8">${vehicle.category}</dd>
            <dt class="col-sm-4">Seats</dt>
            <dd class="col-sm-8">${vehicle.seats}</dd>
            <dt class="col-sm-4">Gearbox</dt>
            <dd class="col-sm-8">${vehicle.gearbox}</dd>
            <dt class="col-sm-4">Fuel</dt>
            <dd class="col-sm-8">${vehicle.fuelType}</dd>
            <dt class="col-sm-4">Features</dt>
            <dd class="col-sm-8">${empty vehicle.features ? '—' : vehicle.features}</dd>
            <dt class="col-sm-4">Location</dt>
            <dd class="col-sm-8">${vehicle.currentLocation}</dd>
            <dt class="col-sm-4">Price / day</dt>
            <dd class="col-sm-8 fw-semibold text-primary">LKR ${vehicle.pricePerDay}</dd>
            <dt class="col-sm-4">Deposit</dt>
            <dd class="col-sm-8">LKR ${vehicle.depositAmount}</dd>
        </dl>

        <div class="d-flex flex-wrap gap-2">
            <sec:authorize access="isAuthenticated()">
                <c:if test="${vehicle.status.name() == 'AVAILABLE'}">
                    <c:url var="bookUrl" value="/bookings/new">
                        <c:param name="vehicleId" value="${vehicle.id}"/>
                        <c:if test="${not empty pickupDate}"><c:param name="pickupDate" value="${pickupDate}"/></c:if>
                        <c:if test="${not empty returnDate}"><c:param name="returnDate" value="${returnDate}"/></c:if>
                        <c:if test="${not empty branchId}"><c:param name="branchId" value="${branchId}"/></c:if>
                        <c:if test="${empty branchId}"><c:param name="branchId" value="${vehicle.branch.id}"/></c:if>
                    </c:url>
                    <a class="btn btn-primary" href="${bookUrl}">Book this vehicle</a>
                </c:if>
            </sec:authorize>
            <sec:authorize access="!isAuthenticated()">
                <c:if test="${vehicle.status.name() == 'AVAILABLE'}">
                    <a class="btn btn-primary" href="<c:url value='/login'/>">Login to book</a>
                    <a class="btn btn-outline-primary" href="<c:url value='/register'/>">Register</a>
                </c:if>
            </sec:authorize>
            <sec:authorize access="hasAnyRole('ADMIN','FLEET_COORDINATOR')">
                <a class="btn btn-outline-warning" href="<c:url value='/maintenance/new?vehicleId=${vehicle.id}'/>">Add maintenance</a>
            </sec:authorize>
            <sec:authorize access="hasAnyRole('ADMIN','FLEET_COORDINATOR','OPERATIONS_MANAGER')">
                <a class="btn btn-outline-primary" href="<c:url value='/vehicles/${vehicle.id}/edit'/>">Edit</a>
                <form method="post" action="<c:url value='/vehicles/${vehicle.id}/retire'/>"
                      onsubmit="return confirm('Retire this vehicle from the active fleet?');">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="btn btn-outline-danger">Retire</button>
                </form>
            </sec:authorize>
        </div>
    </div>
</div>

<sec:authorize access="hasAnyRole('ADMIN','FLEET_COORDINATOR','OPERATIONS_MANAGER')">
    <div class="panel mt-4">
        <h2 class="h5">Transfer to another branch</h2>
        <p class="text-muted small">Moves the vehicle and writes a timestamped transfer log.</p>
        <form method="post" action="<c:url value='/vehicles/${vehicle.id}/transfer'/>" class="row g-2 align-items-end">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="col-md-4">
                <label class="form-label small">Destination branch</label>
                <select class="form-select" name="toBranchId" required>
                    <c:forEach items="${branches}" var="b">
                        <option value="${b.id}" ${b.id == vehicle.branch.id ? 'disabled' : ''}>${b.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-5">
                <label class="form-label small">Note</label>
                <input class="form-control" name="note" maxlength="300" placeholder="Reason for the move"/>
            </div>
            <div class="col-md-3">
                <button class="btn btn-primary w-100" type="submit">Transfer</button>
            </div>
        </form>
    </div>
</sec:authorize>

<div class="panel mt-4">
    <h2 class="h5">Branch transfer log</h2>
    <c:choose>
        <c:when test="${empty transfers}">
            <p class="text-muted mb-0">No transfers recorded.</p>
        </c:when>
        <c:otherwise>
            <ul class="mb-0">
                <c:forEach items="${transfers}" var="t">
                    <li>${t.transferredAt} — ${t.fromBranch.name} → ${t.toBranch.name}
                        <span class="text-muted">(by ${t.transferredBy}<c:if test="${not empty t.note}">, ${t.note}</c:if>)</span>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
