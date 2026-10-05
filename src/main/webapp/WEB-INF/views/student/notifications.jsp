<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Notifications"/><c:set var="nav" value="notifications"/>
<%@ include file="../common/header.jspf" %>
<div class="card"><ul class="list-group list-group-flush">
  <c:forEach var="n" items="${items}">
    <li class="list-group-item d-flex justify-content-between align-items-start">
      <div><i class="bi bi-bell me-2 text-primary"></i><c:out value="${n.message}"/></div>
      <small class="text-muted text-nowrap ms-3"><fmt:formatDate value="${n.createdAt}" pattern="dd MMM, hh:mm a"/></small>
    </li></c:forEach>
  <c:if test="${empty items}"><li class="list-group-item text-center text-muted py-4">No notifications yet.</li></c:if>
</ul></div>
<%@ include file="../common/footer.jspf" %>
