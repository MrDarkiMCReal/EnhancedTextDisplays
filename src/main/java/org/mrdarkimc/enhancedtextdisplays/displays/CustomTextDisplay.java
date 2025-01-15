package org.mrdarkimc.enhancedtextdisplays.displays;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.SatanicLib.loops.ExtendedTimerTask;
import org.mrdarkimc.enhancedtextdisplays.EnhancedTextDisplays;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomTextDisplay implements Serializable {
    private String name;
    private TextDisplay textDisplay;
    private TextDisplay backside;
    private UUID uuid;
    private List<String> contents;
    private Location location;
    private ConfigurationSection section;

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    ExtendedTimerTask updateTask;

    public Location getLocation() {
        return location;
    }
    //todo почему то он пересохраняет конфиг с интами.

    public CustomTextDisplay(String name, List<String> contents, Location location, double scale) {
        this.name = name;
        this.contents = contents;
        this.location = location;
        this.section = EnhancedTextDisplays.config.get().createSection("textdisplays." + name);
        this.updateTask = setupUpdateTask();
        startTimer();
    }

    private ExtendedTimerTask setupUpdateTask() {
//        if (updateTask!=null){
//            updateTask.stop();
//        }
        String str = EnhancedTextDisplays.config.get().getString("textdisplays." + name + ".settings.updateTime");
        if (str == null || str.equals("-1"))
            return null;
        int updatetime = Integer.parseInt(str);
        return new ExtendedTimerTask(new BukkitRunnable() {
            @Override
            public void run() {
                deserealizeAndUpdateContents(name);
            }
        }, updatetime, updatetime);
    }

    public void tryTerminateTask() {
        if (updateTask == null)
            return;
        updateTask.stop();


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomTextDisplay that = (CustomTextDisplay) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public void lookAtMe(Player player) {

        setRotation((player.getLocation().getYaw() + 180), (player.getLocation().getPitch() * -1));
        removeFromWorld();
        spawnEntity();
    }

    public void spawnEntity() {
        textDisplay = (TextDisplay) location.getWorld().spawnEntity(location, EntityType.TEXT_DISPLAY); //todo вот тут оно спавнится в номинале
        textDisplay.getPersistentDataContainer().set(DisplayHandler.key, PersistentDataType.BOOLEAN, true);

        applyText(contents);
        applyBillboard();
        setAlightment(section.getString("settings.alignment", "center"));
        lineWidth(section.getInt("settings.lineWidth", 250));
        setDefaultPivotPoint();
        setDefaultScale();
    }

    public void lineWidth(int width) {
        textDisplay.setLineWidth(width);
        section.set("settings.lineWidth", width);
        EnhancedTextDisplays.config.saveConfig();
    }

    public void applyBillboard() {
        FileConfiguration config = EnhancedTextDisplays.config.get();
        String value = config.getString("textdisplays." + name + ".settings.billboard", "fixed");

        textDisplay.setBillboard(Display.Billboard.valueOf(value.toUpperCase()));
    }

    public void setBillboard(String billboardName) throws IllegalArgumentException {
            textDisplay.setBillboard(Display.Billboard.valueOf(billboardName.toUpperCase()));
            section.set("settings.billboard",billboardName.toUpperCase());
    }

    public void applyText(List<String> contents) { //todo зачем папи + хекс? уже все есть в хандлере
        textDisplay.setText("");
        StringBuilder builder = new StringBuilder();
        for (String content : contents) {
            builder.append(Utils.translateHex(PlaceholderAPI.setPlaceholders(null, content))); //todo поменял местами hex и PAPI
            builder.append("\n");
        }
        textDisplay.setText(builder.toString());
        Bukkit.getLogger().info("[ETD] Text of textDisplay: " + name + " has been updated");
    }

    public void movehere(Player player) {
        Location loc = player.getLocation();
        textDisplay.teleport(loc);
        removeFromWorld();
        spawnEntity();
        saveLocation(loc);
        player.sendMessage(ChatColor.YELLOW + "[ETD] TextDisplay was teleported to you");
    }

    void saveLocation(Location loc) {
        location = loc;
        FileConfiguration config = EnhancedTextDisplays.config.get();
        //Bukkit.getLogger().info("saving key: " + name);
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
        contents = Arrays.stream(text.split("\\n")).collect(Collectors.toList());
        textDisplay.setText(Utils.translateHex(textDisplay.getText().isEmpty() ? text + "\n" : textDisplay.getText() + text + "\n"));
        FileConfiguration config = EnhancedTextDisplays.config.get();
        //Bukkit.getLogger().info("saving key: " + name);
        config.set("textdisplays." + name + ".contents", contents);
        EnhancedTextDisplays.config.saveConfig();
    }

    public void setPivotPoint(float x, float y, float z) {
        //try setPivot customKey 0 -0.9 0
        //для скейл 1
        Transformation transform = textDisplay.getTransformation();
        Vector3f vec = transform.getTranslation();
        vec.set(new Vector3f(x, y, z));
        textDisplay.setTransformation(transform);
        section.set("settings.pivotPoint.x", x);
        section.set("settings.pivotPoint.y", y);
        section.set("settings.pivotPoint.z", z);
        EnhancedTextDisplays.config.saveConfig();
    }

    private void setDefaultPivotPoint() {
        Transformation transform = textDisplay.getTransformation();
        Vector3f vec = transform.getTranslation();


        float x = (float) section.getDouble("settings.pivotPoint.x", 0);
        float y = (float) section.getDouble("settings.pivotPoint.y", 1);
        float z = (float) section.getDouble("settings.pivotPoint.z", 0);
        vec.set(new Vector3f(x, y, z));
        textDisplay.setTransformation(transform);
    }

    public void setScale(float size) {
        Transformation transfor = textDisplay.getTransformation();
        Vector3f scale = transfor.getScale();
        scale.set(size);
        textDisplay.setTransformation(transfor);
        section.set("settings.scale", size);
        EnhancedTextDisplays.config.saveConfig();
    }

    public void setDefaultScale() {
        double size = section.getDouble("settings.scale", 1);
        Transformation transfor = textDisplay.getTransformation();
        Vector3f scale = transfor.getScale();
        scale.set(size);
        textDisplay.setTransformation(transfor);
    }


    public void setBackground(boolean trueOrfalse) {
        textDisplay.setDefaultBackground(trueOrfalse);
    }

    public void setLocation(Location newlocation) {
        location = newlocation;
    }

    public void deserealizeAndUpdateContents(String name) {
        contents = DisplayHandler.deserealizeContents(name);
        applyText(contents);
    }

    public void setAlightment(String alignment) {
        try {
            TextDisplay.TextAlignment enumAlignment = TextDisplay.TextAlignment.valueOf(alignment.toUpperCase());
            textDisplay.setAlignment(enumAlignment);
            section.set("settings.alignment", alignment.toUpperCase());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().info("[EnhancedTextDisplays] unable to set alignment: " + alignment + "\n" +
                    "[EnhancedTextDisplays] avaliable only: CENTER LEFT RIGHT");
            e.printStackTrace();
        }
    }

    public void removeFromWorld() {
        Chunk chunk = textDisplay.getLocation().getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
            textDisplay.remove();
            chunk.unload();
            return;
        }
        textDisplay.remove();
    }

    public void attach(Player player) {
        BlockFace face = player.getTargetBlockFace(5);
        if (player.getTargetBlockExact(3) == null) {
            player.sendMessage(" ");
            player.sendMessage(Utils.translateHex("  &c&l| &#D27E7E[EnhancedTextDisplays] Block not found. Stay closer."));
            player.sendMessage(Utils.translateHex("  &c&l| &#D27E7ELimit is 3 blocks"));
            player.sendMessage(" ");
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
                removeFromWorld();
                spawnEntity();
                break;
            case "DOWN":
                newlocation.setZ(newlocation.getY() - 0.05);
                textDisplay.teleport(newlocation);

                setPitch(90);
                newlocation.setPitch(90);
                setLocation(newlocation);
                saveLocation(newlocation);
                removeFromWorld();
                spawnEntity();
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
                removeFromWorld();
                spawnEntity();

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
                removeFromWorld();
                spawnEntity();
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
                removeFromWorld();
                spawnEntity();
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
                removeFromWorld();
                spawnEntity();
                break;
        }
        player.sendMessage(" ");
        player.sendMessage(Utils.translateHex("  &c&l| &r&#5591CB[EnhancedTextDisplays] TextDisplay was attached to a wall"));
        player.sendMessage(" ");
    }

    public void startTimer() {
        if (updateTask != null) {
            updateTask.start();
        }

    }
}
