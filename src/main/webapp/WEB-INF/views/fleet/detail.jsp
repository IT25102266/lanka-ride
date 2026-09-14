<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Maintenance #${record.id}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="mb-3">
    <a class="small text-decoration-none" href="<c:url value='/maintenance'/>">&larr; Back to maintenance</a>
</div>

<div class="row g-4">
    <div class="col-lg-7">
        <div class="card shadow-sm">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-start mb-3">
                    <h1 class="h4 mb-0">Maintenance #${record.id}</h1>
                    <span class="badge text-bg-primary">${record.status}</span>
                </div>
                <dl class="row mb-0">
                    <dt class="col-sm-4">Vehicle</dt>
                    <dd class="col-sm-8">
                        <a href="<c:url value='/vehicles/${record.vehicle.id}'/>">${record.vehicle.registrationNumber}</a>
                        — ${record.vehicle.brand} ${record.vehicle.model}
                        <span class="badge text-bg-warning">${record.vehicle.status}</span>
                    </dd>
                    <dt class="col-sm-4">Service type</dt>
                    <dd class="col-sm-8">${record.serviceType}</dd>
                    <dt class="col-sm-4">Service date</dt>
                    <dd class="col-sm-8">${record.serviceDate}</dd>
                    <dt class="col-sm-4">Est. completion</dt>
                    <dd class="col-sm-8"><c:out value="${record.estimatedCompletionDate}" default="—"/></dd>
                    <dt class="col-sm-4">Completed</dt>
                    <dd class="col-sm-8"><c:out value="${record.completionDate}" default="—"/></dd>
                    <dt class="col-sm-4">Est. cost</dt>
                    <dd class="col-sm-8"><c:out value="${record.estimatedCost}" default="—"/></dd>
                    <dt class="col-sm-4">Final cost</dt>
                    <dd class="col-sm-8"><c:out value="${record.finalCost}" default="—"/></dd>
                    <dt class="col-sm-4">Mechanics</dt>
                    <dd class="col-sm-8"><c:out value="${record.mechanicsAssigned}" default="—"/></dd>
                    <dt class="col-sm-4">Description</dt>
                    <dd class="col-sm-8"><c:out value="${record.description}" default="—"/></dd>
                </dl>

                <div class="d-flex flex-wrap gap-2 mt-4">
                    <c:if test="${record.status.name() != 'CLOSED'}">
                        <a class="btn btn-outline-primary" href="<c:url value='/maintenance/${record.id}/edit'/>">Edit</a>
                        <form method="post" action="<c:url value='/maintenance/${record.id}/close'/>" class="d-flex gap-2 align-items-end">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <div>
                                <label class="form-label small mb-1" for="finalCost">Final cost</label>
                                <input class="form-control form-control-sm" id="finalCost" name="finalCost" type="number" step="0.01"/>
                            </div>
                            <button type="submit" class="btn btn-success btn-sm">Close record</button>
                        </form>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <div class="card shadow-sm">
            <div class="card-header bg-white">Service history for this vehicle</div>
            <ul class="list-group list-group-flush">
                <c:forEach items="${history}" var="h">
                    <li class="list-group-item d-flex justify-content-between">
                        <span>#${h.id} ${h.serviceType}</span>
                        <span class="small text-muted">${h.status}</span>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
