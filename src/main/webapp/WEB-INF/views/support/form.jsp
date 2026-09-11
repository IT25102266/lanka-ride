<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="New support ticket" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="mb-3">
    <a class="small text-decoration-none" href="<c:url value='/support'/>">&larr; Back to support</a>
</div>

<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="auth-panel mx-auto" style="max-width:560px;">
            <h1 class="page-title">Submit enquiry</h1>
            <p class="page-lead mb-3">Tell us what you need help with. Staff will reply on this ticket.</p>
            <form method="post" action="<c:url value='/support'/>">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="mb-3">
                    <label class="form-label">Subject</label>
                    <input class="form-control" name="subject" required placeholder="e.g. Change pickup time"/>
                </div>
                <div class="mb-3">
                    <label class="form-label">Message</label>
                    <textarea class="form-control" name="message" rows="6" required placeholder="Describe your issue…"></textarea>
                </div>
                <button class="btn btn-primary" type="submit">Submit ticket</button>
                <a class="btn btn-outline-secondary" href="<c:url value='/support'/>">Cancel</a>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
