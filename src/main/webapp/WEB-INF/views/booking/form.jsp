<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="New booking" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="mb-3">
    <a class="back-link" href="<c:url value='/vehicles'/>">&larr; Back to search</a>
</div>

<div class="row justify-content-center">
    <div class="col-lg-7">
        <div class="detail-block">
            <h1 class="page-title mb-1">Request a booking</h1>
            <p class="page-lead mb-3">
                Pick dates and a branch. Vehicles under maintenance or already reserved for those dates cannot be booked.
            </p>

            <c:if test="${not empty error}">
                <div class="alert alert-danger py-2"><c:out value="${error}"/></div>
            </c:if>

            <form method="post" action="<c:url value='/bookings'/>">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="mb-3">
                    <label class="form-label" for="vehicleId">Vehicle</label>
                    <select class="form-select" id="vehicleId" name="vehicleId" required>
                        <c:forEach items="${vehicles}" var="v">
                            <c:set var="dateBlocked" value="${blockedVehicleIds != null and blockedVehicleIds.contains(v.id)}"/>
                            <option value="${v.id}"
                                    <c:if test="${selectedVehicleId == v.id and not dateBlocked}">selected</c:if>
                                    <c:if test="${v.status.name() != 'AVAILABLE' or dateBlocked}">disabled</c:if>>
                                ${v.registrationNumber} — ${v.brand} ${v.model} (${v.branch.name}) — ${v.status}
                                <c:if test="${dateBlocked}"> — already booked for these dates</c:if>
                            </option>
                        </c:forEach>
                    </select>
                    <div class="form-text">A vehicle already reserved for the dates below cannot be selected.</div>
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
                        <input class="form-control" type="date" id="pickupDate" name="pickupDate" value="${pickupDate}"
                               min="${today}" required/>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="returnDate">Return date</label>
                        <input class="form-control" type="date" id="returnDate" name="returnDate" value="${returnDate}"
                               min="${pickupDate}" required/>
                    </div>
                </div>
                <div class="d-flex flex-wrap gap-2">
                    <button type="submit" class="btn btn-primary">Submit request</button>
                    <a class="btn btn-outline-secondary" href="<c:url value='/vehicles'/>">Cancel</a>
                </div>
            </form>
            <script>
                (function () {
                    var pickup = document.getElementById("pickupDate");
                    var dropOff = document.getElementById("returnDate");
                    if (!pickup || !dropOff) return;
                    pickup.addEventListener("change", function () {
                        dropOff.min = pickup.value;
                        if (dropOff.value && dropOff.value < pickup.value) {
                            dropOff.value = pickup.value;
                        }
                    });
                })();
            </script>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
