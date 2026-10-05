<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Student registration"/>
<%@ include file="common/header.jspf" %>
<h5 class="mb-3">Student registration</h5>
<form method="post" action="${ctx}/register">
  <div class="row g-3">
    <div class="col-6"><label class="form-label">Roll number</label><input name="rollNo" class="form-control" required></div>
    <div class="col-6"><label class="form-label">Phone</label><input name="phone" class="form-control"></div>
    <div class="col-12"><label class="form-label">Full name</label><input name="name" class="form-control" required></div>
    <div class="col-12"><label class="form-label">Email</label><input name="email" type="email" class="form-control" required></div>
    <div class="col-12"><label class="form-label">Department</label><input name="department" class="form-control"></div>
    <div class="col-12"><label class="form-label">Password (min 8 characters)</label><input name="password" type="password" minlength="8" class="form-control" required></div>
  </div>
  <button class="btn btn-primary w-100 mt-3">Register</button>
</form>
<p class="text-center small mt-3 mb-0">Already registered? <a href="${ctx}/login">Login</a></p>
<%@ include file="common/footer.jspf" %>
