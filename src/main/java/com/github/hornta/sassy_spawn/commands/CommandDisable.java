package com.github.hornta.sassy_spawn.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.sassy_spawn.MessageKey;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import org.bukkit.command.CommandSender;

public class CommandDisable implements ICommandHandler {
  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    SassySpawnPlugin.getSpawnManager().disable(args[0]);
    MessageManager.setValue("spawn", args[0]);
    MessageManager.sendMessage(commandSender, MessageKey.SPAWN_DISABLED_OK);
  }
}
