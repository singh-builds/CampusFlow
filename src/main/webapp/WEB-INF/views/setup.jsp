<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="First-time setup"/>
<%@ include file="common/header.jspf" %>
<h5 class="mb-1">First-time setup</h5>
<p class="text-muted small">The database has no administrator yet. Create the first one with your real details. This page locks itself after that.</p>
<c:if test="${not empty dbError}"><div class="alert alert-danger"><c:out value="${dbError}"/></div></c:if>
<form method="post" action="${ctx}/setup">
  <div class="mb-3"><label class="form-label">Full name</label><input name="name" class="form-control" required></div>
  <div class="mb-3"><label class="form-label">Email</label><input name="email" type="email" class="form-control" required></div>
  <div class="mb-3"><label class="form-label">Password (min 8 characters)</label><input name="password" type="password" minlength="8" class="form-control" required></div>
  <button class="btn btn-primary w-100">Create administrator</button>
</form>
<%@ include file="common/footer.jspf" %>
