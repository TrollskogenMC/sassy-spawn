package com.github.hornta.sassy_spawn;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
  private final Map<UUID, PlayerData> playerData;

  PlayerManager() {
    playerData = new HashMap<>();
  }

  public PlayerData getPlayerData(Player player) {
    playerData.putIfAbsent(player.getUniqueId(), new PlayerData(player));
    return playerData.get(player.getUniqueId());
  }
}
