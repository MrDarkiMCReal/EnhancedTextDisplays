package org.mrdarkimc.enhancedtextdisplays;

import org.bukkit.plugin.java.JavaPlugin;
import org.mrdarkimc.SatanicLib.SatanicLib;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.SatanicLib.configsetups.Configs;
import org.mrdarkimc.enhancedtextdisplays.commands.Command;
import org.mrdarkimc.enhancedtextdisplays.displays.DisplayHandler;

public final class EnhancedTextDisplays extends JavaPlugin {
    public static EnhancedTextDisplays instance;
    public static EnhancedTextDisplays getInstance() {
        return instance;
    }
    public DisplayHandler handler;
    public static Configs config;
    public static Configs chattags;

    public String commandName;

    public DisplayHandler getHandler() {
        return handler;
    }

    @Override
    public void onEnable() {
        instance = this;
        SatanicLib.setupLib(this);
        commandName = "try";
        config = Configs.Defaults.setupConfig();
        chattags = Configs.Defaults.setupChatTags();

        Utils.startUp("EnhancedTextDisplays");
        handler = new DisplayHandler();
        getServer().getPluginCommand(commandName).setExecutor(new Command());

        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
