<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Queue history"/><c:set var="nav" value="history"/>
<%@ include file="../common/header.jspf" %>
<c:if test="${empty sessionScope.serviceId}"><div class="alert alert-warning">No service is assigned to your account yet.</div></c:if>
<%@ include file="../common/historytable.jspf" %>
<%@ include file="../common/footer.jspf" %>
