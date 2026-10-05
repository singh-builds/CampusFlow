package com.campusflow.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/** Authentication + authorization. Runs before every /student, /staff and /admin request. */
@WebFilter({"/student/*", "/staff/*", "/admin/*"})
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String role = session == null ? null : (String) session.getAttribute("role");

        if (role == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }          // not logged in

        String path = req.getServletPath();
        String needed = path.startsWith("/admin") ? "ADMIN" : path.startsWith("/staff") ? "STAFF" : "STUDENT";
        if (!needed.equals(role)) { resp.sendError(HttpServletResponse.SC_FORBIDDEN); return; }     // wrong role

        resp.setHeader("Cache-Control", "no-store"); // back button after logout will not show old pages
        chain.doFilter(request, response);
    }
}
