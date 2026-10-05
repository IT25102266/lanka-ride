<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Payment successful" scope="request"/>
<c:set var="layoutMode" value="gateway" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="gateway-card text-center">
            <p class="gateway-kicker mb-2"><i class="bi bi-check-circle-fill"></i> Lanka Pay</p>
            <h1 class="h3">Payment successful</h1>
            <p class="text-muted">Booking #${booking.id} is paid. Invoice ${booking.invoiceNumber}.</p>
            <c:if test="${not empty paidBrand}">
                <p class="mb-4">${paidBrand} •••• ${paidLast4}</p>
            </c:if>
            <a class="pay-cta" href="<c:url value='/payments/booking/${booking.id}'/>">View invoice</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
