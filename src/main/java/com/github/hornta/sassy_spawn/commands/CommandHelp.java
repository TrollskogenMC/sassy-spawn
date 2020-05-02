package com.github.hornta.sassy_spawn.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.sassy_spawn.MessageKey;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandHelp implements ICommandHandler {
  @Override
  public void handle(CommandSender commandSender, String[] strings, int i) {
    Player player = null;
    if(commandSender instanceof Player) {
      player = (Player)commandSender;
    }

    List<String> helpTexts = SassySpawnPlugin.getCommando().getHelpTexts(player);
    MessageManager.sendMessage(commandSender, MessageKey.HELP_TITLE);
    for (String helpText : helpTexts) {
      commandSender.sendMessage(helpText);
    }
  }
}
