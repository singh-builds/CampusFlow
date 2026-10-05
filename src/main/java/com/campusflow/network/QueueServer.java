package com.campusflow.network;

import com.campusflow.model.QueueStatus;
import com.campusflow.service.TokenService;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TCP server (client/server architecture) for live queue information.
 * - ServerSocket accepts connections; each client is handled by a thread from an ExecutorService pool.
 * - A synchronized counter shows thread-safe shared data.
 *
 * Text protocol (one command per line):  PING | QUEUE <serviceId> | STATS | QUIT
 */
public class QueueServer implements Runnable {
    private final int port;
    private final ExecutorService pool = Executors.newFixedThreadPool(8);
    private final TokenService tokens = new TokenService();
    private final AtomicInteger activeClients = new AtomicInteger();
    private int requestsServed = 0;                  // shared by all handler threads -> must be synchronized
    private volatile boolean running = true;
    private ServerSocket serverSocket;

    public QueueServer(int port) { this.port = port; }

    private synchronized void countRequest() { requestsServed++; }
    private synchronized int served() { return requestsServed; }

    @Override
    public void run() {
        try (ServerSocket ss = new ServerSocket(port)) {
            serverSocket = ss;
            System.out.println("[CampusFlow] TCP queue server listening on port " + port);
            while (running) {
                Socket client = ss.accept();
                pool.submit(() -> handle(client));      // one pool thread per connected client
            }
        } catch (IOException e) {
            if (running) System.err.println("[CampusFlow] TCP server stopped: " + e.getMessage());
        }
    }

    private void handle(Socket socket) {
        activeClients.incrementAndGet();
        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                countRequest();
                switch (parts[0].toUpperCase()) {
                    case "PING" -> out.println("PONG");
                    case "STATS" -> out.println("{\"requestsServed\":" + served() + ",\"activeClients\":" + activeClients.get() + "}");
                    case "QUEUE" -> out.println(queue(parts));
                    case "QUIT" -> { out.println("BYE"); return; }
                    default -> out.println("{\"error\":\"Unknown command\"}");
                }
            }
        } catch (IOException ignored) {
            // client disconnected
        } finally {
            activeClients.decrementAndGet();
        }
    }

    private String queue(String[] parts) {
        try {
            QueueStatus q = tokens.queueStatus(Integer.parseInt(parts[1]));
            return q == null ? "{\"error\":\"Service not found\"}" : q.toJson();
        } catch (Exception e) {
            return "{\"error\":\"Usage: QUEUE <serviceId>\"}";
        }
    }

    public void stop() {
        running = false;
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) { }
        pool.shutdownNow();
    }
}
