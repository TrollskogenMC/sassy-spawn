package com.github.hornta.sassy_spawn.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.sassy_spawn.MessageKey;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import com.github.hornta.sassy_spawn.Spawn;
import org.bukkit.command.CommandSender;

public class CommandSetCost implements ICommandHandler {
  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    String name = args[0];

    Spawn spawn = SassySpawnPlugin.getSpawnManager().getSpawn(name);

    SassySpawnPlugin.getSpawnManager().setSpawnCost(spawn, args[1]);
    MessageManager.setValue("spawn", spawn.getName());
    MessageManager.setValue("world", spawn.getLocation().getWorld().getName());
    MessageManager.setValue("cost", SassySpawnPlugin.getEconomy().format(spawn.getCost()));
    MessageManager.sendMessage(commandSender, MessageKey.SETSPAWNCOST_OK);
  }
}
