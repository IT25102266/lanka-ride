<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="pageTitle" value="Find a ride" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-end gap-2 mb-3">
    <div>
        <h1 class="h2 mb-1">Find your perfect ride</h1>
        <p class="text-muted mb-0">
            <c:choose>
                <c:when test="${not empty pickupDate && not empty returnDate}">
                    Available ${pickupDate} → ${returnDate}
                    <c:if test="${not empty branchId}"> · selected branch</c:if>
                </c:when>
                <c:otherwise>Browse the fleet across Colombo, Kandy and Galle</c:otherwise>
            </c:choose>
        </p>
    </div>
    <sec:authorize access="hasAnyRole('ADMIN','FLEET_COORDINATOR','OPERATIONS_MANAGER')">
        <a class="btn btn-primary" href="<c:url value='/vehicles/new'/>">Add vehicle</a>
    </sec:authorize>
</div>

<form method="get" action="<c:url value='/vehicles'/>" class="filter-bar">
    <div class="row g-2 align-items-end">
        <div class="col-md-2">
            <label class="form-label small" for="branchId">Branch</label>
            <select class="form-select form-select-sm" id="branchId" name="branchId">
                <option value="">Any</option>
                <c:forEach items="${branches}" var="b">
                    <option value="${b.id}" <c:if test="${branchId == b.id}">selected</c:if>>${b.name}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <label class="form-label small" for="pickupDate">From</label>
            <input class="form-control form-control-sm" type="date" id="pickupDate" name="pickupDate" value="${pickupDate}"/>
        </div>
        <div class="col-md-2">
            <label class="form-label small" for="returnDate">To</label>
            <input class="form-control form-control-sm" type="date" id="returnDate" name="returnDate" value="${returnDate}"/>
        </div>
        <div class="col-md-2">
            <label class="form-label small" for="category">Category</label>
            <input class="form-control form-control-sm" id="category" name="category" value="${category}" placeholder="Sedan, SUV"/>
        </div>
        <div class="col-md-2">
            <label class="form-label small" for="gearbox">Gearbox</label>
            <select class="form-select form-select-sm" id="gearbox" name="gearbox">
                <option value="">Any</option>
                <c:forEach items="${gearboxes}" var="g">
                    <option value="${g}" <c:if test="${gearbox == g}">selected</c:if>>${g}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <label class="form-label small" for="fuelType">Fuel</label>
            <select class="form-select form-select-sm" id="fuelType" name="fuelType">
                <option value="">Any</option>
                <c:forEach items="${fuelTypes}" var="f">
                    <option value="${f}" <c:if test="${fuelType == f}">selected</c:if>>${f}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <label class="form-label small" for="minPrice">Min LKR</label>
            <input class="form-control form-control-sm" id="minPrice" name="minPrice" type="number" step="0.01" value="${minPrice}"/>
        </div>
        <div class="col-md-2">
            <label class="form-label small" for="maxPrice">Max LKR</label>
            <input class="form-control form-control-sm" id="maxPrice" name="maxPrice" type="number" step="0.01" value="${maxPrice}"/>
        </div>
        <div class="col-md-3">
            <div class="form-check mt-3">
                <input class="form-check-input" type="checkbox" id="availableOnly" name="availableOnly" value="true"
                       <c:if test="${availableOnly}">checked</c:if>/>
                <label class="form-check-label small" for="availableOnly">Available only</label>
            </div>
        </div>
        <div class="col-md-5 d-flex gap-2 justify-content-md-end">
            <button type="submit" class="btn btn-primary btn-sm">Search</button>
            <a class="btn btn-outline-secondary btn-sm" href="<c:url value='/vehicles'/>">Reset</a>
        </div>
    </div>
</form>

<div class="row g-3">
    <c:forEach items="${vehicles}" var="v">
        <div class="col-md-6 col-lg-4">
            <c:url var="detailUrl" value="/vehicles/${v.id}">
                <c:if test="${not empty pickupDate}"><c:param name="pickupDate" value="${pickupDate}"/></c:if>
                <c:if test="${not empty returnDate}"><c:param name="returnDate" value="${returnDate}"/></c:if>
                <c:if test="${not empty branchId}"><c:param name="branchId" value="${branchId}"/></c:if>
            </c:url>
            <a class="vehicle-card" href="${detailUrl}">
                <c:choose>
                    <c:when test="${not empty v.photoUrl}">
                        <img src="${v.photoUrl}" alt="${v.brand} ${v.model}"/>
                    </c:when>
                    <c:otherwise>
                        <div style="height:160px;background:#dfe8e2;"></div>
                    </c:otherwise>
                </c:choose>
                <div class="body">
                    <div class="d-flex justify-content-between align-items-start gap-2 mb-1">
                        <strong>${v.brand} ${v.model}</strong>
                        <span class="badge badge-lr">${v.status}</span>
                    </div>
                    <div class="small text-muted mb-2">
                        ${v.registrationNumber} · ${v.branch.name}<br/>
                        ${v.category} · ${v.seats} seats · ${v.gearbox} · ${v.fuelType}
                    </div>
                    <div class="price">LKR ${v.pricePerDay}<span class="fw-normal text-muted small"> / day</span></div>
                </div>
            </a>
        </div>
    </c:forEach>
    <c:if test="${empty vehicles}">
        <div class="col-12">
            <div class="panel">
                <div class="empty-state">No vehicles match these filters. Try different dates or branch.</div>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
