package org.mrdarkimc.enhancedtextdisplays.displays;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.enhancedtextdisplays.EnhancedTextDisplays;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisplayHandler {
    public static NamespacedKey key = new NamespacedKey(EnhancedTextDisplays.getInstance(),"EnhancedTextDisplay");
    public DisplayHandler() {
        deserealize();
        //handleOldDisplays();
        removeOldDisplays();
        spawnDisplays();
            }
    void removeOldDisplays(){
        for (String key : map.keySet()) {
            Chunk chunk = getDisplayByName(key).getLocation().getChunk();
            boolean isloaded = chunk.isLoaded();
            if (!isloaded) {
                chunk.load();
                //
                Arrays.stream(chunk.getEntities()).filter(entity -> entity.getPersistentDataContainer().has(DisplayHandler.key)).forEach(Entity::remove);
                //
                chunk.unload();
            }else {
                Arrays.stream(chunk.getEntities()).filter(entity -> entity.getPersistentDataContainer().has(DisplayHandler.key)).forEach(Entity::remove);
            }
        }
    }
    void spawnDisplays(){
        map.forEach((k,v) -> v.spawnEntity());
    }
    public void handleOldDisplays(){ //todo load /unload worlds?
        for (World world : Bukkit.getServer().getWorlds()) {
            world.getEntities().stream().filter(e -> e.getPersistentDataContainer().has(key)).forEach(Entity::remove);
        }
    }
    public CustomTextDisplay getDisplayByName(String name){
        return map.get(name);
    }
    public CustomTextDisplay createCustomTextDisplay(Player player, String name){
        ConfigurationSection file = EnhancedTextDisplays.config.get().getConfigurationSection("textdisplays");
        if (file == null) {
            Location loc = player.getLocation();
            loc.setPitch(0);
            CustomTextDisplay textDisplay = new CustomTextDisplay(name,List.of(name),loc,1);
            map.put(name,textDisplay);
            EnhancedTextDisplays.config.get().set("textdisplays." + name,textDisplay);
            return textDisplay;
        }
        if (file.getKeys(false).contains(name)){
        player.sendMessage("[ETD] This name is already taken");
        return null;
        }else{
            Location loc = player.getLocation();
            loc.setPitch(0);
            CustomTextDisplay textDisplay = new CustomTextDisplay(name,List.of(name),loc,1);
            map.put(name,textDisplay);
            EnhancedTextDisplays.config.get().set("textdisplays." + name,textDisplay);
            return textDisplay;
        }
    }
    public void remove(String name){
        map.remove(name);
        EnhancedTextDisplays.config.get().set("textdisplays." + name,null);
    }

    public final Map<String,CustomTextDisplay> map = new HashMap<>();
    public void deserealize(){
        FileConfiguration config = EnhancedTextDisplays.config.get();
        if (config.getConfigurationSection("textdisplays") == null)
            return;
        Set<String> set = config.getConfigurationSection("textdisplays").getKeys(false);
        for (String key : set) {
            List<String> list1 = deserealizeContents(key);
            String world =  config.getString("textdisplays." + key + ".location.world");
            int x = config.getInt("textdisplays." + key + ".location.x");
            int y = config.getInt("textdisplays." + key + ".location.y");
            int z = config.getInt("textdisplays." + key + ".location.z");
            double scale = config.getDouble("textdisplays." + key + ".settings.scale");
            float yaw = (float) config.getDouble("textdisplays." + key + ".location.yaw");
            float pitch = (float) config.getDouble("textdisplays." + key + ".location.pitch");
            map.put(key, new CustomTextDisplay(key,list1,new Location(Bukkit.getWorld(world),x,y,z,yaw,pitch),scale));
        }
        Bukkit.getLogger().info("[EnhancedTextDisplays] Successfully registered " + map.size() + " text-displays");
    }
    public static List<String> deserealizeContents(String key){
        List<String> list1 = EnhancedTextDisplays.config.get().getStringList("textdisplays." + key + ".contents");
        list1.replaceAll(line -> {
            Utils.translateHex(line);
            line = line + "\n";
            PlaceholderAPI.setPlaceholders(null,line);
            return line;
        });
        return list1;
    }
    public static String translateHex(String message) {
        Pattern pattern = Pattern.compile("&#[0-9A-Fa-f]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color.replaceAll("&", "")) + "");
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
//    public void startAsyncTest(Player player){
//        TextDisplay display = map.get(lastUUID);
//        new BukkitRunnable(){
//
//            @Override
//            public void run() {
//                display.teleportAsync(player.getLocation());
//            }
//        }.runTaskTimerAsynchronously()
//
//
//    }

}
