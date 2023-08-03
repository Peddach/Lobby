package de.petropia.lobby;

import de.petropia.lobby.commands.ArenaCommand;
import de.petropia.lobby.listener.PlayerJoinListener;
import de.petropia.lobby.listener.ServerShutdownListener;
import de.petropia.lobby.listener.SpawnProtectionListener;
import de.petropia.lobby.commands.BuildCommand;
import de.petropia.lobby.minigames.ArenaUpdateListener;
import de.petropia.lobby.portal.MinigamePortal;
import de.petropia.turtleServer.api.PetropiaPlugin;
import de.petropia.turtleServer.api.minigame.GameMode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    private void registerCommands() {
        getCommand("build").setExecutor(new BuildCommand());
        getCommand("arena").setExecutor(new ArenaCommand());
        getCommand("build").setTabCompleter(new ArenaCommand());
    }

    private Location loadSpawnFromConfig() {
        double x = this.getConfig().getDouble("Spawn.X");
        double y = this.getConfig().getDouble("Spawn.Y");
        double z = this.getConfig().getDouble("Spawn.Z");
        float pitch = this.getConfig().getLong("Spawn.Pitch");
        float yaw = this.getConfig().getLong("Spawn.Yaw");
        return new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch);
    }

    private void registerListener(){
        PluginManager plManager = getServer().getPluginManager();
        plManager.registerEvents(new SpawnProtectionListener(), this);
        plManager.registerEvents(new PlayerJoinListener(), this);
        plManager.registerEvents(new ArenaUpdateListener(),this);
        plManager.registerEvents(new ServerShutdownListener(), this);
    }

    public static Lobby getInstance() {
        return instance;
    }

    public static Location getSpawn() {
        return spawn;
    }
}
