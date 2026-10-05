package com.campusflow.controller;

import com.campusflow.util.AppException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Shared helpers for all controllers (Controller = the C in MVC). */
public abstract class BaseServlet extends HttpServlet {

    /** Sends the request to a JSP. JSPs live in WEB-INF so they can only be reached through a servlet. */
    protected void view(HttpServletRequest req, HttpServletResponse resp, String name) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + name + ".jsp").forward(req, resp);
    }

    /** One-time message shown on the next page (kept in the session). */
    protected void flash(HttpServletRequest req, String type, String message) {
        req.getSession().setAttribute("flashType", type);
        req.getSession().setAttribute("flashMsg", message);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    protected int intParam(HttpServletRequest req, String name) {
        try { return Integer.parseInt(req.getParameter(name)); }
        catch (Exception e) { throw new AppException("Invalid request."); }
    }

    protected int sessionInt(HttpServletRequest req, String name) {
        return (Integer) req.getSession().getAttribute(name);
    }

    /** Runs an action, shows success or the AppException message, then redirects. */
    protected void act(HttpServletRequest req, HttpServletResponse resp, String okMessage, String backTo, Runnable action) throws IOException {
        try {
            action.run();
            if (okMessage != null) flash(req, "success", okMessage);
        } catch (AppException e) {
            flash(req, "danger", e.getMessage());
            if (e.getCause() != null) getServletContext().log("CampusFlow error", e.getCause());
        }
        redirect(req, resp, backTo);
    }
}
