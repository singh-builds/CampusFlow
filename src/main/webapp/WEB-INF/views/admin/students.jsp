<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Students"/><c:set var="nav" value="students"/>
<%@ include file="../common/header.jspf" %>
<div class="card"><div class="table-responsive"><table class="table mb-0 align-middle">
  <thead><tr><th>Roll no</th><th>Name</th><th>Email</th><th>Department</th><th>Registered</th><th>Status</th><th></th></tr></thead><tbody>
  <c:forEach var="s" items="${students}"><tr>
    <td><c:out value="${s.rollNo}"/></td><td class="fw-semibold"><c:out value="${s.fullName}"/></td><td><c:out value="${s.email}"/></td><td><c:out value="${s.department}"/></td>
    <td><fmt:formatDate value="${s.createdAt}" pattern="dd MMM yyyy"/></td><td><span class="badge badge-status st-${s.status}">${s.status}</span></td>
    <td class="text-end"><form method="post" action="${ctx}/admin/students/toggle"><input type="hidden" name="id" value="${s.id}"><input type="hidden" name="active" value="${s.status == 'ACTIVE' ? 0 : 1}">
      <button class="btn btn-sm btn-outline-secondary">${s.status == 'ACTIVE' ? 'Deactivate' : 'Activate'}</button></form></td></tr></c:forEach>
  <c:if test="${empty students}"><tr><td colspan="7" class="text-center text-muted py-4">No students have registered yet.</td></tr></c:if>
  </tbody></table></div></div>
<%@ include file="../common/footer.jspf" %>
