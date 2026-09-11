<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="auth-wrap">
    <div class="auth-panel">
        <div class="brand-mark mb-3 d-inline-flex">Lanka Ride</div>
        <h1>Sign in</h1>
        <p class="text-muted small mb-3">Customers go to My trips. Staff open the operations dashboard.</p>

        <c:if test="${param.error != null}">
            <div class="alert alert-danger py-2">Invalid username or password</div>
        </c:if>
        <c:if test="${param.logout != null}">
            <div class="alert alert-success py-2">You have been signed out</div>
        </c:if>

        <form method="post" action="<c:url value='/login'/>">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <input id="username" name="username" class="form-control" required autofocus/>
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input id="password" type="password" name="password" class="form-control" required/>
            </div>
            <button type="submit" class="btn btn-primary w-100">Login</button>
        </form>

        <p class="small text-muted mt-3 mb-1">Customer demo: <code>customer</code> / <code>customer123</code></p>
        <details class="small text-muted">
            <summary>Staff demo logins</summary>
            <div class="mt-2">
                admin/admin123 · fleet/fleet123 · supervisor/super123 · finance/finance123 · operations/ops123
            </div>
        </details>
        <hr/>
        <p class="small mb-0">No account?
            <a href="<c:url value='/register'/>">Create one</a>
            · <a href="<c:url value='/forgot-password'/>">Forgot password</a>
        </p>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
