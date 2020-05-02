package com.github.hornta.sassy_spawn;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class PlayerData {
  private final Player player;
  private LocalDateTime damagedExpired;

  public PlayerData(Player player) {
    this.player = player;
  }

  public Player getPlayer() {
    return player;
  }

  public void setDamagedExpired(LocalDateTime damagedExpired) {
    this.damagedExpired = damagedExpired;
  }

  public LocalDateTime getDamagedExpired() {
    return damagedExpired;
  }

  public boolean isDamagedCooldownExpired() {
    if (damagedExpired == null) {
      return true;
    }

    LocalDateTime now = LocalDateTime.now();
    return damagedExpired.isBefore(now) || damagedExpired.isEqual(now);
  }
}
