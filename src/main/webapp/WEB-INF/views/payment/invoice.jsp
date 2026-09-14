<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="pageTitle" value="Invoice booking #${booking.id}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="mb-3">
    <a class="small text-decoration-none" href="<c:url value='/bookings/${booking.id}'/>">&larr; Back to booking</a>
</div>

<div class="row g-4">
    <div class="col-lg-7">
        <div class="card shadow-sm">
            <div class="card-body">
                <div class="d-flex justify-content-between">
                    <div>
                        <h1 class="h4">Invoice</h1>
                        <p class="text-muted mb-0">
                            <c:out value="${booking.invoiceNumber}" default="Not issued yet"/>
                        </p>
                    </div>
                    <div class="text-end">
                        <div><span class="badge text-bg-secondary">${booking.status}</span></div>
                        <div class="mt-1"><span class="badge text-bg-light border">${booking.paymentStatus}</span></div>
                    </div>
                </div>
                <hr/>
                <dl class="row">
                    <dt class="col-sm-4">Customer</dt>
                    <dd class="col-sm-8">${booking.customer.fullName} (${booking.customer.email})</dd>
                    <dt class="col-sm-4">Vehicle</dt>
                    <dd class="col-sm-8">${booking.vehicle.registrationNumber} — ${booking.vehicle.brand} ${booking.vehicle.model}</dd>
                    <dt class="col-sm-4">Dates</dt>
                    <dd class="col-sm-8">${booking.pickupDate} → ${booking.returnDate}</dd>
                    <dt class="col-sm-4">Deposit</dt>
                    <dd class="col-sm-8">LKR ${booking.vehicle.depositAmount}</dd>
                    <dt class="col-sm-4">Rental estimate</dt>
                    <dd class="col-sm-8">LKR ${rentalAmount}</dd>
                    <dt class="col-sm-4">Total due (pre-pay)</dt>
                    <dd class="col-sm-8 fw-semibold text-primary">LKR ${totalDue}</dd>
                </dl>

                <h2 class="h6">Transactions</h2>
                <table class="table table-sm">
                    <thead><tr><th>Type</th><th>Amount</th><th>Ref</th><th>Note</th></tr></thead>
                    <tbody>
                    <c:forEach items="${payments}" var="p">
                        <tr>
                            <td>${p.type}</td>
                            <td>LKR ${p.amount}</td>
                            <td class="small">${p.gatewayReference}</td>
                            <td class="small">${p.note}</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty payments}">
                        <tr><td colspan="4" class="text-muted">No transactions yet.</td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="col-lg-5">
                <c:if test="${booking.status.name() == 'APPROVED' && booking.paymentStatus.name() == 'PENDING_PAYMENT'}">
            <div class="card shadow-sm mb-3 border-warning">
                <div class="card-body">
                    <h2 class="h6">Pay now (sandbox)</h2>
                    <p class="small text-muted">Approved — awaiting payment before pickup.</p>
                    <form method="post" action="<c:url value='/payments/booking/${booking.id}/pay'/>">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button type="submit" class="btn btn-primary w-100 mb-2">Pay deposit + rental</button>
                    </form>
                    <form method="post" action="<c:url value='/payments/booking/${booking.id}/pay'/>">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <input type="hidden" name="fail" value="true"/>
                        <button type="submit" class="btn btn-outline-danger btn-sm w-100">Simulate gateway fail</button>
                    </form>
                </div>
            </div>
        </c:if>

        <c:if test="${booking.paymentStatus.name() == 'PAID' && booking.status.name() == 'ONGOING' && empty booking.pickupMileage}">
            <div class="alert alert-info py-2 small">Paid — awaiting pickup mileage/fuel checklist.</div>
        </c:if>

        <sec:authorize access="hasAnyRole('ADMIN','BOOKING_SUPERVISOR','OPERATIONS_MANAGER','FLEET_COORDINATOR')">
            <c:if test="${booking.paymentStatus.name() == 'PAID' && booking.status.name() == 'ONGOING' && empty booking.pickupMileage}">
                <div class="card shadow-sm mb-3">
                    <div class="card-body">
                        <h2 class="h6">Record pickup</h2>
                        <form method="post" action="<c:url value='/payments/booking/${booking.id}/pickup'/>">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <div class="mb-2">
                                <label class="form-label small">Mileage</label>
                                <input class="form-control form-control-sm" type="number" name="pickupMileage" required/>
                            </div>
                            <div class="mb-2">
                                <label class="form-label small">Fuel</label>
                                <select class="form-select form-select-sm" name="pickupFuelLevel">
                                    <option>FULL</option><option>3/4</option><option>1/2</option><option>1/4</option><option>EMPTY</option>
                                </select>
                            </div>
                            <button class="btn btn-outline-primary btn-sm" type="submit">Save pickup</button>
                        </form>
                    </div>
                </div>
            </c:if>

            <c:if test="${booking.status.name() == 'ONGOING'}">
                <div class="card shadow-sm mb-3">
                    <div class="card-body">
                        <h2 class="h6">Complete return</h2>
                        <form method="post" action="<c:url value='/payments/booking/${booking.id}/return'/>">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <div class="mb-2">
                                <label class="form-label small">Return mileage</label>
                                <input class="form-control form-control-sm" type="number" name="returnMileage" required/>
                            </div>
                            <div class="mb-2">
                                <label class="form-label small">Return fuel</label>
                                <select class="form-select form-select-sm" name="returnFuelLevel">
                                    <option>FULL</option><option>3/4</option><option>1/2</option><option>1/4</option><option>EMPTY</option>
                                </select>
                            </div>
                            <div class="mb-2">
                                <label class="form-label small">Late fee (LKR)</label>
                                <input class="form-control form-control-sm" type="number" step="0.01" name="lateFee" value="0"/>
                            </div>
                            <div class="mb-2">
                                <label class="form-label small">Damage charge (LKR)</label>
                                <input class="form-control form-control-sm" type="number" step="0.01" name="damageCharge" value="0"/>
                            </div>
                            <button class="btn btn-success btn-sm" type="submit">Complete return</button>
                        </form>
                    </div>
                </div>
            </c:if>
        </sec:authorize>

        <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER')">
            <c:if test="${booking.paymentStatus.name() == 'PAID'}">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h2 class="h6">Refund</h2>
                        <form method="post" action="<c:url value='/payments/booking/${booking.id}/refund'/>">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <div class="mb-2">
                                <label class="form-label small">Reason</label>
                                <input class="form-control form-control-sm" name="reason" required/>
                            </div>
                            <button class="btn btn-outline-danger btn-sm" type="submit">Process refund</button>
                        </form>
                    </div>
                </div>
            </c:if>
        </sec:authorize>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
