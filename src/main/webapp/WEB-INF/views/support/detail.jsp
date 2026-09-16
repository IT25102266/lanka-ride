<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Ticket #${ticket.id}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="mb-3">
    <a class="small text-decoration-none" href="<c:url value='/support'/>">&larr; Back to support centre</a>
</div>

<c:set var="ts" value="${fn:toLowerCase(fn:replace(ticket.status.name(), '_', '-'))}"/>

<section class="detail-hero">
    <div class="d-flex flex-wrap justify-content-between gap-3 align-items-start">
        <div>
            <p class="muted small mb-1 text-uppercase" style="letter-spacing:.08em;">Ticket #${ticket.id}</p>
            <h1>${ticket.subject}</h1>
            <p class="muted mb-0">${ticket.customer.fullName} · ${ticket.customer.email} · ${ticket.createdAt}</p>
        </div>
        <span class="status-pill status-${ts}">${ticket.status}</span>
    </div>
</section>

<div class="row g-4">
    <div class="col-lg-7">
        <div class="detail-block">
            <h2>Customer message</h2>
            <p class="mb-0" style="white-space:pre-wrap;">${ticket.message}</p>
        </div>
        <c:if test="${not empty ticket.staffResponse}">
            <div class="detail-block">
                <h2>Staff response</h2>
                <p class="mb-0" style="white-space:pre-wrap;">${ticket.staffResponse}</p>
            </div>
        </c:if>
        <c:if test="${empty ticket.staffResponse && !staff}">
            <div class="alert alert-info small mb-0">No staff reply yet — check back soon.</div>
        </c:if>
    </div>
    <c:if test="${staff}">
        <div class="col-lg-5">
            <div class="detail-block">
                <h2>Respond &amp; update status</h2>
                <form method="post" action="<c:url value='/support/${ticket.id}/respond'/>">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="mb-3">
                        <label class="form-label small">Response</label>
                        <textarea class="form-control" name="staffResponse" rows="5" required
                                  placeholder="Write the reply the customer will see…">${ticket.staffResponse}</textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small">Status</label>
                        <select class="form-select" name="status">
                            <c:forEach items="${statuses}" var="s">
                                <option value="${s}" ${ticket.status == s ? 'selected' : ''}>${s}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button class="btn btn-primary" type="submit">Save update</button>
                </form>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
