package org.mrdarkimc.enhancedtextdisplays.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.enhancedtextdisplays.EnhancedTextDisplays;
import org.mrdarkimc.enhancedtextdisplays.displays.CustomTextDisplay;
import org.mrdarkimc.enhancedtextdisplays.displays.DisplayHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Command implements CommandExecutor {
    DisplayHandler handler;
    public Command() {
        handler = EnhancedTextDisplays.getInstance().getHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = null;
        ConsoleCommandSender sender = null;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        }
        if (commandSender instanceof ConsoleCommandSender) {
            sender = (ConsoleCommandSender)commandSender;
        }
        ItemStack stack = player.getInventory().getItemInMainHand();
        if (!player.hasPermission("EnhancedTD.admin"))
            return true;
        if (!(strings.length > 0)){
            player.sendMessage(ChatColor.GRAY + "(Только админ-комманды)Список комманд:");
            player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " <зачарование> <уровень> - дать себе книгу");
            player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " all <уровень> - дать себе все книги на уровень");
            player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " give <player> <зачарование> <уровень> - игроку книгу");
            player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " reload - перезагрузить кеш плагина");
            player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " info - показать инфу о чарах");
            player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " list - список зачарований");
            player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " addAll - Добавить весь инвентарь в список разрешенных предметов");
            return true;
        }

        switch (strings[0]){
            case "spawn": //try spawn penus gg gg
                if (player == null)
                    return true;
                StringBuilder builder = new StringBuilder();
                for (int i = 2; i < strings.length; i++) {
                    builder.append(strings[i]);
                    builder.append(" ");
                }
                handler.createCustomTextDisplay(player,strings[1]);
                break;
            case "movehere":
                CustomTextDisplay display = handler.getDisplayByName(strings[1]);
                if (display !=null){
                    display.movehere(player);
                }
                break;
            case "attach":
                handler.getDisplayByName(strings[1]).attach(player);
                break;
            case "update":
                handler.getDisplayByName(strings[1]).deserealizeAndUpdateContents(strings[1]);
                break;
            case "setYaw":
                handler.getDisplayByName(strings[1]).setYaw(Float.parseFloat(strings[2]));
                break;
            case "setPitch":
                handler.getDisplayByName(strings[1]).setPitch(Float.parseFloat(strings[2]));
                break;
            case "setRotation":
                handler.getDisplayByName(strings[1]).setRotation(Float.parseFloat(strings[2]),Float.parseFloat(strings[3]));
                break;
            case "setBackground":
                handler.getDisplayByName(strings[1]).setBackground(Boolean.getBoolean(strings[2]));
                break;
            case "setText":
                StringBuilder builder2 = new StringBuilder();
                for (int i = 2; i < strings.length; i++) {
                    builder2.append(strings[i]);
                    builder2.append(" ");
                }
                handler.getDisplayByName(strings[1]).setText(builder2.toString());
                break;
            case "setScale":
                handler.getDisplayByName(strings[1]).setScale(Float.parseFloat(strings[2]));
                break;
            case "removeNear":
                int radius = Integer.parseInt(strings[1]);
                player.getLocation().getNearbyEntities(radius,radius,radius).stream().filter(entity -> entity.getPersistentDataContainer().has(DisplayHandler.key)).forEach(Entity::remove);
                player.sendMessage("[ETD] Removed. Warning: entities in unloaded chunks may not be deleted");
                break;
                //
        }
        return true;
    }
}
