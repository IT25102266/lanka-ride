<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Notifications" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
    <div>
        <h1 class="page-title">Notification log</h1>
        <p class="page-lead">Sandbox email / SMS-style messages recorded for the demo (booking, payment, reset).</p>
    </div>
</div>

<c:choose>
    <c:when test="${empty notifications}">
        <div class="panel"><div class="empty-state">No notifications yet — they appear after booking or payment events.</div></div>
    </c:when>
    <c:otherwise>
        <div class="item-stack">
            <c:forEach items="${notifications}" var="n">
                <article class="notif-card">
                    <div class="d-flex flex-wrap justify-content-between gap-2 mb-1">
                        <span class="channel">${n.channel}</span>
                        <span class="small text-muted">${n.createdAt}</span>
                    </div>
                    <h3 class="h6 mb-1">${n.subject}</h3>
                    <div class="small text-muted mb-2"><i class="bi bi-envelope"></i> ${n.recipient}</div>
                    <p class="small mb-0">${n.body}</p>
                </article>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
