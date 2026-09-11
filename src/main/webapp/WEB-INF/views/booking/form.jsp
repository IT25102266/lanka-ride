<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="New booking" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="mb-3">
    <a class="small text-decoration-none" href="<c:url value='/vehicles'/>">&larr; Back to search</a>
</div>

<div class="row justify-content-center">
    <div class="col-lg-7">
        <div class="panel">
            <div class="panel-body p-4">
                <h1 class="h3 mb-2">Request a booking</h1>
                <p class="text-muted small">Unavailable or overlapping vehicles cannot be booked.</p>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger py-2"><c:out value="${error}"/></div>
                </c:if>

                <form method="post" action="<c:url value='/bookings'/>">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                    <div class="mb-3">
                        <label class="form-label" for="vehicleId">Vehicle</label>
                        <select class="form-select" id="vehicleId" name="vehicleId" required>
                            <c:forEach items="${vehicles}" var="v">
                                <option value="${v.id}" <c:if test="${selectedVehicleId == v.id}">selected</c:if>
                                        <c:if test="${v.status.name() != 'AVAILABLE'}">disabled</c:if>>
                                    ${v.registrationNumber} — ${v.brand} ${v.model} (${v.branch.name}) — ${v.status}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="branchId">Pickup branch</label>
                        <select class="form-select" id="branchId" name="branchId" required>
                            <c:forEach items="${branches}" var="b">
                                <option value="${b.id}" <c:if test="${selectedBranchId == b.id}">selected</c:if>>${b.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label" for="pickupDate">Pickup date</label>
                            <input class="form-control" type="date" id="pickupDate" name="pickupDate" value="${pickupDate}" required/>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label" for="returnDate">Return date</label>
                            <input class="form-control" type="date" id="returnDate" name="returnDate" value="${returnDate}" required/>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary">Submit request</button>
                    <a class="btn btn-outline-secondary" href="<c:url value='/vehicles'/>">Cancel</a>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
