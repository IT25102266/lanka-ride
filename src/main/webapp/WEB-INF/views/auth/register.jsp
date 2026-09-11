<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Register" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="auth-wrap">
    <div class="auth-panel">
        <div class="brand-mark mb-3 d-inline-flex">Lanka Ride</div>
        <h1>Create account</h1>
        <p class="text-muted small mb-3">Public registration creates a <strong>customer</strong> account only.</p>

        <c:if test="${not empty error}">
            <div class="alert alert-danger py-2"><c:out value="${error}"/></div>
        </c:if>

        <form method="post" action="<c:url value='/register'/>">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="mb-3">
                <label for="fullName" class="form-label">Full name</label>
                <input id="fullName" name="fullName" class="form-control" value="${fullName}" required/>
            </div>
            <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <input id="username" name="username" class="form-control" value="${username}" required/>
            </div>
            <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <input id="email" type="email" name="email" class="form-control" value="${email}" required/>
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input id="password" type="password" name="password" class="form-control" required/>
            </div>
            <div class="mb-3">
                <label for="confirmPassword" class="form-label">Confirm password</label>
                <input id="confirmPassword" type="password" name="confirmPassword" class="form-control" required/>
            </div>
            <button type="submit" class="btn btn-primary w-100">Register</button>
        </form>
        <hr/>
        <p class="small mb-0">Already registered?
            <a href="<c:url value='/login'/>">Sign in</a>
        </p>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
