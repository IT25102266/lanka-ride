<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="New support ticket" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="mb-3">
    <a class="small text-decoration-none" href="<c:url value='/support'/>">&larr; Back to support centre</a>
</div>

<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="detail-block">
            <h1 class="page-title mb-1">Submit enquiry</h1>
            <p class="page-lead mb-3">Describe the issue clearly — booking ID helps if this is about a rental.</p>
            <form method="post" action="<c:url value='/support'/>">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="mb-3">
                    <label class="form-label">Subject</label>
                    <input class="form-control" name="subject" required placeholder="e.g. Change pickup time for booking #12"/>
                </div>
                <div class="mb-3">
                    <label class="form-label">Message</label>
                    <textarea class="form-control" name="message" rows="6" required
                              placeholder="What happened, and what do you need?"></textarea>
                </div>
                <div class="d-flex flex-wrap gap-2">
                    <button class="btn btn-primary" type="submit">Submit ticket</button>
                    <a class="btn btn-outline-secondary" href="<c:url value='/support'/>">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
