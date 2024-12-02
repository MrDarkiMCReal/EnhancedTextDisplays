package org.mrdarkimc.enhancedtextdisplays;

import org.bukkit.plugin.java.JavaPlugin;
import org.mrdarkimc.SatanicLib.SatanicLib;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.SatanicLib.configsetups.Configs;
import org.mrdarkimc.enhancedtextdisplays.commands.Command;
import org.mrdarkimc.enhancedtextdisplays.displays.DisplayHandler;
import org.mrdarkimc.enhancedtextdisplays.displays.TDisplay;

public final class EnhancedTextDisplays extends JavaPlugin {
    public static EnhancedTextDisplays instance;
    public static EnhancedTextDisplays getInstance() {
        return instance;
    }
    public DisplayHandler handler;
    public static Configs config;

    public DisplayHandler getHandler() {
        return handler;
    }

    @Override
    public void onEnable() {
        instance = this;
        SatanicLib.setupLib(this);
        config = Configs.Defaults.setupConfig();

        Utils.startUp("EnhancedTextDisplays");
        handler = new DisplayHandler();
        getServer().getPluginCommand("try").setExecutor(new Command());

        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
