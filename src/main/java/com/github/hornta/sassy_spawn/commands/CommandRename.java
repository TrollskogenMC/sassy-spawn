package com.github.hornta.sassy_spawn.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.sassy_spawn.MessageKey;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import org.bukkit.command.CommandSender;

public class CommandRename implements ICommandHandler {
  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    if (SassySpawnPlugin.getSpawnManager().exists(args[1])) {
      MessageManager.setValue("spawn", args[0]);
      MessageManager.setValue("new_name", args[1]);
      MessageManager.sendMessage(commandSender, MessageKey.RENAME_SPAWN_EXIST);
      return;
    }

    SassySpawnPlugin.getSpawnManager().rename(args[0], args[1]);

    MessageManager.setValue("spawn", args[0]);
    MessageManager.setValue("new_name", args[1]);
    MessageManager.sendMessage(commandSender, MessageKey.RENAME_SPAWN_OK);
  }
}
