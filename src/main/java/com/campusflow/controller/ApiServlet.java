package com.campusflow.controller;

import com.campusflow.model.QueueStatus;
import com.campusflow.model.Service;
import com.campusflow.model.Token;
import com.campusflow.service.AdminService;
import com.campusflow.service.TokenService;
import com.campusflow.util.AppException;
import com.campusflow.util.Json;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * REST API (JSON only, read-only).
 *   GET /api/services            list of active services with waiting count   (public)
 *   GET /api/queue/{serviceId}   live queue status                            (public)
 *   GET /api/token/{number}      status + position of one token               (public, no personal data)
 *   GET /api/student/me          the logged-in student's own info             (needs login)
 */
@WebServlet("/api/*")
public class ApiServlet extends HttpServlet {
    private final AdminService admin = new AdminService();
    private final TokenService tokens = new TokenService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String[] p = (req.getPathInfo() == null ? "" : req.getPathInfo()).split("/"); // ["", "queue", "1"]
        try {
            if (p.length == 2 && p[1].equals("services")) {
                List<String> items = new ArrayList<>();
                for (Service s : admin.services(true))
                    items.add(Json.obj("id", s.getId(), "name", s.getName(), "prefix", s.getPrefix(), "description", s.getDescription(), "waiting", s.getWaiting()));
                resp.getWriter().write(Json.arr(items));
            } else if (p.length == 3 && p[1].equals("queue")) {
                QueueStatus q = tokens.queueStatus(Integer.parseInt(p[2]));
                if (q == null) error(resp, 404, "Service not found"); else resp.getWriter().write(q.toJson());
            } else if (p.length == 3 && p[1].equals("token")) {
                Token t = tokens.trackByNumber(p[2]);
                if (t == null) error(resp, 404, "Token not found");
                else resp.getWriter().write(Json.obj("number", t.getNumber(), "service", t.getServiceName(), "status", t.getStatus(), "position", t.getPosition()));
            } else if (p.length == 3 && p[1].equals("student") && p[2].equals("me")) {
                Object id = req.getSession().getAttribute("userId");
                if (id == null || !"STUDENT".equals(req.getSession().getAttribute("role"))) { error(resp, 401, "Login required"); return; }
                List<String> active = new ArrayList<>();
                for (Token t : tokens.activeTokens((Integer) id))
                    active.add(Json.obj("number", t.getNumber(), "service", t.getServiceName(), "status", t.getStatus(), "position", t.getPosition()));
                resp.getWriter().write(Json.obj("name", req.getSession().getAttribute("name"), "activeTokens", Json.raw(Json.arr(active))));
            } else {
                error(resp, 404, "Unknown endpoint");
            }
        } catch (NumberFormatException e) {
            error(resp, 400, "Bad id");
        } catch (AppException e) {
            getServletContext().log("API error", e);
            error(resp, 500, "Server error");
        }
    }

    private void error(HttpServletResponse resp, int code, String msg) throws IOException {
        resp.setStatus(code);
        resp.getWriter().write(Json.obj("error", msg));
    }
}
