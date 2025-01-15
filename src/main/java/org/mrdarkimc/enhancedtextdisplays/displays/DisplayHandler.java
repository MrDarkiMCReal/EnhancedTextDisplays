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
    public CustomTextDisplay getDisplayByName(String name){
        return map.get(name);
    }
    public List<String> getListByPage(int page){
        //displays 7 of elements
        page = page-1;
        page = (page*7);
        List<String> displays = new ArrayList<>(map.keySet());
        List<String> newList = new ArrayList<>();
        for (int i = page; i < page+7; i++) {
            if (i < displays.size()) {
                newList.add(displays.get(i));
            }else {
                break;
            }
        }
        return newList;
    }
    public int maxPage(){
      return (int) Math.ceil((double) map.keySet().size() /7);
    }
    public CustomTextDisplay createCustomTextDisplay(Player player, String name, List<String> stringlist){
        ConfigurationSection file = EnhancedTextDisplays.config.get().getConfigurationSection("textdisplays");
        if (file == null){
            file = EnhancedTextDisplays.config.get().createSection("textdisplays");
        }
        if (file.contains(name)){
            player.sendMessage(" ");
            player.sendMessage(Utils.translateHex("               &#1e90ffMrDarkiMC's EnhancedTextDisplays"));
            player.sendMessage(Utils.translateHex("  &c&l| &#D27E7EThis name is already taken"));
            player.sendMessage(" ");
            return null;
        }
        Location loc = player.getLocation();
        loc.setPitch(0);
        CustomTextDisplay textDisplay = new CustomTextDisplay(name,stringlist,loc,1);
        map.put(name,textDisplay);
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".contents",stringlist);
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".settings.scale",1);
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".settings.billboard","fixed");
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".settings.updateTime",-1);
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".settings.alignment","center");
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".settings.lineWidth",250);
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".settings.pivotPoint.x",0);
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".settings.pivotPoint.y",0);
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".settings.pivotPoint.z",0);

        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".location.world",player.getLocation().getWorld().getName());
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".location.x",player.getLocation().getX());
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".location.y",player.getLocation().getY());
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".location.z",player.getLocation().getZ());
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".location.yaw",player.getLocation().getYaw());
        EnhancedTextDisplays.config.get().set("textdisplays." + name + ".location.pitch",player.getLocation().getPitch());
        EnhancedTextDisplays.config.saveConfig();
        textDisplay.spawnEntity();
        return textDisplay;
    }
    public void remove(String name){
        map.get(name).removeFromWorld();
        map.remove(name);
        EnhancedTextDisplays.config.get().set("textdisplays." + name,null);
        EnhancedTextDisplays.config.saveConfig();
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
            double x = config.getDouble("textdisplays." + key + ".location.x");
            double y = config.getDouble("textdisplays." + key + ".location.y");
            double z = config.getDouble("textdisplays." + key + ".location.z");
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
    public static List<String> deserealizeRawContents(String key){
        List<String> list1 = EnhancedTextDisplays.config.get().getStringList("textdisplays." + key + ".contents");
        list1.replaceAll(line -> {
            Utils.translateHex(line);
            line = line + "\n";
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
