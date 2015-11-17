package com.mengcraft.craftnotify;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Timer;

/**
 * Created on 15-11-17.
 */
public class Main extends JavaPlugin {

    private final Timer timer = new Timer("CraftNotify Timer Daemon", true);

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        Daemon daemon = new Daemon(this);

        getServer().getScheduler().runTaskTimer(this, () -> {
            daemon.getTick().addAndGet(20);
        }, 20, 20);

        daemon.setNeedAuth(true);

        daemon.setFrom(getConfig().getString("notify.from"));
        daemon.setHost(getConfig().getString("notify.smtp.host"));
        daemon.setPort(getConfig().getInt("notify.smtp.port"));
        daemon.setUser(getConfig().getString("notify.smtp.user"));
        daemon.setPass(getConfig().getString("notify.smtp.pass"));
        daemon.setSign(getConfig().getBoolean("notify.smtp.sign"));

        getServer().getScheduler().runTask(this, () -> {
            timer.scheduleAtFixedRate(daemon, Daemon.PERIOD, Daemon.PERIOD);
        });

        String[] ad = {
                ChatColor.GREEN + "本插件由梦梦家服务器提供",
                ChatColor.GREEN + "梦梦家高性能服务器出租店",
                ChatColor.GREEN + "shop105595113.taobao.com"
        };
        getServer().getConsoleSender().sendMessage(ad);

        new Metrics(this).start();
    }

}
