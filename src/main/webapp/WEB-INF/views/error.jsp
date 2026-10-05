<%@ page isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Error"/>
<%@ include file="common/header.jspf" %>
<div class="text-center py-5">
  <h1 class="display-4"><c:out value="${pageContext.errorData.statusCode}"/></h1>
  <p class="lead">
    <c:choose>
      <c:when test="${pageContext.errorData.statusCode == 403}">You do not have permission to open this page.</c:when>
      <c:when test="${pageContext.errorData.statusCode == 404}">Page not found.</c:when>
      <c:otherwise>Something went wrong on the server.</c:otherwise>
    </c:choose>
  </p>
  <a class="btn btn-primary" href="${ctx}/login">Go to home</a>
</div>
<%@ include file="common/footer.jspf" %>
