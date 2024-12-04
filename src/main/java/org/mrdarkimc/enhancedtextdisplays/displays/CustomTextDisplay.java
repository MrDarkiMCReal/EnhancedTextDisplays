package org.mrdarkimc.enhancedtextdisplays.displays;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.enhancedtextdisplays.EnhancedTextDisplays;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class CustomTextDisplay implements Serializable {
    private String name;
    private TextDisplay textDisplay;
    private TextDisplay backside;
    private UUID uuid;
    private List<String> contents;
    private Location location;
    private double scale;

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Location getLocation() {
        return location;
    }
    //todo почему то он пересохраняет конфиг с интами.

    public CustomTextDisplay(String name, List<String> contents, Location location, double scale) {
        this.name = name;
        this.contents = contents;
        this.location = location;
        this.scale = scale;
        //spawnEntity();
    }

    public void spawnEntity() {
        textDisplay = (TextDisplay) location.getWorld().spawnEntity(location, EntityType.TEXT_DISPLAY);
        textDisplay.getPersistentDataContainer().set(DisplayHandler.key, PersistentDataType.BOOLEAN, true);
        setRotation(location.getYaw(),location.getPitch());
        applyText(contents);
    }

    public void applyText(List<String> contents) {
        textDisplay.setText("");
        StringBuilder builder = new StringBuilder();
        for (String content : contents) {
            builder.append(PlaceholderAPI.setPlaceholders(null, Utils.translateHex(content)));
            builder.append("\n");
        }
        textDisplay.setText(builder.toString());
        Bukkit.getLogger().info(" [ETD] - updated");
    }

    public void movehere(Player player) {
        //logic to get Entity by its name from config
        Location loc = player.getLocation();
        textDisplay.teleport(loc);
        setRotation(player.getLocation().getYaw(), 0);
        location = loc;
        //todo serealize
    }

    void saveLocation(Location loc) {
        location = loc;
        FileConfiguration config = EnhancedTextDisplays.config.get();
        Bukkit.getLogger().info("saving key: " + name);
        config.set("textdisplays." + name + ".location.world", loc.getWorld().getName());
        config.set("textdisplays." + name + ".location.x", loc.getX());
        config.set("textdisplays." + name + ".location.y", loc.getY());
        config.set("textdisplays." + name + ".location.z", loc.getZ());
        config.set("textdisplays." + name + ".location.yaw", loc.getYaw());
        config.set("textdisplays." + name + ".location.pitch", loc.getPitch());
        EnhancedTextDisplays.config.saveConfig();

    }

    public void setPitch(float pitch) {
        float originalyaw = textDisplay.getLocation().getYaw();
        //double originalpitch = display.getLocation().getPitch();
        textDisplay.setRotation(originalyaw, pitch);
        saveLocation(textDisplay.getLocation());
    }

    public void setYaw(float yaw) {
        //float originalyaw = display.getLocation().getYaw();
        float originalpitch = textDisplay.getLocation().getPitch();
        textDisplay.setRotation(yaw, originalpitch);
        saveLocation(textDisplay.getLocation());
    }

    public void setRotation(float yaw, float pitch) {
        textDisplay.setRotation(yaw, pitch);
        saveLocation(textDisplay.getLocation());
    }

    public void setText(String text) {
        textDisplay.setText(Utils.translateHex(textDisplay.getText().isEmpty() ? text + "\n" : textDisplay.getText() + text + "\n"));
    }

    public void setScale(float size) {
        Transformation transfor = textDisplay.getTransformation();
        Vector3f scale = transfor.getScale();
        scale.set(size);
        textDisplay.setTransformation(transfor);
    }

    public void setBackground(boolean trueOrfalse) {
        textDisplay.setDefaultBackground(false);
    }

    public void setLocation(Location newlocation) {
        location = newlocation;
    }

    public void deserealizeAndUpdateContents(String name) {
        contents = DisplayHandler.deserealizeContents(name);
        applyText(contents);
    }

    public void attach(Player player) {
        BlockFace face = player.getTargetBlockFace(5);
        if (player.getTargetBlockExact(3) == null) {
            player.sendMessage(ChatColor.RED + "Block not found. Stay closer"); //todo hardcode
            return;
        }
        Location newlocation = player.getTargetBlockExact(3).getLocation();
        switch (face.name()) {
            //todo добавить разворот по yaw относительно игрока.
            //у игрока нет UP & DOWN
            //todo сначала тп потом сетаем ротейшон
            case "UP":
                newlocation.setZ(newlocation.getY() + 0.01);
                textDisplay.teleport(newlocation);


                setPitch(-90);
                newlocation.setPitch(-90);
                setLocation(newlocation);
                saveLocation(newlocation);
                break;
            case "DOWN":
                newlocation.setZ(newlocation.getY() - 0.05);
                textDisplay.teleport(newlocation);

                setPitch(90);
                newlocation.setPitch(90);
                setLocation(newlocation);
                saveLocation(newlocation);
                break;
            case "EAST":
                newlocation.setX(newlocation.getX() + 1.05);
                textDisplay.teleport(newlocation);
                setPitch(0);
                setYaw(270);

                newlocation.setYaw(270);
                newlocation.setPitch(0);
                setLocation(newlocation);
                saveLocation(newlocation);

                break;
            case "WEST":
                newlocation.setX(newlocation.getX() - 0.05);
                textDisplay.teleport(newlocation);
                setPitch(0);
                setYaw(90);
                newlocation.setYaw(90);
                newlocation.setPitch(0);
                setLocation(newlocation);
                saveLocation(newlocation);
                break;
            case "SOUTH":
                newlocation.setZ(newlocation.getZ() + 1.05);
                textDisplay.teleport(newlocation);
                setPitch(0);
                setYaw(360);
                newlocation.setYaw(360);
                newlocation.setPitch(0);
                setLocation(newlocation);
                saveLocation(newlocation);
                break;
            case "NORTH":
                newlocation.setZ(newlocation.getZ() - 0.05);
                textDisplay.teleport(newlocation);
                setPitch(0);
                setYaw(180);
                newlocation.setYaw(180);
                newlocation.setPitch(0);
                setLocation(newlocation);
                saveLocation(newlocation);
                break;
        }
    }
}
