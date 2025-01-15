package org.mrdarkimc.enhancedtextdisplays.commands;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.SatanicLib.TagBuilderGetter;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.SatanicLib.chat.ChatUtils;
import org.mrdarkimc.enhancedtextdisplays.EnhancedTextDisplays;
import org.mrdarkimc.enhancedtextdisplays.displays.CustomTextDisplay;
import org.mrdarkimc.enhancedtextdisplays.displays.DisplayHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Command implements CommandExecutor {
    DisplayHandler myHandler; //todo разобраться что с сылками не так, когда присваиваешь
    //todo EnhancedTextDisplays.instance.handler = new DisplayHandler();
    public Command() {
        myHandler = EnhancedTextDisplays.getInstance().getHandler();

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
            player.sendMessage(" ");
            player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
            player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " create <name> <line1 \\n line2> - create a holo");
            player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " removeNear <radius> - remove dispays !FROM WORLD");
            player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " update <name> - parse text with PAPI");
            player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " list - List of registered Text displays");
            player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " ? - show hologram management commands");
            player.sendMessage(" ");
            return true;
        }

        switch (strings[0].toLowerCase()) {
            case "?":
            case "help":
                if (player == null)
                    return true;
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("                  &#1e90ffManagement:"));
                player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " tp <name> - tp to textDisplay");
                player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " movehere <name> - summon text to yourself");
                player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " look <name> - make a text look at you");
                player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " attach <name> - attach holo to a wall");
                player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " setYaw <name> <yaw> - sets yaw");
                player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " setPitch <name> <pitch> - sets pitch");
                player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " setRotation <name> <yaw> <pitch> - sets rotation");
                player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " setText <name> <updated text> - sets the yaw and pitch");
                player.sendMessage(ChatColor.GRAY + "  /" + command.getName() + " setScale <name> <scale>");
                player.sendMessage(" ");
                return true;

            case "create":
                if (player == null)
                    return true;
                if (strings.length < 3) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBMissing arguments. Specify name and text."));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " create <name> <line1 \\n line2>"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EExample: /" + command.getName() + " create myBoard first line \\n second line"));
                    player.sendMessage(" ");
                    return true;
                }
                StringBuilder builder = new StringBuilder();
                for (int i = 2; i < strings.length; i++) {
                    builder.append(strings[i]);
                    builder.append(" ");
                }
                String name = strings[1];
                List<String> stringlist = Arrays.stream(builder.toString().split("\n")).collect(Collectors.toList());
                EnhancedTextDisplays.instance.handler.createCustomTextDisplay(player, name, stringlist);
                return true;
            case "delete":
                if (player == null)
                    return true;
                if (strings.length < 2) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBMissing arguments. Specify name and text."));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " remove <name>"));
                    player.sendMessage(Utils.translateHex("   &c&l| &#D27E7EWarning: this removes display from config"));
                    player.sendMessage(Utils.translateHex("   &c&l| &#D27E7EIf you want to delete display only from world use:"));
                    player.sendMessage(Utils.translateHex("   &c&l| &#D27E7E/" + command.getName() + " removeNear <radius>"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay display22 = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (display22 != null) {
                    EnhancedTextDisplays.instance.handler.remove(strings[1]);
                    return true;
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBTextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;

            case "reload":
                EnhancedTextDisplays.config.reloadConfig();
                EnhancedTextDisplays.instance.handler.map.forEach((k, v) -> {
                    v.tryTerminateTask();
                    v.removeFromWorld();
                });
                EnhancedTextDisplays.instance.handler.map.clear();
                EnhancedTextDisplays.instance.handler = new DisplayHandler();
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBPlugin reloaded."));
                player.sendMessage(" ");
                return true;

            case "movehere":
                if (player == null)
                    return true;
                if (strings.length < 2) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBTeleports TextDisplay to your position"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EMissing argument. Specify name of text display"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " movehere <name>"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay display = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (display != null) {
                    display.movehere(player);
                    return true;
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBTextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;
            case "billboard":
                if (player == null)
                    return true;
                if (strings.length < 2) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBTeleports TextDisplay to your position"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EMissing argument. Specify name of text display"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " movehere <name>"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay displayg = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (displayg != null) {
                    try {
                        displayg.setBillboard(strings[2]);
                    }catch (IllegalArgumentException e){
                        player.sendMessage(" ");
                        player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                        player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EInvalid billboard: " + strings[2]));
                        player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EAvaliable only: fixed, center, horizontal, vertical"));
                        player.sendMessage(" ");
                        return true;
                    }

                    return true;
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBTextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;

            case "look":
                if (player == null)
                    return true;
                if (strings.length < 2) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBForces a text display look at you"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EMissing argument. Specify name of text display"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " look <name>"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay display2 = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (display2 != null) {
                    display2.lookAtMe(player);
                    player.sendMessage("Display now is looking at you");
                    return true;
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBTextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;

            case "tp":
                if (player == null)
                    return true;
                if (strings.length < 2) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBTeleports you to TextDisplay's position"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EMissing argument. Specify name of text display"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " tp <name>"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay display3 = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (display3 != null) {
                    player.teleport(display3.getLocation());
                    return true;
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBTextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;

            case "list":
                if (player == null)
                    return true;
                int page = 1;
                if (strings.length > 1) {
                    try {
                        page = Integer.parseInt(strings[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(" ");
                        player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                        player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBShows registered text displays"));
                        player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EInvalid page number."));
                        player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " list <page>"));
                        player.sendMessage(" ");
                        return true;
                    }
                }
                List<String> list = EnhancedTextDisplays.instance.handler.getListByPage(page);
                TextComponent header = TagBuilderGetter.get(player, "header", Map.of("{page}", String.valueOf(page)));
                player.sendMessage(header);
                String spaces = "                        ";
                for (String string : list) {
                    StringBuilder spaceBuilder = new StringBuilder();
                    for (int i = 0; i < string.length(); i++) {
                        spaceBuilder.append(" ");
                    }
                    TextComponent format = TagBuilderGetter.get(player, "formattedTextDisplay", Map.of("{name}", string + spaceBuilder.toString() + spaces));
                    player.sendMessage(format);
                }
                TextComponent footer = TagBuilderGetter.get(player, "footer", Map.of("{page}", String.valueOf(page)));
                player.sendMessage(footer);
                break;

            case "attach":
                if (player == null)
                    return true;
                if (strings.length < 2) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBAttach textDisplay to wall you looking at"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EMissing argument. Specify name of text display"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " attach <name>"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay display4 = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (display4 != null) {
                    display4.attach(player);
                    return true;
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBTextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;

            case "setpivot":
                if (player == null)
                    return true;
                if (strings.length < 5) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBSets the position of text display"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EMissing argument. Specify params"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " setPivot <name> x y z"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EExample: /" + command.getName() + " setPivot topKillsDisplay 0.5 0 1.5"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay display5 = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (display5 != null) {
                    display5.setPivotPoint(Float.parseFloat(strings[2]), Float.parseFloat(strings[3]), Float.parseFloat(strings[4]));
                    return true;
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBTextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;

            case "update":
                if (player == null)
                    return true;
                if (strings.length < 2) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBMissing argument. Specify name."));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " update <name>"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay display6 = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (display6 != null) {
                    display6.deserealizeAndUpdateContents(strings[1]);
                    return true;
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBTextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;

            case "setyaw":
                if (player == null)
                    return true;
                if (strings.length < 3) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBSets yaw for a specific textDisplay"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EMissing argument. Specify name and yaw"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " setYaw <name> <yaw>"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay display7 = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (display7 != null) {
                    try {
                        float yaw = Float.parseFloat(strings[2]);
                        display7.setYaw(yaw);
                        return true;
                    } catch (NumberFormatException e) {
                        player.sendMessage(" ");
                        player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                        player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBInvalid number format for yaw"));
                        player.sendMessage(" ");
                    }
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBTextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;

            case "setpitch":
                if (player == null)
                    return true;
                if (strings.length < 3) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBSets pitch for a specific textDisplay"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EMissing argument. Specify name and pitch"));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " setPitch <name> <pitch>"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay display8 = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (display8 != null) {
                    try {
                        float pitch = Float.parseFloat(strings[2]);
                        display8.setPitch(pitch);
                        return true;
                    } catch (NumberFormatException e) {
                        player.sendMessage(" ");
                        player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                        player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBInvalid number format for pitch"));
                        player.sendMessage(" ");
                    }
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBTextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;
            case "setrotation":
                if (player == null)
                    return true;
                if (strings.length < 4) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBMissing argument. Specify name, yaw and pitch."));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " setrotation <name> <yaw> <pitch>"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay display9 = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (display9 != null) {
                    display9.setRotation(Float.parseFloat(strings[2]), Float.parseFloat(strings[3]));
                    return true;
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7ETextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;

            case "setbackground":
                if (player == null)
                    return true;
                if (strings.length < 3) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBMissing argument. Specify name and background."));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " setbackground <name> <true|false>"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay display10 = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (display10 != null) {
                    display10.setBackground(Boolean.parseBoolean(strings[2]));
                    return true;
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7ETextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;

            case "settext":
                if (player == null)
                    return true;
                if (strings.length < 3) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBMissing argument. Specify name and text."));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " settext <name> <text>"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay display11 = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (display11 != null) {
                    StringBuilder textBuilder = new StringBuilder();
                    for (int i = 2; i < strings.length; i++) {
                        textBuilder.append(strings[i]).append(" ");
                    }
                    display11.setText(textBuilder.toString());
                    return true;
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7ETextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;

            case "setscale":
                if (player == null)
                    return true;
                if (strings.length < 3) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBMissing argument. Specify name and scale."));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " setscale <name> <scale>"));
                    player.sendMessage(" ");
                    return true;
                }
                CustomTextDisplay display12 = EnhancedTextDisplays.instance.handler.getDisplayByName(strings[1]);
                if (display12 != null) {
                    display12.setScale(Float.parseFloat(strings[2]));
                    return  true;
                }
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7ETextDisplay: " + strings[1] + " not found"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUse: /" + command.getName() + " list"));
                player.sendMessage(" ");
                return true;

            case "removenear":
                if (player == null)
                    return true;
                if (strings.length < 2) {
                    player.sendMessage(" ");
                    player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                    player.sendMessage(Utils.translateHex("  &#1e90ff&l| &r&#5591CBMissing argument. Specify radius."));
                    player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EUsage: /" + command.getName() + " removenear <radius>"));
                    player.sendMessage(" ");
                    return true;
                }
                int radius = Integer.parseInt(strings[1]);
                player.getLocation().getNearbyEntities(radius, radius, radius).stream()
                        .filter(entity -> entity.getPersistentDataContainer().has(DisplayHandler.key))
                        .forEach(Entity::remove);
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7ECustom displays removed in radius of " + radius + " blocks"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EEntities in unloaded chunks may not be deleted"));
                player.sendMessage(" ");
                return true;
            default:
                player.sendMessage(" ");
                player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
                player.sendMessage(Utils.translateHex("  &c&l| &#D27E7ESubcommand " + strings[0] + " did not found"));
                player.sendMessage(" ");
                return true;
        }


        return true;
    }
}
