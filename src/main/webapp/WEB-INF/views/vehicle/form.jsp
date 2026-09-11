<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="${vehicle.id == null ? 'Add vehicle' : 'Edit vehicle'}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="mb-3">
    <a class="small text-decoration-none" href="<c:url value='/vehicles'/>">&larr; Back to vehicles</a>
</div>

<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h1 class="h4 mb-3">${vehicle.id == null ? 'Add vehicle' : 'Edit vehicle'}</h1>

                <form:form method="post" modelAttribute="vehicle" action="${formAction}">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label" for="registrationNumber">Registration number</label>
                            <form:input path="registrationNumber" id="registrationNumber" cssClass="form-control" required="true"/>
                            <form:errors path="registrationNumber" cssClass="text-danger small"/>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label" for="category">Category</label>
                            <form:input path="category" id="category" cssClass="form-control" required="true"/>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label" for="brand">Brand</label>
                            <form:input path="brand" id="brand" cssClass="form-control" required="true"/>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label" for="model">Model</label>
                            <form:input path="model" id="model" cssClass="form-control" required="true"/>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label" for="seats">Seats</label>
                            <form:input path="seats" id="seats" type="number" min="1" max="20" cssClass="form-control" required="true"/>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label" for="gearbox">Gearbox</label>
                            <form:select path="gearbox" id="gearbox" cssClass="form-select" items="${gearboxes}"/>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label" for="fuelType">Fuel type</label>
                            <form:select path="fuelType" id="fuelType" cssClass="form-select" items="${fuelTypes}"/>
                        </div>
                        <div class="col-12">
                            <label class="form-label" for="features">Features</label>
                            <form:textarea path="features" id="features" cssClass="form-control" rows="2"/>
                        </div>
                        <div class="col-12">
                            <label class="form-label" for="photoUrl">Photo URL</label>
                            <form:input path="photoUrl" id="photoUrl" cssClass="form-control"/>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label" for="pricePerDay">Price per day (LKR)</label>
                            <form:input path="pricePerDay" id="pricePerDay" type="number" step="0.01" cssClass="form-control" required="true"/>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label" for="depositAmount">Deposit (LKR)</label>
                            <form:input path="depositAmount" id="depositAmount" type="number" step="0.01" cssClass="form-control" required="true"/>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label" for="branchId">Home branch</label>
                            <select id="branchId" name="branchId" class="form-select" required>
                                <c:forEach items="${branches}" var="b">
                                    <option value="${b.id}"
                                            <c:if test="${vehicle.branch != null && vehicle.branch.id == b.id}">selected</c:if>>
                                        ${b.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label" for="currentLocation">Current location</label>
                            <form:input path="currentLocation" id="currentLocation" cssClass="form-control"/>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label" for="status">Status</label>
                            <form:select path="status" id="status" cssClass="form-select" items="${statuses}"/>
                        </div>
                    </div>

                    <div class="d-flex gap-2 mt-4">
                        <button type="submit" class="btn btn-primary">Save</button>
                        <a class="btn btn-outline-secondary" href="<c:url value='/vehicles'/>">Cancel</a>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
