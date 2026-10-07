<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="${editing ? 'Edit maintenance' : 'Add maintenance'}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="fleet-page scene-garage">
<div class="page-head">
    <div>
        <a class="back-link" href="<c:url value='/maintenance'/>">&larr; Back to maintenance</a>
        <h1 class="page-title">${editing ? 'Edit maintenance' : 'New maintenance record'}</h1>
        <p class="page-lead">Opening a record takes the vehicle offline for bookings until you close it.</p>
    </div>
</div>

<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="detail-block">

            <c:if test="${not empty conflicts}">
                <div class="alert alert-warning">
                    <strong>Booking conflicts</strong>
                    <ul class="mb-2">
                        <c:forEach items="${conflicts}" var="b">
                            <li>Booking #${b.id} — ${b.customer.username} — ${b.pickupDate} to ${b.returnDate} (${b.status})</li>
                        </c:forEach>
                    </ul>
                    <p class="mb-0 small">Confirm below to take the vehicle offline anyway.</p>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${editing}">
                    <c:url var="saveUrl" value="/maintenance/${record.id}"/>
                </c:when>
                <c:otherwise>
                    <c:url var="saveUrl" value="/maintenance"/>
                </c:otherwise>
            </c:choose>

            <form:form method="post" modelAttribute="record" action="${saveUrl}">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="mb-3">
                    <label class="form-label" for="vehicleId">Vehicle</label>
                    <select class="form-select" id="vehicleId" name="vehicleId" ${editing ? 'disabled' : 'required'}>
                        <c:forEach items="${vehicles}" var="v">
                            <option value="${v.id}" <c:if test="${selectedVehicleId == v.id}">selected</c:if>>
                                ${v.registrationNumber} — ${v.brand} ${v.model} (${v.status})
                            </option>
                        </c:forEach>
                    </select>
                    <c:if test="${editing}">
                        <input type="hidden" name="vehicleId" value="${selectedVehicleId}"/>
                    </c:if>
                </div>

                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label" for="serviceType">Service type</label>
                        <form:input path="serviceType" id="serviceType" cssClass="form-control" required="true"
                                    minlength="3" maxlength="50" list="serviceTypeList"
                                    placeholder="Routine service / Repair / Damage"/>
                        <datalist id="serviceTypeList">
                            <c:forEach items="${serviceTypes}" var="service">
                                <option value="${service.name}"></option>
                            </c:forEach>
                        </datalist>
                        <form:errors path="serviceType" cssClass="text-danger small d-block"/>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="status">Status</label>
                        <form:select path="status" id="status" cssClass="form-select" items="${statuses}"/>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="serviceDate">Service date</label>
                        <form:input path="serviceDate" id="serviceDate" type="date" cssClass="form-control" required="true" min="${today}"/>
                        <form:errors path="serviceDate" cssClass="text-danger small d-block"/>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="estimatedCompletionDate">Est. completion</label>
                        <form:input path="estimatedCompletionDate" id="estimatedCompletionDate" type="date" cssClass="form-control" min="${today}"/>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="estimatedCost">Estimated cost (LKR)</label>
                        <form:input path="estimatedCost" id="estimatedCost" type="number" min="0" step="0.01" cssClass="form-control"/>
                        <form:errors path="estimatedCost" cssClass="text-danger small d-block"/>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="mechanicsAssigned">Mechanics</label>
                        <form:input path="mechanicsAssigned" id="mechanicsAssigned" cssClass="form-control"/>
                    </div>
                    <div class="col-12">
                        <label class="form-label" for="description">Description</label>
                        <form:textarea path="description" id="description" cssClass="form-control" rows="3"/>
                    </div>
                </div>

                <c:if test="${not empty conflicts && !editing}">
                    <div class="form-check mt-3">
                        <input class="form-check-input" type="checkbox" value="true" id="force" name="force"/>
                        <label class="form-check-label" for="force">
                            Confirm take vehicle offline despite booking conflicts
                        </label>
                    </div>
                </c:if>

                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-primary">Save record</button>
                    <a class="btn btn-outline-secondary" href="<c:url value='/maintenance'/>">Cancel</a>
                </div>
            </form:form>
            <script>
                (function () {
                    var service = document.getElementById("serviceDate");
                    var completion = document.getElementById("estimatedCompletionDate");
                    if (!service || !completion) return;
                    function sync() {
                        completion.min = service.value && service.value > service.min ? service.value : service.min;
                        if (completion.value && completion.value < completion.min) {
                            completion.value = completion.min;
                        }
                    }
                    service.addEventListener("change", sync);
                    sync();
                })();
            </script>
        </div>
    </div>
</div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
