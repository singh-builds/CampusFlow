package com.campusflow.listener;

import com.campusflow.network.QueueServer;
import com.campusflow.util.DBConnection;
import com.campusflow.util.MailUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/** Starts the TCP server when Tomcat starts the app and stops it when the app stops. */
@WebListener
public class AppListener implements ServletContextListener {
    private QueueServer server;

    @Override public void contextInitialized(ServletContextEvent e) {
        int port = Integer.parseInt(DBConnection.get("tcp.port", "9090"));
        server = new QueueServer(port);
        Thread t = new Thread(server, "campusflow-tcp-acceptor");
        t.setDaemon(true);
        t.start();
    }

    @Override public void contextDestroyed(ServletContextEvent e) {
        if (server != null) server.stop();
        MailUtil.shutdown();
    }
}
