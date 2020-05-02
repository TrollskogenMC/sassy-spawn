package com.github.hornta.sassy_spawn.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.messenger.Translation;
import com.github.hornta.sassy_spawn.ConfigKey;
import com.github.hornta.sassy_spawn.MessageKey;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import org.bukkit.command.CommandSender;

public class CommandReload implements ICommandHandler {

  @Override
  public void handle(CommandSender commandSender, String[] strings, int i) {
    SassySpawnPlugin.getInstance().getConfiguration().reload();
    Translation translation = SassySpawnPlugin.getInstance().getTranslations().createTranslation(SassySpawnPlugin.getInstance().getConfiguration().get(ConfigKey.LANGUAGE));
    MessageManager.getInstance().setTranslation(translation);
    MessageManager.sendMessage(commandSender, MessageKey.CONFIG_RELOADED_OK);
  }
}
