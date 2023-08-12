package de.petropia.lobby;

import de.petropia.lobby.commands.ArenaCommand;
import de.petropia.lobby.holograms.ServerGroupHologram;
import de.petropia.lobby.listener.PlayerJoinListener;
import de.petropia.lobby.listener.ServerShutdownListener;
import de.petropia.lobby.listener.SpawnProtectionListener;
import de.petropia.lobby.commands.BuildCommand;
import de.petropia.lobby.minigames.ArenaUpdateListener;
import de.petropia.lobby.navigation.HotbarListener;
import de.petropia.lobby.portal.MinigamePortal;
import de.petropia.lobby.scoreboard.ScoreboardManager;
import de.petropia.turtleServer.api.PetropiaPlugin;
import de.petropia.turtleServer.api.minigame.GameMode;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class Lobby extends PetropiaPlugin {

    private static Lobby instance;
    private static Location spawn;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        saveConfig();
        reloadConfig();
        registerListener();
        registerCommands();
        spawn = loadSpawnFromConfig();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            getLogger().info("Requesting Arenas...");
            getCloudNetAdapter().publishArenaUpdateResendRequest();
        }, 20*5);
        createHolograms();
        createPortals();
    }

    private void createPortals(){
        ConfigurationSection section = getConfig().getConfigurationSection("MinigamePortals");
        if(section == null){
            getLogger().warning("No MinigamePortals in config!");
            return;
        }
        FileConfiguration config = getConfig();
        List<String> childs = new ArrayList<>(section.getKeys(false));
        for(String child : childs){
            String basePath = "MinigamePortals."+child + ".";
            int x1 = config.getInt(basePath + "X1");
            int y1 = config.getInt(basePath + "Y1");
            int z1 = config.getInt(basePath + "Z1");
            int x2 = config.getInt(basePath + "X2");
            int y2 = config.getInt(basePath + "Y2");
            int z2 = config.getInt(basePath + "Z2");
            String game = config.getString(basePath + "Game");
            GameMode mode = GameMode.valueOf(config.getString(basePath + "Mode"));
            BoundingBox boundingBox = new BoundingBox(x1, y1, z1, x2, y2, z2);
            new MinigamePortal(boundingBox, game, mode);
            getLogger().info("Loaded Portal "+ game + " " + mode);
        }
    }

    private void createHolograms(){
        getLogger().info("Creating Hologramms...");
        for(String section : getConfig().getConfigurationSection("Holograms").getKeys(false)){
            section = "Holograms." + section;
            getLogger().info("Loading Hologram: " + section);
            double x = getConfig().getDouble(section + ".X");
            double y = getConfig().getDouble(section + ".Y");
            double z = getConfig().getDouble(section + ".Z");
            String text = getConfig().getString(section + ".Text");
            String group = getConfig().getString(section + ".Group");
            Material material = Material.valueOf(getConfig().getString(section + ".Item"));
            double offset = getConfig().getDouble(section + ".Offset");
            new ServerGroupHologram(group, text, new Location(Bukkit.getWorld("world"), x, y, z), material, offset);
        }
    }

    private void registerCommands() {
        getCommand("build").setExecutor(new BuildCommand());
        getCommand("arena").setExecutor(new ArenaCommand());
        getCommand("build").setTabCompleter(new ArenaCommand());
    }

    private Location loadSpawnFromConfig() {
        return readLocationFromConfig("Spawn");
    }

    public Location readLocationFromConfig(String basePath){
        double x = this.getConfig().getDouble(basePath + ".X");
        double y = this.getConfig().getDouble(basePath + ".Y");
        double z = this.getConfig().getDouble(basePath + ".Z");
        float pitch = this.getConfig().getLong(basePath + ".Pitch");
        float yaw = this.getConfig().getLong(basePath + ".Yaw");
        return new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch);
    }

    private void registerListener(){
        PluginManager plManager = getServer().getPluginManager();
        plManager.registerEvents(new SpawnProtectionListener(), this);
        plManager.registerEvents(new PlayerJoinListener(), this);
        plManager.registerEvents(new ArenaUpdateListener(),this);
        plManager.registerEvents(new HotbarListener(), this);
        plManager.registerEvents(new ScoreboardManager(), this);
        EventManager manager = InjectionLayer.ext().instance(EventManager.class);
        manager.registerListener(new ServerShutdownListener());
        manager.registerListener(new PlayerJoinListener());
        manager.registerListener(new HotbarListener());
    }

    public static Lobby getInstance() {
        return instance;
    }

    public static Location getSpawn() {
        return spawn;
    }
}
