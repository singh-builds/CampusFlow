package com.campusflow.controller;

import com.campusflow.model.Token;
import com.campusflow.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Staff pages. A staff member works only on the service assigned to them by the admin. */
@WebServlet("/staff/*")
public class StaffServlet extends BaseServlet {
    private final TokenService tokens = new TokenService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "/queue" : req.getPathInfo();
        Object sid = req.getSession().getAttribute("serviceId");
        if (sid != null) {
            int serviceId = (Integer) sid;
            switch (path) {
                case "/queue" -> { req.setAttribute("current", tokens.currentToken(serviceId)); req.setAttribute("waiting", tokens.waitingList(serviceId)); }
                case "/history" -> req.setAttribute("rows", tokens.serviceHistory(serviceId));
                default -> { resp.sendError(404); return; }
            }
        }
        view(req, resp, "/history".equals(path) ? "staff/history" : "staff/queue");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object sid = req.getSession().getAttribute("serviceId");
        if (sid == null) { flash(req, "warning", "No service is assigned to you yet. Ask the admin."); redirect(req, resp, "/staff/queue"); return; }
        int me = sessionInt(req, "userId"), serviceId = (Integer) sid;
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        switch (path) {
            case "/next" -> {
                try {
                    Token t = tokens.callNext(me, serviceId);
                    flash(req, t == null ? "info" : "success", t == null ? "No student is waiting." : "Called token " + t.getNumber() + ".");
                } catch (com.campusflow.util.AppException e) { flash(req, "danger", e.getMessage()); }
                redirect(req, resp, "/staff/queue");
            }
            case "/complete" -> act(req, resp, "Token completed.", "/staff/queue", () -> tokens.finish(me, serviceId, intParam(req, "tokenId"), true));
            case "/skip" -> act(req, resp, "Token skipped.", "/staff/queue", () -> tokens.finish(me, serviceId, intParam(req, "tokenId"), false));
            default -> resp.sendError(404);
        }
    }
}
