<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Maintenance #${record.id}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="mb-3">
    <a class="small text-decoration-none" href="<c:url value='/maintenance'/>">&larr; Back to maintenance</a>
</div>

<c:set var="rs" value="${fn:toLowerCase(fn:replace(record.status.name(), '_', '-'))}"/>

<section class="detail-hero">
    <div class="d-flex flex-wrap justify-content-between gap-3 align-items-start">
        <div>
            <p class="muted small mb-1 text-uppercase" style="letter-spacing:.08em;">Maintenance #${record.id}</p>
            <h1>${record.serviceType}</h1>
            <p class="muted mb-0">${record.vehicle.registrationNumber} · ${record.vehicle.brand} ${record.vehicle.model}</p>
        </div>
        <span class="status-pill status-${rs}">${record.status}</span>
    </div>
</section>

<div class="row g-4">
    <div class="col-lg-7">
        <div class="detail-block">
            <h2>Service details</h2>
            <dl class="detail-grid">
                <dt>Vehicle</dt>
                <dd>
                    <a href="<c:url value='/vehicles/${record.vehicle.id}'/>">${record.vehicle.registrationNumber}</a>
                    — ${record.vehicle.brand} ${record.vehicle.model}
                    <span class="status-pill status-${fn:toLowerCase(record.vehicle.status.name())}">${record.vehicle.status}</span>
                </dd>
                <dt>Service date</dt>
                <dd>${record.serviceDate}</dd>
                <dt>Est. completion</dt>
                <dd><c:out value="${record.estimatedCompletionDate}" default="—"/></dd>
                <dt>Completed</dt>
                <dd><c:out value="${record.completionDate}" default="—"/></dd>
                <dt>Est. cost</dt>
                <dd>LKR <c:out value="${record.estimatedCost}" default="—"/></dd>
                <dt>Final cost</dt>
                <dd>LKR <c:out value="${record.finalCost}" default="—"/></dd>
                <dt>Mechanics</dt>
                <dd><c:out value="${record.mechanicsAssigned}" default="—"/></dd>
                <dt>Description</dt>
                <dd><c:out value="${record.description}" default="—"/></dd>
            </dl>

            <div class="d-flex flex-wrap gap-2 mt-4">
                <c:if test="${record.status.name() != 'CLOSED'}">
                    <a class="btn btn-outline-primary" href="<c:url value='/maintenance/${record.id}/edit'/>">Edit</a>
                    <form method="post" action="<c:url value='/maintenance/${record.id}/close'/>" class="d-flex flex-wrap gap-2 align-items-end">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <div>
                            <label class="form-label small mb-1" for="finalCost">Final cost (LKR)</label>
                            <input class="form-control form-control-sm" id="finalCost" name="finalCost" type="number" step="0.01"/>
                        </div>
                        <button type="submit" class="btn btn-success btn-sm">Close &amp; make available</button>
                    </form>
                </c:if>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <h2 class="h5 mb-3">Service history</h2>
        <div class="history-rail">
            <c:forEach items="${history}" var="h">
                <c:set var="hs" value="${fn:toLowerCase(fn:replace(h.status.name(), '_', '-'))}"/>
                <div class="rail-item d-flex justify-content-between gap-2">
                    <div>
                        <div class="fw-semibold">#${h.id} · ${h.serviceType}</div>
                        <div class="small text-muted">${h.serviceDate}</div>
                    </div>
                    <span class="status-pill status-${hs}">${h.status}</span>
                </div>
            </c:forEach>
            <c:if test="${empty history}">
                <div class="text-muted small">No history yet.</div>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
