<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Forgot password" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="auth-wrap">
    <div class="auth-panel">
        <div class="brand-mark mb-3 d-inline-flex">Lanka Ride</div>
        <h1>Forgot password</h1>
        <p class="page-lead mb-3">Enter your username or email. For the demo, the reset token is written to the notification log.</p>
        <form method="post" action="<c:url value='/forgot-password'/>">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="mb-3">
                <label class="form-label">Username or email</label>
                <input class="form-control" name="usernameOrEmail" required autofocus/>
            </div>
            <button class="btn btn-primary w-100" type="submit">Send reset token</button>
        </form>
        <p class="small text-muted mt-3 mb-0">
            <a href="<c:url value='/login'/>">Back to login</a>
            · <a href="<c:url value='/reset-password'/>">I already have a token</a>
        </p>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
