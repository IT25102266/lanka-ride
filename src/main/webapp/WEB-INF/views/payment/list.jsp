<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Payments" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
    <div>
        <h1 class="page-title">Payments</h1>
        <p class="page-lead">Sandbox transaction ledger across bookings.</p>
    </div>
</div>

<c:choose>
    <c:when test="${empty payments}">
        <div class="panel"><div class="empty-state">No payments yet.</div></div>
    </c:when>
    <c:otherwise>
        <div class="item-stack">
            <c:forEach items="${payments}" var="p">
                <article class="item-card ${p.success ? '' : 'border-danger'}">
                    <div>
                        <div class="item-kicker">${p.type} · Booking #${p.booking.id}</div>
                        <h3 class="item-title">LKR ${p.amount}</h3>
                        <div class="item-meta">
                            <span><i class="bi bi-hash"></i> ${p.gatewayReference}</span>
                            <span><i class="bi bi-clock"></i> ${p.createdAt}</span>
                            <c:if test="${not empty p.note}"><span>${p.note}</span></c:if>
                        </div>
                    </div>
                    <div>
                        <div class="item-badges">
                            <span class="status-pill ${p.success ? 'status-paid' : 'status-denied'}">
                                ${p.success ? 'SUCCESS' : 'FAILED'}
                            </span>
                        </div>
                        <div class="item-actions">
                            <a class="btn btn-sm btn-outline-primary" href="<c:url value='/payments/booking/${p.booking.id}'/>">Invoice</a>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
