<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="mb-4">
    <h1 class="page-title">System admin</h1>
    <p class="page-lead">Scale the platform — add branches and staff accounts. Public register remains customer-only.</p>
</div>

<div class="row g-4">
    <div class="col-lg-6">
        <div class="detail-block h-100">
            <h2>Add branch</h2>
            <p class="small text-muted mb-3">New depot locations appear in search and booking forms.</p>
            <form method="post" action="<c:url value='/admin/branches'/>">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="mb-3">
                    <label class="form-label">Name</label>
                    <input class="form-control" name="name" required placeholder="e.g. Negombo"/>
                </div>
                <div class="mb-3">
                    <label class="form-label">Address</label>
                    <input class="form-control" name="address" required placeholder="Street / area"/>
                </div>
                <button class="btn btn-primary" type="submit">Add branch</button>
            </form>
            <hr class="my-4"/>
            <h3 class="h6 text-muted mb-2">Current branches</h3>
            <div class="item-stack">
                <c:forEach items="${branches}" var="b">
                    <div class="rail-item">
                        <div class="fw-semibold">${b.name}</div>
                        <div class="small text-muted">${b.address}</div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
    <div class="col-lg-6">
        <div class="detail-block h-100">
            <h2>Add user</h2>
            <p class="small text-muted mb-3">Create staff roles — never use public register for admin accounts.</p>
            <form method="post" action="<c:url value='/admin/users'/>">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="mb-3">
                    <label class="form-label">Full name</label>
                    <input class="form-control" name="fullName" required/>
                </div>
                <div class="mb-3">
                    <label class="form-label">Username</label>
                    <input class="form-control" name="username" required/>
                </div>
                <div class="mb-3">
                    <label class="form-label">Email</label>
                    <input class="form-control" type="email" name="email" required/>
                </div>
                <div class="mb-3">
                    <label class="form-label">Password</label>
                    <input class="form-control" type="password" name="password" required/>
                </div>
                <div class="mb-3">
                    <label class="form-label">Role</label>
                    <select class="form-select" name="role">
                        <c:forEach items="${roles}" var="r">
                            <option value="${r}">${r}</option>
                        </c:forEach>
                    </select>
                </div>
                <button class="btn btn-primary" type="submit">Create user</button>
            </form>
            <p class="small text-muted mt-3 mb-0">${users.size()} users in the system</p>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
