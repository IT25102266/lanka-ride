<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>${pageTitle} | Lanka Ride</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>"/>
</head>
<body class="d-flex flex-column min-vh-100">
<c:set var="isMarketing" value="${layoutMode == 'marketing'}"/>
<c:choose>
<c:when test="${layoutMode == 'gateway'}">
<nav class="navbar navbar-dark nav-app">
    <div class="container d-flex justify-content-between">
        <span class="navbar-brand brand-mark mb-0"><i class="bi bi-lock-fill"></i> Lanka Pay</span>
        <span class="navbar-text small">Secure payment</span>
    </div>
</nav>
</c:when>
<c:otherwise>
<nav class="navbar navbar-expand-lg <c:choose><c:when test='${isMarketing}'>nav-marketing</c:when><c:otherwise>nav-app</c:otherwise></c:choose>">
    <div class="container">
        <a class="navbar-brand brand-mark" href="<c:url value='/'/>">Lanka Ride</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
                aria-controls="mainNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav nav-center mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="<c:url value='/'/>">Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<c:url value='/vehicles'/>">Find a ride</a>
                </li>
                <sec:authorize access="hasRole('CUSTOMER') and !hasAnyRole('ADMIN','BOOKING_SUPERVISOR','FLEET_COORDINATOR','FINANCE_MANAGER','OPERATIONS_MANAGER')">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/app'/>">My trips</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/bookings'/>">Bookings</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/support'/>">Support</a>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAnyRole('ADMIN','BOOKING_SUPERVISOR','FLEET_COORDINATOR','FINANCE_MANAGER','OPERATIONS_MANAGER')">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">Operations</a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="<c:url value='/dashboard'/>">Dashboard</a></li>
                            <li><a class="dropdown-item" href="<c:url value='/bookings'/>">Bookings</a></li>
                            <sec:authorize access="hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')">
                                <li><a class="dropdown-item" href="<c:url value='/bookings/monitor'/>">Monitor</a></li>
                            </sec:authorize>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAnyRole('ADMIN','FLEET_COORDINATOR','OPERATIONS_MANAGER')">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">Fleet</a>
                        <ul class="dropdown-menu">
                            <sec:authorize access="hasAnyRole('ADMIN','FLEET_COORDINATOR')">
                                <li><a class="dropdown-item" href="<c:url value='/maintenance'/>">Maintenance</a></li>
                            </sec:authorize>
                            <li><a class="dropdown-item" href="<c:url value='/vehicles/new'/>">Add vehicle</a></li>
                            <sec:authorize access="hasAnyRole('ADMIN','OPERATIONS_MANAGER')">
                                <li><a class="dropdown-item" href="<c:url value='/dashboard/catalog'/>">Defaults</a></li>
                            </sec:authorize>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">Finance</a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="<c:url value='/payments'/>">Payments</a></li>
                            <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER','OPERATIONS_MANAGER')">
                                <li><a class="dropdown-item" href="<c:url value='/reports'/>">Reports</a></li>
                            </sec:authorize>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER')">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">More</a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="<c:url value='/support'/>">Support</a></li>
                            <sec:authorize access="hasRole('ADMIN')">
                                <li><a class="dropdown-item" href="<c:url value='/admin'/>">Admin</a></li>
                                <li><a class="dropdown-item" href="<c:url value='/support/notifications'/>">Alerts</a></li>
                            </sec:authorize>
                        </ul>
                    </li>
                </sec:authorize>
            </ul>
            <ul class="navbar-nav ms-auto align-items-lg-center gap-lg-2">
                <sec:authorize access="isAuthenticated()">
                    <li class="nav-item">
                        <span class="navbar-text small">
                            <i class="bi bi-person-circle"></i>
                            <sec:authentication property="name"/>
                        </span>
                    </li>
                    <li class="nav-item">
                        <form method="post" action="<c:url value='/logout'/>" class="d-inline">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button type="submit" class="btn btn-outline-secondary">Logout</button>
                        </form>
                    </li>
                </sec:authorize>
                <sec:authorize access="!isAuthenticated()">
                    <li class="nav-item">
                        <a class="btn-dark-pill" href="<c:url value='/login'/>">Login</a>
                    </li>
                </sec:authorize>
            </ul>
        </div>
    </div>
</nav>
</c:otherwise>
</c:choose>
<main class="flex-grow-1 ${isMarketing ? '' : ''}">
    <c:choose>
        <c:when test="${isMarketing}">
            <%-- hero owns full bleed; flashes rendered inside home --%>
        </c:when>
        <c:otherwise>
            <div class="container page-shell">
                <c:if test="${not empty message}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <c:out value="${message}"/>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <c:out value="${error}"/>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
        </c:otherwise>
    </c:choose>
