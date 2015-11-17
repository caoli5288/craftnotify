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
            daemon.tick += 20;
        }, 20, 20);

        daemon.setAuth(true);

        daemon.setSendTo(getConfig().getString("notify.sendTo"));
        daemon.setHost(getConfig().getString("notify.account.host"));
        daemon.setUser(getConfig().getString("notify.account.user"));
        daemon.setPass(getConfig().getString("notify.account.pass"));
        daemon.setOverSSL(getConfig().getBoolean("notify.account.overSSL"));

        timer.scheduleAtFixedRate(daemon, Daemon.PERIOD, Daemon.PERIOD);

        String[] ad = {
                ChatColor.GREEN + "本插件由梦梦家服务器提供",
                ChatColor.GREEN + "梦梦家高性能服务器出租店",
                ChatColor.GREEN + "shop105595113.taobao.com"
        };
        getServer().getConsoleSender().sendMessage(ad);
    }

}
