package com.campusflow.network;

import java.io.*;
import java.net.Socket;

/**
 * Demo TCP client: starts many clients at the same time to show concurrent requests.
 * Run:  java -cp build/WEB-INF/classes com.campusflow.network.QueueClient localhost 9090 <serviceId> <numberOfClients>
 */
public class QueueClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 9090;
        String serviceId = args.length > 2 ? args[2] : "1";
        int clients = args.length > 3 ? Integer.parseInt(args[3]) : 5;

        Thread[] threads = new Thread[clients];
        for (int i = 0; i < clients; i++) {
            final int n = i + 1;
            threads[i] = new Thread(() -> {
                try (Socket s = new Socket(host, port);
                     BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                     PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
                    out.println("QUEUE " + serviceId);
                    System.out.println("Client " + n + " got: " + in.readLine());
                    out.println("QUIT");
                } catch (IOException e) {
                    System.out.println("Client " + n + " error: " + e.getMessage());
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) t.join();

        try (Socket s = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
            out.println("STATS");
            System.out.println("Server stats: " + in.readLine());
        }
    }
}
