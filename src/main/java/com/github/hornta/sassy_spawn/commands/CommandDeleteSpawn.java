package com.github.hornta.sassy_spawn.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.sassy_spawn.MessageKey;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import com.github.hornta.sassy_spawn.Spawn;
import org.bukkit.command.CommandSender;

public class CommandDeleteSpawn implements ICommandHandler {

  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    Spawn spawn = SassySpawnPlugin.getSpawnManager().getSpawn(args[0]);
    SassySpawnPlugin.getSpawnManager().deleteSpawn(spawn);
    MessageManager.setValue("spawn", spawn.getName());
    MessageManager.setValue("world", spawn.getLocation().getWorld().getName());
    MessageManager.sendMessage(commandSender, MessageKey.SPAWN_DELETED_OK);
  }
}
