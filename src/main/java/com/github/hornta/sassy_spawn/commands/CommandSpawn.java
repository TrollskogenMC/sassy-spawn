package com.github.hornta.sassy_spawn.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.sassy_spawn.*;
import io.papermc.lib.PaperLib;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.time.Duration;
import java.time.LocalDateTime;

public class CommandSpawn implements ICommandHandler {
  private String formatRemainingTime(LocalDateTime time) {
    Duration duration = Duration.between(LocalDateTime.now(), time);

    String timeRemaining = "";

    long days = duration.toDays();
    long hours = duration.toHours() % 24;
    long minutes = duration.toMinutes() % 60;
    long seconds = duration.getSeconds() % 60;

    if (days > 0) {
      timeRemaining += days + "d ";
    }
    if (hours > 0) {
      timeRemaining += hours + "h ";
    }
    if (minutes > 0) {
      timeRemaining += minutes + "m ";
    }
    if (seconds > 0) {
      timeRemaining += seconds + "s";
    }
    if (timeRemaining.isEmpty()) {
      timeRemaining = "0s";
    }

    return timeRemaining;
  }

  private boolean isInFallingBlocks(Player player) {
    return player.getLocation().getBlock().getType() == Material.LADDER ||
      player.getLocation().getBlock().getType() == Material.VINE ||
      player.getLocation().getBlock().getType() == Material.WATER ||
      player.getLocation().getBlock().getType() == Material.LAVA ||
      player.getLocation().getBlock().getType() == Material.COBWEB;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    Spawn spawn = SassySpawnPlugin.getSpawnManager().getSpawn(args[0]);

    if (spawn.getLocation().getWorld() == null) {
      MessageManager.setValue("spawn", args[0]);
      MessageManager.sendMessage(commandSender, MessageKey.DENY_SPAWN_NO_WORLD);
      return;
    }

    Player player = (Player) commandSender;

    if (
      !(boolean)SassySpawnPlugin.getInstance().getConfiguration().get(ConfigKey.ALLOW_SPAWN_WHEN_FALLING) &&
      !player.isOnGround() &&
      player.getVelocity().getY() < 0 &&
      !this.isInFallingBlocks(player)
    ) {
      MessageManager.sendMessage(commandSender, MessageKey.FALLING_SPAWN_DENIED);
      return;
    }

    PlayerData playerData = SassySpawnPlugin.getPlayerManager().getPlayerData(player);
    if (
      !(boolean)SassySpawnPlugin.getInstance().getConfiguration().get(ConfigKey.ALLOW_SPAWN_WHEN_DAMAGED) &&
      !playerData.isDamagedCooldownExpired()
    ) {
      MessageManager.setValue("time_left", formatRemainingTime(playerData.getDamagedExpired()));
      MessageManager.sendMessage(commandSender, MessageKey.DENY_SPAWN_DAMAGED);
      return;
    }

    if (!(boolean)SassySpawnPlugin.getInstance().getConfiguration().get(ConfigKey.ALLOW_SPAWN_WHEN_TARGETED)) {
      if (SassySpawnPlugin.getTargetedPlayers().isTargeted(player)) {
        MessageManager.sendMessage(commandSender, MessageKey.DENY_SPAWN_TARGETED);
        return;
      }

      if (!SassySpawnPlugin.getTargetedPlayers().hasCooldownExpired(player)) {
        MessageManager.setValue("time_left", formatRemainingTime(SassySpawnPlugin.getTargetedPlayers().getExpired(player)));
        MessageManager.sendMessage(commandSender, MessageKey.DENY_SPAWN_TARGETED_COOLDOWN);
        return;
      }
    }

    if (!spawn.isEnabled()) {
      MessageManager.setValue("spawn", args[0]);
      MessageManager.sendMessage(commandSender, MessageKey.TRY_SPAWN_DISABLED);
      return;
    }

    if (SassySpawnPlugin.getEconomy() != null && spawn.getCost() > 0) {
      if (!SassySpawnPlugin.getEconomy().has(player, spawn.getCost())) {
        MessageManager.setValue("cost", SassySpawnPlugin.getEconomy().format(spawn.getCost()));
        MessageManager.setValue("spawn", spawn.getName());
        MessageManager.sendMessage(commandSender, MessageKey.NOT_AFFORD_SPAWN);
        return;
      }

      SassySpawnPlugin.getEconomy().withdrawPlayer(player, spawn.getCost());
      MessageManager.setValue("cost", SassySpawnPlugin.getEconomy().format(spawn.getCost()));
      MessageManager.sendMessage(commandSender, MessageKey.SPAWN_COST_OK);
    }

    PaperLib.teleportAsync(player, spawn.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
  }
}
