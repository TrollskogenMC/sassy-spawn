package com.github.hornta.sassy_spawn.listeners;

import com.github.hornta.sassy_spawn.ConfigKey;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import com.github.hornta.sassy_spawn.Spawn;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PlayerListener implements Listener {
  private SassySpawnPlugin plugin;

  public PlayerListener(SassySpawnPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    if (SassySpawnPlugin.getInstance().getConfiguration().get(ConfigKey.RESPAWN_AT_BED)) {
      Location bedSpawnLocation = event.getPlayer().getBedSpawnLocation();
      if (bedSpawnLocation != null) {
        event.setRespawnLocation(bedSpawnLocation);
        return;
      }
    }

    String onDeathSpawn = SassySpawnPlugin.getInstance().getConfiguration().get(ConfigKey.ON_DEATH_SPAWN);
    if (onDeathSpawn.isEmpty()) {
      return;
    }

    if (!SassySpawnPlugin.getSpawnManager().exists(onDeathSpawn)) {
      Bukkit.getLogger().severe("\"on-death-spawn\" set to an invalid spawn. Please change to correct spawn or to an empty string in config.yml");
      return;
    }

    Spawn spawn = SassySpawnPlugin.getSpawnManager().getSpawn(onDeathSpawn);
    if (spawn.isEnabled()) {
      event.setRespawnLocation(spawn.getLocation());
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (event.getPlayer().hasPlayedBefore()) {
      return;
    }

    String firstJoinSpawn = SassySpawnPlugin.getInstance().getConfiguration().get(ConfigKey.FIRST_JOIN_SPAWN);
    if (firstJoinSpawn.isEmpty()) {
      return;
    }

    if (!SassySpawnPlugin.getSpawnManager().exists(firstJoinSpawn)) {
      Bukkit.getLogger().severe("\"first-join-spawn\" set to an invalid spawn. Please change to correct spawn or to an empty string in config.yml");
      return;
    }

    Spawn spawn = SassySpawnPlugin.getSpawnManager().getSpawn(firstJoinSpawn);
    if (spawn.isEnabled()) {
      PaperLib.teleportAsync(event.getPlayer(), spawn.getLocation(), TeleportCause.PLUGIN);
    }
  }
}
