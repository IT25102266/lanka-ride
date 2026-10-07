<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Reset password" scope="request"/>
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
        <h1>Reset password</h1>
        <p class="page-lead mb-3">Paste the token from the notification log, then choose a new password.</p>
        <c:if test="${not empty error}">
            <div class="alert alert-danger py-2"><c:out value="${error}"/></div>
        </c:if>
        <form method="post" action="<c:url value='/reset-password'/>">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="mb-3">
                <label class="form-label" for="token">Token</label>
                <input id="token" class="form-control" name="token" value="${token}" required/>
            </div>
            <div class="mb-3">
                <label class="form-label" for="password">New password</label>
                <input id="password" class="form-control" type="password" name="password" required minlength="8" maxlength="80"/>
            </div>
            <div class="mb-3">
                <label class="form-label" for="confirmPassword">Confirm password</label>
                <input id="confirmPassword" class="form-control" type="password" name="confirmPassword" required/>
            </div>
            <button class="btn btn-primary w-100" type="submit">Update password</button>
        </form>
        <p class="auth-links">
            <a href="<c:url value='/forgot-password'/>">Request a new token</a>
            <a href="<c:url value='/login'/>">Login</a>
        </p>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
