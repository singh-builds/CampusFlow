package com.campusflow.controller;

import com.campusflow.service.AuthService;
import com.campusflow.util.AppException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** First-run page: creates the first administrator with details YOU type. Locked once an admin exists. */
@WebServlet("/setup")
public class SetupServlet extends BaseServlet {
    private final AuthService auth = new AuthService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (!auth.setupNeeded()) { redirect(req, resp, "/login"); return; }
        } catch (AppException e) { req.setAttribute("dbError", e.getMessage()); }
        view(req, resp, "setup");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            auth.createFirstAdmin(req.getParameter("name"), req.getParameter("email"), req.getParameter("password"));
            flash(req, "success", "Administrator created. Please log in.");
            redirect(req, resp, "/login");
        } catch (AppException e) {
            req.setAttribute("error", e.getMessage());
            view(req, resp, "setup");
        }
    }
}
