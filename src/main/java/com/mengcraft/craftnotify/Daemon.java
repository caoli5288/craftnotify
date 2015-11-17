package com.mengcraft.craftnotify;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 15-11-17.
 */
public class Daemon extends TimerTask {

    static final int PERIOD = 180000;

    private final Properties prop;
    private final Main main;

    private PasswordAuthentication authentication;
    private Session session;
    private String serverName;
    private AtomicInteger tick;

    public int threshold;

    public Daemon(Main main) {
        this.tick = new AtomicInteger();
        this.main = main;
        this.prop = new Properties();
    }

    @Override
    public void run() {
        float tickPerSecond = tick.getAndSet(0) / (PERIOD / 1000F);
        if (tickPerSecond < getThreshold()) {
            MimeMessage message = new MimeMessage(getSession());
            try {
                message.setFrom(prop.getProperty("mail.smtp.from"));
                message.setRecipients(Message.RecipientType.TO, prop.getProperty("mail.smtp.from"));
                message.setSubject("Your server \"" + getServerName() + "\" may be lagged!");
                message.setText("Current TPS is " + tickPerSecond + ", at " + new Date().toString() + '.');

                Transport.send(message);
            } catch (MessagingException e) {
                throw new RuntimeException("Exception while send mail!", e);
            }
        }
    }

    private PasswordAuthentication getAuthentication() {
        if (authentication == null) {
            authentication = new PasswordAuthentication(
                    prop.getProperty("mail.smtp.user"),
                    prop.getProperty("mail.smtp.pass")
            );
        }
        return authentication;
    }

    private Session getSession() {
        if (session == null) {
            session = Session.getInstance(prop, new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return getAuthentication();
                }
            });
        }
        return session;
    }

    private String getServerName() {
        if (serverName == null) {
            serverName = main.getConfig().getString("server.name");
        }
        return serverName;
    }

    private int getThreshold() {
        if (threshold == 0) {
            threshold = main.getConfig().getInt("server.notify.lagged.threshold");
        }
        return threshold;
    }

    public void setHost(String host) {
        prop.put("mail.smtp.host", host);
    }

    public void setNeedAuth(boolean b) {
        prop.put("mail.smtp.auth", b);
    }

    public void setSign(boolean b) {
        prop.put("mail.smtp.ssl.enable", b);
        prop.put("mail.smtp.starttls.enable", b);
    }

    public void setUser(String user) {
        prop.put("mail.smtp.user", user);
    }

    public void setPass(String pass) {
        prop.put("mail.smtp.pass", pass);
    }

    public AtomicInteger getTick() {
        return tick;
    }

    public void setPort(int port) {
        prop.put("mail.smtp.port", port);
    }

    public void setFrom(String from) {
        prop.put("mail.smtp.from", from);
    }

}
