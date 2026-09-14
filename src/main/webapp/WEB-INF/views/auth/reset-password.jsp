<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Reset password" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="auth-wrap">
    <div class="auth-panel">
        <div class="brand-mark mb-3 d-inline-flex">Lanka Ride</div>
        <h1>Reset password</h1>
        <p class="page-lead mb-3">Paste the token from the notification log, then choose a new password.</p>
        <c:if test="${not empty error}">
            <div class="alert alert-danger py-2"><c:out value="${error}"/></div>
        </c:if>
        <form method="post" action="<c:url value='/reset-password'/>">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="mb-3">
                <label class="form-label">Token</label>
                <input class="form-control" name="token" value="${token}" required/>
            </div>
            <div class="mb-3">
                <label class="form-label">New password</label>
                <input class="form-control" type="password" name="password" required/>
            </div>
            <div class="mb-3">
                <label class="form-label">Confirm password</label>
                <input class="form-control" type="password" name="confirmPassword" required/>
            </div>
            <button class="btn btn-primary w-100" type="submit">Update password</button>
        </form>
        <p class="small text-muted mt-3 mb-0">
            <a href="<c:url value='/forgot-password'/>">Request a new token</a>
            · <a href="<c:url value='/login'/>">Login</a>
        </p>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
