package com.campusflow.controller;

import com.campusflow.service.AdminService;
import com.campusflow.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Student pages. URL /student/<action>. AuthFilter already checked the role. */
@WebServlet("/student/*")
public class StudentServlet extends BaseServlet {
    private final TokenService tokens = new TokenService();
    private final AdminService catalog = new AdminService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int me = sessionInt(req, "userId");
        String path = req.getPathInfo() == null ? "/dashboard" : req.getPathInfo();
        switch (path) {
            case "/dashboard" -> {
                req.setAttribute("services", catalog.services(true));
                req.setAttribute("active", tokens.activeTokens(me));
                req.setAttribute("unread", tokens.unreadCount(me));
                view(req, resp, "student/dashboard");
            }
            case "/history" -> { req.setAttribute("tokens", tokens.allTokens(me)); view(req, resp, "student/history"); }
            case "/notifications" -> { req.setAttribute("items", tokens.notifications(me, true)); view(req, resp, "student/notifications"); }
            default -> resp.sendError(404);
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int me = sessionInt(req, "userId");
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        switch (path) {
            case "/request" -> act(req, resp, "Token created. Track it below.", "/student/dashboard",
                    () -> tokens.requestToken(me, intParam(req, "serviceId")));
            case "/cancel" -> act(req, resp, "Token cancelled.", "/student/dashboard",
                    () -> tokens.cancelToken(me, intParam(req, "tokenId")));
            default -> resp.sendError(404);
        }
    }
}
