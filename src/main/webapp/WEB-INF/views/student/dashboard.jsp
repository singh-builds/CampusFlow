<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Dashboard"/><c:set var="nav" value="dashboard"/>
<%@ include file="../common/header.jspf" %>
<div class="row g-3 mb-4">
  <div class="col-sm-6 col-xl-3"><div class="card stat"><div class="card-body d-flex justify-content-between align-items-center">
    <div><div class="text-muted small">Active tokens</div><div class="value">${active.size()}</div></div><i class="bi bi-ticket-perforated icon"></i></div></div></div>
  <div class="col-sm-6 col-xl-3"><a href="${ctx}/student/notifications" class="text-decoration-none"><div class="card stat"><div class="card-body d-flex justify-content-between align-items-center">
    <div><div class="text-muted small">Unread notifications</div><div class="value text-dark">${unread}</div></div><i class="bi bi-bell icon"></i></div></div></a></div>
</div>

<div class="card mb-4"><div class="card-header bg-white fw-semibold">My active tokens</div>
  <div class="table-responsive"><table class="table mb-0 align-middle">
    <thead><tr><th>Token</th><th>Service</th><th>Status</th><th>Position</th><th>Requested</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="t" items="${active}">
      <tr data-token="${t.number}" data-status="${t.status}">
        <td class="fw-bold"><c:out value="${t.number}"/></td><td><c:out value="${t.serviceName}"/></td>
        <td><span class="badge badge-status js-status st-${t.status}">${t.status}</span></td>
        <td class="js-pos"><c:choose><c:when test="${t.status == 'WAITING'}">${t.position}</c:when><c:otherwise>-</c:otherwise></c:choose></td>
        <td><fmt:formatDate value="${t.createdAt}" pattern="dd MMM, hh:mm a"/></td>
        <td class="text-end"><c:if test="${t.status == 'WAITING'}">
          <form method="post" action="${ctx}/student/cancel" onsubmit="return confirm('Cancel this token?')">
            <input type="hidden" name="tokenId" value="${t.id}"><button class="btn btn-sm btn-outline-danger">Cancel</button></form></c:if></td>
      </tr>
    </c:forEach>
    <c:if test="${empty active}"><tr><td colspan="6" class="text-center text-muted py-4">No active tokens. Pick a service below.</td></tr></c:if>
    </tbody></table></div></div>

<h6 class="text-muted text-uppercase mb-3">Available services</h6>
<div class="row g-3">
  <c:forEach var="s" items="${services}">
    <div class="col-md-6 col-xl-4"><div class="card h-100"><div class="card-body d-flex flex-column">
      <div class="d-flex justify-content-between"><h5 class="mb-1"><c:out value="${s.name}"/></h5><span class="badge text-bg-light"><c:out value="${s.prefix}"/></span></div>
      <p class="text-muted small flex-grow-1"><c:out value="${s.description}"/></p>
      <div class="d-flex justify-content-between align-items-center">
        <span class="small"><i class="bi bi-people"></i> ${s.waiting} waiting</span>
        <form method="post" action="${ctx}/student/request"><input type="hidden" name="serviceId" value="${s.id}"><button class="btn btn-primary btn-sm">Get token</button></form>
      </div></div></div></div>
  </c:forEach>
  <c:if test="${empty services}"><div class="col-12"><div class="alert alert-info mb-0">No services are available yet. The admin must add services first.</div></div></c:if>
</div>
<%@ include file="../common/footer.jspf" %>
