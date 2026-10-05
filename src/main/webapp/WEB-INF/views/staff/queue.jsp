<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="My queue"/><c:set var="nav" value="queue"/><c:set var="autorefresh" value="1"/>
<%@ include file="../common/header.jspf" %>
<c:choose>
<c:when test="${empty sessionScope.serviceId}">
  <div class="alert alert-warning">No service is assigned to your account yet. Please ask the administrator.</div>
</c:when>
<c:otherwise>
<div class="row g-3 mb-4">
  <div class="col-lg-5"><div class="card h-100"><div class="card-body serving">
    <div class="text-muted text-uppercase small">Now serving - <c:out value="${sessionScope.serviceName}"/></div>
    <c:choose>
      <c:when test="${not empty current}">
        <div class="num"><c:out value="${current.number}"/></div>
        <div class="mb-3"><c:out value="${current.studentName}"/></div>
        <div class="d-flex gap-2 justify-content-center">
          <form method="post" action="${ctx}/staff/complete"><input type="hidden" name="tokenId" value="${current.id}"><button class="btn btn-success"><i class="bi bi-check-lg"></i> Complete</button></form>
          <form method="post" action="${ctx}/staff/skip"><input type="hidden" name="tokenId" value="${current.id}"><button class="btn btn-outline-danger"><i class="bi bi-skip-forward"></i> Skip</button></form>
        </div>
      </c:when>
      <c:otherwise>
        <div class="num text-muted">--</div>
        <form method="post" action="${ctx}/staff/next"><button class="btn btn-primary btn-lg"><i class="bi bi-megaphone"></i> Call next</button></form>
      </c:otherwise>
    </c:choose>
  </div></div></div>
  <div class="col-lg-7"><div class="card h-100"><div class="card-header bg-white fw-semibold">Waiting line (${waiting.size()})</div>
    <div class="table-responsive"><table class="table mb-0">
      <thead><tr><th>#</th><th>Token</th><th>Student</th><th>Requested</th></tr></thead><tbody>
      <c:forEach var="t" items="${waiting}"><tr><td>${t.position}</td><td class="fw-bold"><c:out value="${t.number}"/></td>
        <td><c:out value="${t.studentName}"/></td><td><fmt:formatDate value="${t.createdAt}" pattern="hh:mm a"/></td></tr></c:forEach>
      <c:if test="${empty waiting}"><tr><td colspan="4" class="text-center text-muted py-4">Nobody is waiting.</td></tr></c:if>
      </tbody></table></div></div></div>
</div>
</c:otherwise>
</c:choose>
<%@ include file="../common/footer.jspf" %>
