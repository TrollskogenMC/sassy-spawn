package com.github.hornta.sassy_spawn;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TargetedPlayers {
  private final SassySpawnPlugin plugin;
  private final HashMap<Player, ArrayList<Entity>> targets = new HashMap<>();
  private final HashMap<Player, LocalDateTime> targetCooldown = new HashMap<>();

  public TargetedPlayers(SassySpawnPlugin plugin) {
    this.plugin = plugin;
  }

  public void startTarget(Entity entity, Player target) {
    List<Entity> entities = targets.computeIfAbsent(target, player -> new ArrayList<>());
    entities.add(entity);
    targetCooldown.remove(target);
  }

  public void stopTarget(Entity entity) {
    Player player = null;
    for (Map.Entry<Player, ArrayList<Entity>> entry : targets.entrySet()) {
      entry.getValue().remove(entity);
      player = entry.getKey();
    }

    if (player == null) {
      return;
    }

    if (targets.get(player).isEmpty()) {
      targets.remove(player);
      targetCooldown.put(player, LocalDateTime.now().plusSeconds(SassySpawnPlugin.getInstance().getConfiguration().get(ConfigKey.TARGETED_COOLDOWN)));
    }
  }

  public boolean isTargeted(Player player) {
    return targets.containsKey(player);
  }

  public boolean hasCooldownExpired(Player player) {
    if (!targetCooldown.containsKey(player)) {
      return true;
    }

    LocalDateTime expired = targetCooldown.get(player);
    LocalDateTime now = LocalDateTime.now();
    if (expired.isBefore(now) || expired.isEqual(now)) {
      targetCooldown.remove(player);
      return true;
    }

    return false;
  }

  public LocalDateTime getExpired(Player player) {
    return targetCooldown.get(player);
  }
}
