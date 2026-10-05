<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login"/>
<%@ include file="common/header.jspf" %>
<form method="post" action="${ctx}/login">
  <div class="btn-group w-100 mb-3" role="group">
    <input type="radio" class="btn-check" name="as" id="asStudent" value="student" ${loginAs != 'staff' ? 'checked' : ''}>
    <label class="btn btn-outline-primary" for="asStudent"><i class="bi bi-mortarboard"></i> Student</label>
    <input type="radio" class="btn-check" name="as" id="asStaff" value="staff" ${loginAs == 'staff' ? 'checked' : ''}>
    <label class="btn btn-outline-primary" for="asStaff"><i class="bi bi-person-badge"></i> Staff / Admin</label>
  </div>
  <div class="mb-3"><label class="form-label">Email</label><input name="email" type="email" class="form-control" required autofocus></div>
  <div class="mb-3"><label class="form-label">Password</label><input name="password" type="password" class="form-control" required></div>
  <button class="btn btn-primary w-100">Login</button>
</form>
<p class="text-center small mt-3 mb-0">New student? <a href="${ctx}/register">Register here</a></p>
<%@ include file="common/footer.jspf" %>
