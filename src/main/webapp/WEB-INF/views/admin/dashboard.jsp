<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin dashboard"/><c:set var="nav" value="dashboard"/>
<%@ include file="../common/header.jspf" %>
<div class="row g-3 mb-4">
  <div class="col-sm-6 col-xl-3"><div class="card stat"><div class="card-body d-flex justify-content-between"><div><div class="text-muted small">Students</div><div class="value">${stats.students}</div></div><i class="bi bi-mortarboard icon"></i></div></div></div>
  <div class="col-sm-6 col-xl-3"><div class="card stat"><div class="card-body d-flex justify-content-between"><div><div class="text-muted small">Staff</div><div class="value">${stats.staff}</div></div><i class="bi bi-person-badge icon"></i></div></div></div>
  <div class="col-sm-6 col-xl-3"><div class="card stat"><div class="card-body d-flex justify-content-between"><div><div class="text-muted small">Services</div><div class="value">${stats.services}</div></div><i class="bi bi-building icon"></i></div></div></div>
  <div class="col-sm-6 col-xl-3"><div class="card stat"><div class="card-body d-flex justify-content-between"><div><div class="text-muted small">Waiting tokens</div><div class="value">${stats.tokens['WAITING']}</div></div><i class="bi bi-hourglass-split icon"></i></div></div></div>
</div>
<div class="row g-3">
  <div class="col-lg-5"><div class="card"><div class="card-header bg-white fw-semibold">Tokens by status</div><ul class="list-group list-group-flush">
    <c:forEach var="e" items="${stats.tokens}"><li class="list-group-item d-flex justify-content-between"><span class="badge badge-status st-${e.key}">${e.key}</span><strong>${e.value}</strong></li></c:forEach>
  </ul></div></div>
  <div class="col-lg-7"><div class="card"><div class="card-header bg-white fw-semibold">Queues right now</div>
    <div class="table-responsive"><table class="table mb-0"><thead><tr><th>Service</th><th>Status</th><th>Waiting</th></tr></thead><tbody>
    <c:forEach var="s" items="${services}"><tr><td><c:out value="${s.name}"/></td><td><span class="badge badge-status ${s.active ? 'st-ACTIVE' : 'st-INACTIVE'}">${s.active ? 'ACTIVE' : 'INACTIVE'}</span></td><td>${s.waiting}</td></tr></c:forEach>
    <c:if test="${empty services}"><tr><td colspan="3" class="text-center text-muted py-4">No services yet. Add them under Services.</td></tr></c:if>
    </tbody></table></div></div></div>
</div>
<%@ include file="../common/footer.jspf" %>
