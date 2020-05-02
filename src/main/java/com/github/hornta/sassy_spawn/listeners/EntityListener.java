package com.github.hornta.sassy_spawn.listeners;

import com.github.hornta.sassy_spawn.ConfigKey;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import com.github.hornta.sassy_spawn.TargetedPlayers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import java.time.LocalDateTime;

public class EntityListener implements Listener {
  private final TargetedPlayers targetedPlayers;

  public EntityListener(TargetedPlayers targetedPlayers) {
    this.targetedPlayers = targetedPlayers;
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    long damagedCooldown = (int)SassySpawnPlugin.getInstance().getConfiguration().get(ConfigKey.DAMAGED_COOLDOWN);
    LocalDateTime damagedExpired = LocalDateTime.now().plusSeconds(damagedCooldown);
    SassySpawnPlugin.getPlayerManager().getPlayerData((Player) event.getEntity()).setDamagedExpired(damagedExpired);
  }

  @EventHandler
  public void onEntityTarget(EntityTargetEvent event) {
    if (!(event.getEntity() instanceof Monster)) {
      return;
    }

    Entity target = event.getTarget();
    if (target == null) {
      targetedPlayers.stopTarget(event.getEntity());
      return;
    }

    if (target.getType() != EntityType.PLAYER) {
      return;
    }

    targetedPlayers.startTarget(event.getEntity(), (Player) target);
  }

  @EventHandler
  public void onEntityDeath(EntityDeathEvent event) {
    if (!(event.getEntity() instanceof Monster)) {
      return;
    }

    targetedPlayers.stopTarget(event.getEntity());
  }
}
