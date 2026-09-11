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

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
