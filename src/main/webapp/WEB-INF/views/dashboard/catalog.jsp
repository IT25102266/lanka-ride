<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Fleet defaults" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="dashboard-page">
<div class="mb-3">
    <a class="back-link" href="<c:url value='/dashboard'/>">&larr; Back to dashboard</a>
</div>

<div class="page-head">
    <div>
        <h1 class="page-title">Fleet defaults</h1>
        <p class="page-lead">Categories, models, and service types used when staff add vehicles or maintenance. Deleting a default does not delete vehicles already saved.</p>
    </div>
</div>

<div class="row g-4">
    <div class="col-lg-4">
        <div class="detail-block h-100">
            <h2>Categories</h2>
            <c:forEach items="${categories}" var="item">
                <div class="d-flex justify-content-between align-items-center gap-2 border-bottom py-2">
                    <span>${item.name}</span>
                    <span class="d-flex gap-1">
                        <a class="btn btn-outline-secondary btn-sm" href="<c:url value='/dashboard/catalog?edit=${item.id}'/>">Edit</a>
                        <form method="post" action="<c:url value='/dashboard/catalog/${item.id}/delete'/>" onsubmit="return confirm('Delete this category default?');">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button class="btn btn-outline-danger btn-sm" type="submit">Delete</button>
                        </form>
                    </span>
                </div>
            </c:forEach>
            <c:if test="${empty categories}"><p class="text-muted small">No categories yet.</p></c:if>
        </div>
    </div>
    <div class="col-lg-4">
        <div class="detail-block h-100">
            <h2>Models</h2>
            <c:forEach items="${models}" var="item">
                <div class="d-flex justify-content-between align-items-center gap-2 border-bottom py-2">
                    <span>${item.brand} ${item.name}<br/><span class="small text-muted">${item.categoryName} · ${item.seats} seats · ${item.gearbox} · ${item.fuelType}</span></span>
                    <span class="d-flex gap-1">
                        <a class="btn btn-outline-secondary btn-sm" href="<c:url value='/dashboard/catalog?edit=${item.id}'/>">Edit</a>
                        <form method="post" action="<c:url value='/dashboard/catalog/${item.id}/delete'/>" onsubmit="return confirm('Delete this model default?');">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button class="btn btn-outline-danger btn-sm" type="submit">Delete</button>
                        </form>
                    </span>
                </div>
            </c:forEach>
            <c:if test="${empty models}"><p class="text-muted small">No models yet.</p></c:if>
        </div>
    </div>
    <div class="col-lg-4">
        <div class="detail-block h-100">
            <h2>Service types</h2>
            <c:forEach items="${serviceTypes}" var="item">
                <div class="d-flex justify-content-between align-items-center gap-2 border-bottom py-2">
                    <span>${item.name}</span>
                    <span class="d-flex gap-1">
                        <a class="btn btn-outline-secondary btn-sm" href="<c:url value='/dashboard/catalog?edit=${item.id}'/>">Edit</a>
                        <form method="post" action="<c:url value='/dashboard/catalog/${item.id}/delete'/>" onsubmit="return confirm('Delete this service type?');">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button class="btn btn-outline-danger btn-sm" type="submit">Delete</button>
                        </form>
                    </span>
                </div>
            </c:forEach>
            <c:if test="${empty serviceTypes}"><p class="text-muted small">No service types yet.</p></c:if>
        </div>
    </div>
</div>

<div class="detail-block mt-4">
    <h2><c:choose><c:when test="${editing != null}">Edit default</c:when><c:otherwise>Add a default</c:otherwise></c:choose></h2>
    <c:choose>
        <c:when test="${editing != null}">
            <c:url var="saveUrl" value="/dashboard/catalog/${editing.id}"/>
        </c:when>
        <c:otherwise>
            <c:url var="saveUrl" value="/dashboard/catalog"/>
        </c:otherwise>
    </c:choose>
    <form method="post" action="${saveUrl}" class="row g-3">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div class="col-md-4">
            <label class="form-label" for="kind">Type</label>
            <select class="form-select" id="kind" name="kind" required>
                <option value="CATEGORY" <c:if test="${editing.kind.name() == 'CATEGORY'}">selected</c:if>>Category</option>
                <option value="MODEL" <c:if test="${editing == null or editing.kind.name() == 'MODEL'}">selected</c:if>>Model</option>
                <option value="SERVICE_TYPE" <c:if test="${editing.kind.name() == 'SERVICE_TYPE'}">selected</c:if>>Service type</option>
            </select>
        </div>
        <div class="col-md-4">
            <label class="form-label" for="name">Name</label>
            <input class="form-control" id="name" name="name" required minlength="2" maxlength="50"
                   value="<c:out value='${editing.name}'/>"/>
        </div>
        <div class="col-md-4 model-only">
            <label class="form-label" for="brand">Brand</label>
            <input class="form-control" id="brand" name="brand" maxlength="50" value="<c:out value='${editing.brand}'/>"/>
        </div>
        <div class="col-md-4 model-only">
            <label class="form-label" for="categoryName">Category</label>
            <input class="form-control" id="categoryName" name="categoryName" maxlength="50" list="categoryNames"
                   value="<c:out value='${editing.categoryName}'/>"/>
            <datalist id="categoryNames">
                <c:forEach items="${categories}" var="item">
                    <option value="${item.name}"></option>
                </c:forEach>
            </datalist>
        </div>
        <div class="col-md-2 model-only">
            <label class="form-label" for="seats">Seats</label>
            <input class="form-control" id="seats" name="seats" type="number" min="1" max="20" value="${editing.seats}"/>
        </div>
        <div class="col-md-3 model-only">
            <label class="form-label" for="gearbox">Gearbox</label>
            <select class="form-select" id="gearbox" name="gearbox">
                <c:forEach items="${gearboxes}" var="g">
                    <option value="${g}" <c:if test="${editing.gearbox == g}">selected</c:if>>${g}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-3 model-only">
            <label class="form-label" for="fuelType">Fuel</label>
            <select class="form-select" id="fuelType" name="fuelType">
                <c:forEach items="${fuelTypes}" var="f">
                    <option value="${f}" <c:if test="${editing.fuelType == f}">selected</c:if>>${f}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-12 d-flex gap-2">
            <button class="btn btn-primary" type="submit">${editing != null ? 'Update' : 'Add'}</button>
            <c:if test="${editing != null}">
                <a class="btn btn-outline-secondary" href="<c:url value='/dashboard/catalog'/>">Cancel</a>
            </c:if>
        </div>
    </form>
</div>

<script>
    (function () {
        var kind = document.getElementById("kind");
        function toggle() {
            var show = kind.value === "MODEL";
            document.querySelectorAll(".model-only").forEach(function (el) {
                el.hidden = !show;
                el.querySelectorAll("input, select").forEach(function (input) {
                    input.disabled = !show;
                    input.required = show && (input.name === "brand" || input.name === "categoryName" || input.name === "seats");
                });
            });
        }
        kind.addEventListener("change", toggle);
        toggle();
    })();
</script>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
