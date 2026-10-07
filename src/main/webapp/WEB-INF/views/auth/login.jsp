<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="auth-wrap">
    <div class="auth-shell">
        <aside class="auth-aside">
            <div>
                <p class="auth-kicker">Lanka Ride</p>
                <h2>Rent a car for every journey</h2>
                <p>Search a branch, reserve the dates, and pay once the trip is approved.</p>
            </div>
            <ol class="auth-steps">
                <li><span>1</span> Choose a car</li>
                <li><span>2</span> Book online</li>
                <li><span>3</span> Pick up and drive</li>
            </ol>
        </aside>
        <div class="auth-panel">
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

        <p class="auth-note">Customer demo: <code>customer</code> / <code>customer123</code></p>
        <details class="auth-note">
            <summary>Staff demo logins</summary>
            <div class="mt-2">
                admin/admin123 · fleet/fleet123 · supervisor/super123 · finance/finance123 · operations/ops123
            </div>
        </details>
        <p class="auth-links">
            <a href="<c:url value='/register'/>">Create an account</a>
            <a href="<c:url value='/forgot-password'/>">Forgot password</a>
        </p>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
