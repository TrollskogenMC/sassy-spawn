package com.github.hornta.sassy_spawn.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.sassy_spawn.MessageKey;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetSpawn implements ICommandHandler {
  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    Player player = (Player) commandSender;

    SassySpawnPlugin.getSpawnManager().setSpawn(args[0], player.getLocation());
    MessageManager.setValue("spawn", args[0]);
    MessageManager.setValue("world", player.getWorld().getName());
    MessageManager.sendMessage(commandSender, MessageKey.SPAWN_SET_OK);
  }
}
