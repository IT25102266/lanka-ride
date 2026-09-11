<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Forgot password" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="auth-wrap">
    <div class="auth-panel">
        <h1>Forgot password</h1>
        <p class="text-muted small mb-3">We'll issue a reset token (logged for demo).</p>
        <form method="post" action="<c:url value='/forgot-password'/>">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="mb-3">
                <label class="form-label">Username or email</label>
                <input class="form-control" name="usernameOrEmail" required/>
            </div>
            <button class="btn btn-primary w-100" type="submit">Send reset token</button>
        </form>
        <p class="small text-muted mt-3 mb-0">
            <a href="<c:url value='/login'/>">Back to login</a>
        </p>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
