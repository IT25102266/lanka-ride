<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Support" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
    <div>
        <h1 class="page-title">Support centre</h1>
        <p class="page-lead">
            <c:choose>
                <c:when test="${staff}">Triage customer tickets and post staff replies.</c:when>
                <c:otherwise>Raise an enquiry — we reply on the same ticket thread.</c:otherwise>
            </c:choose>
        </p>
    </div>
    <a class="btn btn-primary" href="<c:url value='/support/new'/>"><i class="bi bi-plus-lg"></i> New ticket</a>
</div>

<c:choose>
    <c:when test="${empty tickets}">
        <div class="panel">
            <div class="empty-state">
                <p class="mb-3">No tickets yet.</p>
                <a class="btn btn-primary" href="<c:url value='/support/new'/>">Submit an enquiry</a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="item-stack">
            <c:forEach items="${tickets}" var="t">
                <c:set var="ts" value="${fn:toLowerCase(fn:replace(t.status.name(), '_', '-'))}"/>
                <article class="item-card">
                    <div>
                        <div class="item-kicker">Ticket #${t.id}
                            <c:if test="${staff}"> · ${t.customer.fullName} (@${t.customer.username})</c:if>
                        </div>
                        <h3 class="item-title">${t.subject}</h3>
                        <div class="item-meta">
                            <span><i class="bi bi-clock"></i> ${t.createdAt}</span>
                            <c:if test="${not empty t.message}">
                                <span>${fn:substring(t.message, 0, 90)}<c:if test="${fn:length(t.message) > 90}">…</c:if></span>
                            </c:if>
                        </div>
                    </div>
                    <div>
                        <div class="item-badges">
                            <span class="status-pill status-${ts}">${t.status}</span>
                        </div>
                        <div class="item-actions">
                            <a class="btn btn-sm btn-outline-primary" href="<c:url value='/support/${t.id}'/>">
                                ${staff ? 'Respond' : 'View'}
                            </a>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
