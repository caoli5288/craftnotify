package com.mengcraft.craftnotify;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.util.TimerTask;

/**
 * Created on 15-11-17.
 */
public class Daemon extends TimerTask {

    static final int PERIOD = 180000;

    private final Properties prop;
    private final Main main;

    private PasswordAuthentication authentication;
    private Session session;

    public Daemon(Main main) {
        this.main = main;
        this.prop = new Properties();
    }

    public volatile int tick;

    public void setTick() {
        this.tick = 0;
    }

    @Override
    public void run() {
        int tickPerSecond = tick / (PERIOD / 1000);
        if (tickPerSecond < getThreshold()) {
            MimeMessage message = new MimeMessage(getSession());
            try {
                message.setFrom(prop.getProperty("mail.smtp.from"));
                message.setRecipients(Message.RecipientType.TO, getSendTo());
                message.setSubject("Your server \"" + getServerName() + "\"may be lagged!");
                message.setText("Current TPS is " + tickPerSecond + ", at " + new Date().toString());

                Transport.send(message);
            } catch (MessagingException e) {
                throw new RuntimeException("Exception while send mail!", e);
            }
        }
        setTick();
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
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return getAuthentication();
                }
            });
        }
        return session;
    }

    private String getServerName() {
        return main.getConfig().getString("server.name");
    }

    private int getThreshold() {
        return main.getConfig().getInt("server.notify.lagged.threshold");
    }

    public void setHost(String host) {
        prop.put("mail.smtp.host", host);
    }

    public void setAuth(boolean auth) {
        prop.put("mail.smtp.auth", Boolean.toString(auth));
    }

    public void setOverSSL(boolean b) {
        prop.put("mail.smtp.starttls.enable", Boolean.toString(b));
    }

    public void setUser(String user) {
        prop.put("mail.smtp.from", user);
        prop.put("mail.smtp.user", user.substring(0, user.indexOf('@')));
    }

    public void setPass(String pass) {
        prop.put("mail.smtp.pass", pass);
    }

    public void setSendTo(String sendTo) {
        prop.put("mail.smtp.sendTo", sendTo);
    }

    public String getSendTo() {
        return prop.getProperty("mail.smtp.sendTo");
    }

}
