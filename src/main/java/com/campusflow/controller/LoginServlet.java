package com.campusflow.controller;

import com.campusflow.model.Staff;
import com.campusflow.model.Student;
import com.campusflow.service.AuthService;
import com.campusflow.util.AppException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private final AuthService auth = new AuthService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("role") != null) { redirect(req, resp, home((String) s.getAttribute("role"))); return; }
        try {
            if (auth.setupNeeded()) { redirect(req, resp, "/setup"); return; }
        } catch (AppException e) { req.setAttribute("error", e.getMessage()); }
        view(req, resp, "login");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email"), password = req.getParameter("password"), as = req.getParameter("as");
        try {
            if ("staff".equals(as)) {
                Staff st = auth.loginStaff(email, password);
                if (st != null) {
                    HttpSession s = start(req);
                    s.setAttribute("userId", st.getId()); s.setAttribute("name", st.getFullName()); s.setAttribute("role", st.getRole());
                    if (st.getServiceId() != null) { s.setAttribute("serviceId", st.getServiceId()); s.setAttribute("serviceName", st.getServiceName()); }
                    redirect(req, resp, home(st.getRole())); return;
                }
            } else {
                Student st = auth.loginStudent(email, password);
                if (st != null) {
                    HttpSession s = start(req);
                    s.setAttribute("userId", st.getId()); s.setAttribute("name", st.getFullName()); s.setAttribute("role", "STUDENT");
                    redirect(req, resp, home("STUDENT")); return;
                }
            }
            req.setAttribute("error", "Wrong email or password.");
        } catch (AppException e) {
            req.setAttribute("error", e.getMessage());
        }
        req.setAttribute("loginAs", as);
        view(req, resp, "login");
    }

    /** New session id after login (protects against session fixation). */
    private HttpSession start(HttpServletRequest req) {
        HttpSession s = req.getSession(true);
        req.changeSessionId();
        s.setMaxInactiveInterval(30 * 60);
        return s;
    }

    static String home(String role) {
        return switch (role) { case "ADMIN" -> "/admin/dashboard"; case "STAFF" -> "/staff/queue"; default -> "/student/dashboard"; };
    }
}
