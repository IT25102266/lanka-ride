<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Lanka Pay" scope="request"/>
<c:set var="layoutMode" value="gateway" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="row justify-content-center">
    <div class="col-lg-7">
        <div class="gateway-card">
            <div class="d-flex justify-content-between align-items-start gap-3 mb-3">
                <div>
                    <p class="gateway-kicker mb-1"><i class="bi bi-shield-lock"></i> Secure checkout</p>
                    <h1 class="h4 mb-1">Pay Lanka Ride</h1>
                    <p class="text-muted small mb-0">Booking #${booking.id} · ${booking.vehicle.brand} ${booking.vehicle.model}</p>
                </div>
                <div class="text-end">
                    <div class="small text-muted">Amount</div>
                    <div class="fw-semibold">LKR ${totalDue}</div>
                    <div class="small text-muted">Deposit + rental</div>
                </div>
            </div>

            <form method="post" action="<c:url value='/payments/booking/${booking.id}/checkout'/>">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <fieldset class="mb-3">
                    <legend class="form-label">Card</legend>
                    <div class="d-flex flex-wrap gap-2">
                        <label class="gateway-method">
                            <input type="radio" name="method" value="VISA" <c:if test="${method == null or method == 'VISA'}">checked</c:if> required/>
                            Visa
                        </label>
                        <label class="gateway-method">
                            <input type="radio" name="method" value="MASTERCARD" <c:if test="${method == 'MASTERCARD'}">checked</c:if>/>
                            Mastercard
                        </label>
                        <label class="gateway-method">
                            <input type="radio" name="method" value="AMEX" <c:if test="${method == 'AMEX'}">checked</c:if>/>
                            American Express
                        </label>
                    </div>
                </fieldset>

                <div class="mb-3">
                    <label class="form-label" for="cardNumber">Card number</label>
                    <input class="form-control" id="cardNumber" name="cardNumber" inputmode="numeric" autocomplete="cc-number"
                           maxlength="23" required placeholder="4242 4242 4242 4242"/>
                </div>
                <div class="mb-3">
                    <label class="form-label" for="holder">Name on card</label>
                    <input class="form-control" id="holder" name="holder" autocomplete="cc-name" maxlength="40" required
                           value="<c:out value='${holder}'/>"/>
                </div>
                <div class="row g-3 mb-3">
                    <div class="col-6">
                        <label class="form-label" for="expiry">Expiry</label>
                        <input class="form-control" id="expiry" name="expiry" autocomplete="cc-exp" maxlength="7" required
                               placeholder="MM/YY" value="<c:out value='${expiry}'/>"/>
                    </div>
                    <div class="col-6">
                        <label class="form-label" for="cvv">Security code</label>
                        <input class="form-control" id="cvv" name="cvv" inputmode="numeric" autocomplete="cc-csc"
                               maxlength="4" required placeholder="123"/>
                    </div>
                </div>

                <button type="submit" class="pay-cta w-100 justify-content-center">Pay LKR ${totalDue}</button>
                <p class="small text-muted mt-3 mb-0">
                    Sandbox only. Card details are checked and not stored.
                    <code>4242 4242 4242 4242</code> approves.
                    <code>4000 0000 0000 0002</code> is declined.
                </p>
            </form>
        </div>
        <p class="text-center mt-3 mb-0">
            <a class="back-link" href="<c:url value='/payments/booking/${booking.id}'/>">Cancel and return to the invoice</a>
        </p>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
