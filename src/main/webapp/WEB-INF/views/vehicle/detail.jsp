<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="pageTitle" value="${vehicle.brand} ${vehicle.model}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<a class="back-link" href="<c:url value='/vehicles'/>">&larr; Back to search</a>

<div class="vehicle-layout">
    <div class="vehicle-photo">
        <c:choose>
            <c:when test="${not empty vehicle.photoUrl}">
                <img src="${vehicle.photoUrl}" alt="${vehicle.brand} ${vehicle.model}"
                     onerror="this.hidden=true; this.nextElementSibling.hidden=false;"/>
                <div class="photo-fallback" hidden>
                    <i class="bi bi-car-front"></i>
                    <span>No photo</span>
                </div>
            </c:when>
            <c:otherwise>
                <div class="photo-fallback">
                    <i class="bi bi-car-front"></i>
                    <span>No photo</span>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="vehicle-summary">
        <div class="d-flex flex-wrap justify-content-between align-items-start gap-2">
            <div>
                <h1>${vehicle.brand} ${vehicle.model}</h1>
                <p class="page-lead">${vehicle.registrationNumber} · ${vehicle.branch.name}</p>
            </div>
            <span class="status-pill status-${fn:toLowerCase(vehicle.status.name())}">${vehicle.status}</span>
        </div>

        <dl class="spec-grid">
            <div><dt>Category</dt><dd>${vehicle.category}</dd></div>
            <div><dt>Seats</dt><dd>${vehicle.seats}</dd></div>
            <div><dt>Gearbox</dt><dd>${vehicle.gearbox}</dd></div>
            <div><dt>Fuel</dt><dd>${vehicle.fuelType}</dd></div>
            <div><dt>Features</dt><dd>${empty vehicle.features ? '—' : vehicle.features}</dd></div>
            <div><dt>Location</dt><dd>${vehicle.currentLocation}</dd></div>
            <div><dt>Price / day</dt><dd>LKR ${vehicle.pricePerDay}</dd></div>
            <div><dt>Deposit</dt><dd>LKR ${vehicle.depositAmount}</dd></div>
        </dl>

        <div class="action-row">
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
