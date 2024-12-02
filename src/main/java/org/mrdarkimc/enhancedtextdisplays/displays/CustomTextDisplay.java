package org.mrdarkimc.enhancedtextdisplays.displays;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;
import org.mrdarkimc.SatanicLib.Utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
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

    public CustomTextDisplay(String name, List<String> contents, Location location, double scale) {
        this.name = name;
        this.contents = contents;
        this.location = location;
        this.scale = scale;
        spawnEntity();
    }
    public void spawnEntity(){
        textDisplay = (TextDisplay) location.getWorld().spawnEntity(location, EntityType.TEXT_DISPLAY);
        textDisplay.getPersistentDataContainer().set(DisplayHandler.key, PersistentDataType.BOOLEAN,true);
        StringBuilder builder = new StringBuilder();
        for (String content : contents) {
            builder.append(PlaceholderAPI.setPlaceholders(null,Utils.translateHex(content)));
            builder.append("\n");
        }
        textDisplay.setText(builder.toString());
    }
    public void movehere(Player player){
        //logic to get Entity by its name from config
        Location loc = player.getLocation();
        textDisplay.teleport(loc);
        location = loc;
        //todo serealize

    }
    public void setPitch(float pitch){
        float originalyaw = textDisplay.getLocation().getYaw();
        //double originalpitch = display.getLocation().getPitch();
        textDisplay.setRotation(originalyaw,pitch);
        location = textDisplay.getLocation();
    }
    public void setYaw(float yaw){
        //float originalyaw = display.getLocation().getYaw();
        float originalpitch = textDisplay.getLocation().getPitch();
        textDisplay.setRotation(yaw,originalpitch);
        location = textDisplay.getLocation();
    }
    public void setRotation(float yaw, float pitch){
        textDisplay.setRotation(yaw,pitch);

    }
    public void setText(String text){
        textDisplay.setText(Utils.translateHex(textDisplay.getText().isEmpty() ? text + "\n" : textDisplay.getText() + text + "\n"));
    }
    public void setScale(float size){
        Transformation transfor = textDisplay.getTransformation();
        Vector3f scale =  transfor.getScale();
        scale.set(size);
        textDisplay.setTransformation(transfor);
    }
    public void setBackground(boolean trueOrfalse){
       textDisplay.setDefaultBackground(false);
    }
    public void setLocation(Location newlocation){
        location = newlocation;
    }

public void update(){
        textDisplay.remove();
        spawnEntity();
}

    public void attach(Player player){
        BlockFace face = player.getTargetBlockFace(5);
        if (player.getTargetBlockExact(3)==null) {
            player.sendMessage(ChatColor.RED +  "Block not found. Stay closer"); //todo hardcode
            return;
        }
        Location newlocation = player.getTargetBlockExact(3).getLocation();
        switch (face.name()) {
            //todo добавить разворот по yaw относительно игрока.
            //у игрока нет UP & DOWN
            //todo сначала тп потом сетаем ротейшон
            case "UP":
                player.sendMessage("facing up");
                player.sendMessage("new location: " + newlocation);
                player.sendMessage("current yaw is: " + location.getYaw());
                player.sendMessage("current pitch is: " + location.getPitch());

                newlocation.setZ(newlocation.getY()+0.01);
                textDisplay.teleport(newlocation);


                setPitch(-90);
                setLocation(newlocation);
                break;
            case "DOWN":
                player.sendMessage("facing down");
                player.sendMessage("new location: " + newlocation);
                player.sendMessage("current yaw is: " + location.getYaw());
                player.sendMessage("current pitch is: " + location.getPitch());

                newlocation.setZ(newlocation.getY()-0.05);
                textDisplay.teleport(newlocation);

                setPitch(90);
                setLocation(newlocation);
                break;
            case "EAST":
                player.sendMessage("facing east");
                player.sendMessage("new location: " + newlocation);
                player.sendMessage("current yaw is: " + location.getYaw());
                player.sendMessage("current pitch is: " + location.getPitch());

                newlocation.setX(newlocation.getX()+1.05);
                textDisplay.teleport(newlocation);
                setPitch(0);
                setYaw(270);
                setLocation(newlocation);

                break;
            case "WEST":
                player.sendMessage("facing west");
                player.sendMessage("new location: " + newlocation);
                player.sendMessage("current yaw is: " + location.getYaw());
                player.sendMessage("current pitch is: " + location.getPitch());

                newlocation.setX(newlocation.getX()-0.05);
                textDisplay.teleport(newlocation);
                setPitch(0);
                setYaw(90);
                setLocation(newlocation);
                break;
            case "SOUTH":
                player.sendMessage("facing south");
                player.sendMessage("new location: " + newlocation);
                player.sendMessage("current yaw is: " + location.getYaw());
                player.sendMessage("current pitch is: " + location.getPitch());

                newlocation.setZ(newlocation.getZ()+1.05);
                textDisplay.teleport(newlocation);
                setPitch(0);
                setYaw(360);
                setLocation(newlocation);
                break;
            case "NORTH":
                player.sendMessage("facing north");
                player.sendMessage("new location: " + newlocation);
                player.sendMessage("current yaw is: " + location.getYaw());
                player.sendMessage("current pitch is: " + location.getPitch());

                newlocation.setZ(newlocation.getZ()-0.05);
                textDisplay.teleport(newlocation);
                setPitch(0);
                setYaw(180);
                setLocation(newlocation);
                break;
        }
    }
}
