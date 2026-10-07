<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Forgot password" scope="request"/>
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
        <h1>Forgot password</h1>
        <p class="page-lead mb-3">Enter your username or email. For the demo, the reset token is written to the notification log.</p>
        <form method="post" action="<c:url value='/forgot-password'/>">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="mb-3">
                <label class="form-label" for="usernameOrEmail">Username or email</label>
                <input id="usernameOrEmail" class="form-control" name="usernameOrEmail" required autofocus/>
            </div>
            <button class="btn btn-primary w-100" type="submit">Send reset token</button>
        </form>
        <p class="auth-links">
            <a href="<c:url value='/login'/>">Back to login</a>
            <a href="<c:url value='/reset-password'/>">I already have a token</a>
        </p>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
