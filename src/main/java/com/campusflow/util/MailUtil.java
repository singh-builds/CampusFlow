package com.campusflow.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** JavaMail: sends e-mail notifications in a background thread. Does nothing unless mail.enabled=true in db.properties. */
public final class MailUtil {
    private static final ExecutorService MAIL_POOL = Executors.newFixedThreadPool(2);

    private MailUtil() {}

    public static void sendAsync(String to, String subject, String body) {
        if (!"true".equalsIgnoreCase(DBConnection.get("mail.enabled", "false"))) return;
        MAIL_POOL.submit(() -> {
            try {
                Properties p = new Properties();
                p.put("mail.smtp.host", DBConnection.get("mail.host", ""));
                p.put("mail.smtp.port", DBConnection.get("mail.port", "587"));
                p.put("mail.smtp.auth", "true");
                p.put("mail.smtp.starttls.enable", "true");
                final String user = DBConnection.get("mail.user", "");
                final String pass = DBConnection.get("mail.password", "");
                Session session = Session.getInstance(p, new Authenticator() {
                    @Override protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pass);
                    }
                });
                MimeMessage m = new MimeMessage(session);
                m.setFrom(new InternetAddress(DBConnection.get("mail.from", user)));
                m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                m.setSubject(subject);
                m.setText(body);
                Transport.send(m);
            } catch (Exception e) {
                System.err.println("[CampusFlow] Mail failed: " + e.getMessage());
            }
        });
    }

    public static void shutdown() { MAIL_POOL.shutdown(); }
}
