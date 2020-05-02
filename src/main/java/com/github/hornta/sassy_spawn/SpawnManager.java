package com.github.hornta.sassy_spawn;

import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class SpawnManager {
  private final SassySpawnPlugin plugin;
  private final Config config;
  private final HashMap<String, Spawn> spawnLocations = new HashMap<>();
  public static final String DEFAULT_SPAWN = "default";

  SpawnManager(SassySpawnPlugin plugin) {
    this.plugin = plugin;
    this.config = new Config(plugin, "spawns.yml");
    this.config.saveDefault();
    load();
  }

  void load() {
    ConfigurationSection spawnsSection = config.getConfig().getConfigurationSection("spawns");
    if (spawnsSection == null) {
      return;
    }

    Set<String> spawns = spawnsSection.getKeys(false);
    for (String spawn : spawns) {
      String key = "spawns." + spawn + ".";
      Location spawnLocation = new Location(
        plugin.getServer().getWorld(UUID.fromString(config.getConfig().getString(key + "world"))),
        config.getConfig().getDouble(key + "x"),
        config.getConfig().getDouble(key + "y"),
        config.getConfig().getDouble(key + "z"),
        (float) config.getConfig().getDouble(key + "yaw"),
        (float) config.getConfig().getDouble(key + "pitch")
      );

      Double cost = config.getConfig().getDouble(key + "cost");
      boolean enabled = config.getConfig().getBoolean(key + "enabled");

      spawnLocations.put(spawn, new Spawn(spawnLocation, cost, enabled, spawn));
    }
  }

  private void save() {
    for (Map.Entry<String, Spawn> entry : spawnLocations.entrySet()) {
      String baseKey = "spawns." + entry.getKey() + ".";
      config.getConfig().set(baseKey + "world", entry.getValue().getLocation().getWorld().getUID().toString());
      config.getConfig().set(baseKey + "x", entry.getValue().getLocation().getX());
      config.getConfig().set(baseKey + "y", entry.getValue().getLocation().getY());
      config.getConfig().set(baseKey + "z", entry.getValue().getLocation().getZ());
      config.getConfig().set(baseKey + "yaw", entry.getValue().getLocation().getYaw());
      config.getConfig().set(baseKey + "pitch", entry.getValue().getLocation().getPitch());
      config.getConfig().set(baseKey + "cost", entry.getValue().getCost());
      config.getConfig().set(baseKey + "enabled", entry.getValue().isEnabled());
    }
    config.save();
  }

  public void reload() {
    config.reloadConfig();
    spawnLocations.clear();
    this.load();
  }

  public Spawn getSpawn(String name) {
    return spawnLocations.get(name);
  }

  public List<Spawn> getSpawns() {
    return new ArrayList<>(spawnLocations.values());
  }

  public Boolean exists(String name) {
    return spawnLocations.get(name) != null;
  }

  public void rename(String current, String newName) {
    Spawn currentSpawn = spawnLocations.remove(current);
    currentSpawn.setName(newName);
    spawnLocations.put(newName, currentSpawn);
    save();
  }

  public void deleteSpawn(Spawn spawn) {
    spawnLocations.remove(spawn.getName());
    save();
  }

  public void setSpawn(String name, Location location) {
    spawnLocations.put(name, new Spawn(location, name));
    save();
  }

  public void setSpawnCost(Spawn spawn, String amount) {
    spawn.setCost(Double.parseDouble(amount));
    save();
  }

  public void enable(String spawnName) {
    getSpawn(spawnName).setEnabled(true);
    save();
  }

  public void disable(String spawnName) {
    getSpawn(spawnName).setEnabled(false);
    save();
  }

  public Map<String, Spawn> getSpawnLocations() {
    return spawnLocations;
  }

  private static class Config {
    private final File file;
    private final JavaPlugin plugin;
    private FileConfiguration newConfig;
    private final String relativeFilepath;

    public Config(JavaPlugin plugin, String relativeFilepath) {
      this.plugin = plugin;
      this.relativeFilepath = relativeFilepath;
      file = new File(plugin.getDataFolder(), relativeFilepath);
    }

    public void saveDefault() {
      if (!file.exists()) {
        plugin.saveResource(relativeFilepath, false);
      }
    }

    public FileConfiguration getConfig() {
      if (newConfig == null) {
        reloadConfig();
      }
      return newConfig;
    }

    public void save() {
      try {
        getConfig().save(file);
      } catch (IOException ex) {
        Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + file, ex);
      }
    }

    public void reloadConfig() {
      saveDefault();
      newConfig = YamlConfiguration.loadConfiguration(file);

      final InputStream defConfigStream = plugin.getResource(file.getPath());
      if (defConfigStream == null) {
        return;
      }

      newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }
  }
}
