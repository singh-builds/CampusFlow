<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="My service history"/><c:set var="nav" value="history"/>
<%@ include file="../common/header.jspf" %>
<div class="card"><div class="table-responsive"><table class="table mb-0">
  <thead><tr><th>Token</th><th>Service</th><th>Status</th><th>Requested</th><th>Finished</th></tr></thead><tbody>
  <c:forEach var="t" items="${tokens}"><tr>
    <td class="fw-bold"><c:out value="${t.number}"/></td><td><c:out value="${t.serviceName}"/></td>
    <td><span class="badge badge-status st-${t.status}">${t.status}</span></td>
    <td><fmt:formatDate value="${t.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></td>
    <td><fmt:formatDate value="${t.completedAt}" pattern="dd MMM yyyy, hh:mm a"/></td></tr></c:forEach>
  <c:if test="${empty tokens}"><tr><td colspan="5" class="text-center text-muted py-4">No history yet.</td></tr></c:if>
  </tbody></table></div></div>
<%@ include file="../common/footer.jspf" %>
