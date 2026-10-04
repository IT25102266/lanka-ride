<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="pageTitle" value="Home" scope="request"/>
<c:set var="layoutMode" value="marketing" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<section class="hero-full">
    <div class="container hero-inner">
        <c:if test="${not empty message}">
            <div class="alert alert-success alert-dismissible fade show mx-auto" style="max-width:420px;" role="alert">
                <c:out value="${message}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <p class="hero-brand-signal">Lanka Ride</p>
        <h1 class="hero-title">Your next adventure <em>starts here.</em></h1>
        <p class="hero-kicker">
            <span>Sri Lanka's</span>
            <span class="hash">#1</span>
            <span>car rental platform</span>
        </p>

        <div class="search-panel mt-4">
            <h2>Find your perfect ride</h2>
            <form method="get" action="<c:url value='/vehicles'/>">
                <div class="mb-3">
                    <label class="form-label" for="branchId">Pickup location *</label>
                    <select class="form-select" id="branchId" name="branchId" required>
                        <option value="">Select branch</option>
                        <c:forEach items="${branches}" var="b">
                            <option value="${b.id}">${b.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="row g-3 mb-3">
                    <div class="col-6">
                        <label class="form-label" for="pickupDate">From</label>
                        <input class="form-control" type="date" id="pickupDate" name="pickupDate"
                               value="${pickupDate}" required/>
                    </div>
                    <div class="col-6">
                        <label class="form-label" for="returnDate">To</label>
                        <input class="form-control" type="date" id="returnDate" name="returnDate"
                               value="${returnDate}" required/>
                    </div>
                </div>
                <div class="form-check mb-2">
                    <input class="form-check-input" type="checkbox" id="availableOnly" name="availableOnly" value="true" checked/>
                    <label class="form-check-label small" for="availableOnly">Show available vehicles only</label>
                </div>
                <button type="submit" class="btn btn-search">
                    <i class="bi bi-search"></i> Search
                </button>
            </form>
        </div>
    </div>
</section>

<section class="section-quiet">
    <div class="container">
        <h2>Branches across the island</h2>
        <p class="text-muted mb-4">One fleet. Three depots. Seamless handovers.</p>
        <div class="branch-strip">
            <div class="branch-item">
                <h3>Colombo</h3>
                <p>Fort depot — city cars, sedans and airport transfers.</p>
            </div>
            <div class="branch-item">
                <h3>Kandy</h3>
                <p>Hill-country ready SUVs for cooler roads.</p>
            </div>
            <div class="branch-item">
                <h3>Galle</h3>
                <p>Southern vans and coastal trip rentals.</p>
            </div>
        </div>
    </div>
</section>

<section class="section-quiet pt-0">
    <div class="container">
        <h2>How it works</h2>
        <p class="text-muted mb-4">Search, book, drive — with staff approval and secure payment.</p>
        <div class="step-row">
            <div class="step-item">
                <div class="num">01</div>
                <h3 class="h5 mt-2">Search</h3>
                <p class="text-muted small mb-0">Pick a branch and dates. We hide cars under maintenance or already booked.</p>
            </div>
            <div class="step-item">
                <div class="num">02</div>
                <h3 class="h5 mt-2">Book &amp; pay</h3>
                <p class="text-muted small mb-0">Submit a request, get approved, then pay deposit and rental online.</p>
            </div>
            <div class="step-item">
                <div class="num">03</div>
                <h3 class="h5 mt-2">Drive</h3>
                <p class="text-muted small mb-0">Pick up at your branch and return when your trip ends.</p>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
