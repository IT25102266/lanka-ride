<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Maintenance" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
    <div>
        <h1 class="page-title">Fleet maintenance</h1>
        <p class="page-lead">Service and repair records that affect vehicle availability.</p>
    </div>
    <a class="btn btn-primary" href="<c:url value='/maintenance/new'/>">Add record</a>
</div>

<c:choose>
    <c:when test="${empty records}">
        <div class="panel"><div class="empty-state">No maintenance records yet.</div></div>
    </c:when>
    <c:otherwise>
        <div class="item-stack">
            <c:forEach items="${records}" var="r">
                <c:set var="rs" value="${fn:toLowerCase(fn:replace(r.status.name(), '_', '-'))}"/>
                <article class="item-card">
                    <div>
                        <div class="item-kicker">#${r.id} · ${r.serviceType}</div>
                        <h3 class="item-title">${r.vehicle.brand} ${r.vehicle.model}</h3>
                        <div class="item-meta">
                            <span><i class="bi bi-car-front"></i> ${r.vehicle.registrationNumber}</span>
                            <span><i class="bi bi-calendar3"></i> ${r.serviceDate}</span>
                            <span><i class="bi bi-cash"></i> LKR <c:out value="${r.estimatedCost}" default="—"/></span>
                        </div>
                    </div>
                    <div>
                        <div class="item-badges">
                            <span class="status-pill status-${rs}">${r.status}</span>
                        </div>
                        <div class="item-actions">
                            <a class="btn btn-sm btn-outline-primary" href="<c:url value='/maintenance/${r.id}'/>">Open</a>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
