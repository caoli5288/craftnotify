package com.mengcraft.craftnotify;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 15-11-17.
 */
public class Daemon extends TimerTask {

    static final int PERIOD = 180000;

    private final Properties prop;
    private final Set<String> send;
    private final Main main;
    private final AtomicInteger tick;

    private PasswordAuthentication authentication;
    private Session session;

    private String serverName;
    public int threshold;

    public Daemon(Main main) {
        this.send = new HashSet<>();
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
                if (send.size() == 0) {
                    message.addRecipients(Message.RecipientType.TO, prop.getProperty("mail.smtp.from"));
                } else for (String line : send) {
                    message.addRecipients(Message.RecipientType.TO, line);
                }
                message.setFrom(prop.getProperty("mail.smtp.from"));

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
        if (serverName == null) try {
            serverName = main.getConfig().getString("server.name");
            serverName = MimeUtility.encodeText(serverName);
        } catch (UnsupportedEncodingException e) {
            main.getLogger().warning("Exception while get server name. " + e);
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

    public void addSend(List<String> list) {
        send.addAll(list);
    }

}
