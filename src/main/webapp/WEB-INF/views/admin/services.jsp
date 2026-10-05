<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Services"/><c:set var="nav" value="services"/>
<%@ include file="../common/header.jspf" %>
<div class="d-flex justify-content-end mb-3"><button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addService"><i class="bi bi-plus-lg"></i> Add service</button></div>
<div class="card"><div class="table-responsive"><table class="table mb-0 align-middle">
  <thead><tr><th>Name</th><th>Token prefix</th><th>Description</th><th>Waiting</th><th>Status</th><th></th></tr></thead><tbody>
  <c:forEach var="s" items="${services}"><tr>
    <td class="fw-semibold"><c:out value="${s.name}"/></td><td><c:out value="${s.prefix}"/></td><td><c:out value="${s.description}"/></td><td>${s.waiting}</td>
    <td><span class="badge badge-status ${s.active ? 'st-ACTIVE' : 'st-INACTIVE'}">${s.active ? 'ACTIVE' : 'INACTIVE'}</span></td>
    <td class="text-end"><form method="post" action="${ctx}/admin/services/toggle"><input type="hidden" name="id" value="${s.id}"><input type="hidden" name="active" value="${s.active ? 0 : 1}">
      <button class="btn btn-sm btn-outline-secondary">${s.active ? 'Disable' : 'Enable'}</button></form></td></tr></c:forEach>
  <c:if test="${empty services}"><tr><td colspan="6" class="text-center text-muted py-4">No services yet. Click "Add service".</td></tr></c:if>
  </tbody></table></div></div>
<div class="modal fade" id="addService" tabindex="-1"><div class="modal-dialog"><form class="modal-content" method="post" action="${ctx}/admin/services/add">
  <div class="modal-header"><h5 class="modal-title">Add service</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
  <div class="modal-body">
    <div class="mb-3"><label class="form-label">Service name</label><input name="name" class="form-control" required></div>
    <div class="mb-3"><label class="form-label">Token prefix (2-5 letters)</label><input name="prefix" class="form-control" pattern="[A-Za-z]{2,5}" maxlength="5" required></div>
    <div class="mb-3"><label class="form-label">Description</label><input name="description" class="form-control" maxlength="255"></div>
  </div>
  <div class="modal-footer"><button class="btn btn-primary">Save</button></div></form></div></div>
<%@ include file="../common/footer.jspf" %>
