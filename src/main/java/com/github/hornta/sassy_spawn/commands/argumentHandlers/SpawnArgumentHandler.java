package com.github.hornta.sassy_spawn.commands.argumentHandlers;

import com.github.hornta.commando.ValidationResult;
import com.github.hornta.commando.completers.IArgumentHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.sassy_spawn.MessageKey;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import com.github.hornta.sassy_spawn.Spawn;
import org.bukkit.command.CommandSender;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class SpawnArgumentHandler implements IArgumentHandler {
  @Override
  public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
    return SassySpawnPlugin
      .getSpawnManager()
      .getSpawns()
      .stream()
      .filter(spawn -> spawn
        .getName()
        .toLowerCase(Locale.ENGLISH)
        .startsWith(argument.toLowerCase(Locale.ENGLISH))
      )
      .map(Spawn::getName)
      .collect(Collectors.toSet());
  }

  @Override
  public boolean test(Set<String> items, String argument) {
    return items.contains(argument);
  }

  @Override
  public void whenInvalid(ValidationResult validationResult) {
    MessageManager.setValue("spawn", validationResult.getValue());
    MessageManager.sendMessage(validationResult.getCommandSender(), MessageKey.SPAWN_NOT_FOUND);
  }
}
