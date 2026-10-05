package com.campusflow.controller;

import com.campusflow.service.AuthService;
import com.campusflow.util.AppException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {
    private final AuthService auth = new AuthService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        view(req, resp, "register");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            auth.registerStudent(req.getParameter("rollNo"), req.getParameter("name"), req.getParameter("email"),
                    req.getParameter("phone"), req.getParameter("department"), req.getParameter("password"));
            flash(req, "success", "Registration done. You can log in now.");
            redirect(req, resp, "/login");
        } catch (AppException e) {
            req.setAttribute("error", e.getMessage());
            view(req, resp, "register");
        }
    }
}
