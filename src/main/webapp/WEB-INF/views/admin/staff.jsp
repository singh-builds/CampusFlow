<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Staff"/><c:set var="nav" value="staff"/>
<%@ include file="../common/header.jspf" %>
<div class="d-flex justify-content-end mb-3"><button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addStaff"><i class="bi bi-plus-lg"></i> Add staff</button></div>
<div class="card"><div class="table-responsive"><table class="table mb-0 align-middle">
  <thead><tr><th>Name</th><th>Email</th><th>Role</th><th>Service</th><th>Status</th><th></th></tr></thead><tbody>
  <c:forEach var="s" items="${staffList}"><tr>
    <td class="fw-semibold"><c:out value="${s.fullName}"/></td><td><c:out value="${s.email}"/></td><td>${s.role}</td><td><c:out value="${s.serviceName}"/></td>
    <td><span class="badge badge-status st-${s.status}">${s.status}</span></td>
    <td class="text-end"><c:if test="${s.id != sessionScope.userId}"><form method="post" action="${ctx}/admin/staff/toggle"><input type="hidden" name="id" value="${s.id}"><input type="hidden" name="active" value="${s.status == 'ACTIVE' ? 0 : 1}">
      <button class="btn btn-sm btn-outline-secondary">${s.status == 'ACTIVE' ? 'Deactivate' : 'Activate'}</button></form></c:if></td></tr></c:forEach>
  </tbody></table></div></div>
<div class="modal fade" id="addStaff" tabindex="-1"><div class="modal-dialog"><form class="modal-content" method="post" action="${ctx}/admin/staff/add">
  <div class="modal-header"><h5 class="modal-title">Add staff member</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
  <div class="modal-body">
    <div class="mb-3"><label class="form-label">Full name</label><input name="name" class="form-control" required></div>
    <div class="mb-3"><label class="form-label">Email</label><input name="email" type="email" class="form-control" required></div>
    <div class="mb-3"><label class="form-label">Password (min 8)</label><input name="password" type="password" minlength="8" class="form-control" required></div>
    <div class="row g-3">
      <div class="col-6"><label class="form-label">Role</label><select name="role" class="form-select"><option value="STAFF">Staff</option><option value="ADMIN">Admin</option></select></div>
      <div class="col-6"><label class="form-label">Service (for staff)</label><select name="serviceId" class="form-select"><option value="">- none -</option>
        <c:forEach var="s" items="${services}"><option value="${s.id}"><c:out value="${s.name}"/></option></c:forEach></select></div>
    </div>
  </div>
  <div class="modal-footer"><button class="btn btn-primary">Save</button></div></form></div></div>
<%@ include file="../common/footer.jspf" %>
