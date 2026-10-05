package com.campusflow.controller;

import com.campusflow.service.AdminService;
import com.campusflow.service.AuthService;
import com.campusflow.util.AppException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/*")
public class AdminServlet extends BaseServlet {
    private final AdminService admin = new AdminService();
    private final AuthService auth = new AuthService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "/dashboard" : req.getPathInfo();
        switch (path) {
            case "/dashboard" -> { req.setAttribute("stats", admin.stats()); req.setAttribute("services", admin.services(false)); view(req, resp, "admin/dashboard"); }
            case "/services" -> { req.setAttribute("services", admin.services(false)); view(req, resp, "admin/services"); }
            case "/staff" -> { req.setAttribute("staffList", admin.staff()); req.setAttribute("services", admin.services(true)); view(req, resp, "admin/staff"); }
            case "/students" -> { req.setAttribute("students", admin.students()); view(req, resp, "admin/students"); }
            case "/activity" -> { req.setAttribute("rows", admin.recentActivity()); view(req, resp, "admin/activity"); }
            default -> resp.sendError(404);
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        switch (path) {
            case "/services/add" -> act(req, resp, "Service added.", "/admin/services",
                    () -> admin.createService(req.getParameter("name"), req.getParameter("prefix"), req.getParameter("description")));
            case "/services/toggle" -> act(req, resp, "Service updated.", "/admin/services",
                    () -> admin.setServiceActive(intParam(req, "id"), "1".equals(req.getParameter("active"))));
            case "/staff/add" -> act(req, resp, "Staff member created.", "/admin/staff", () -> {
                String role = req.getParameter("role");
                String sv = req.getParameter("serviceId");
                Integer serviceId = (sv == null || sv.isBlank()) ? null : Integer.valueOf(sv);
                auth.createStaff(req.getParameter("name"), req.getParameter("email"), req.getParameter("password"), role, serviceId);
            });
            case "/staff/toggle" -> act(req, resp, "Staff updated.", "/admin/staff",
                    () -> admin.setStaffStatus(intParam(req, "id"), "1".equals(req.getParameter("active")) ? "ACTIVE" : "INACTIVE"));
            case "/students/toggle" -> act(req, resp, "Student updated.", "/admin/students",
                    () -> admin.setStudentStatus(intParam(req, "id"), "1".equals(req.getParameter("active")) ? "ACTIVE" : "INACTIVE"));
            default -> resp.sendError(404);
        }
    }
}
