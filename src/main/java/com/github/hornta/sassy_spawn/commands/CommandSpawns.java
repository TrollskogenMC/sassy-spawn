package com.github.hornta.sassy_spawn.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.sassy_spawn.MessageKey;
import com.github.hornta.sassy_spawn.SassySpawnPlugin;
import com.github.hornta.sassy_spawn.Spawn;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.stream.Collectors;

public class CommandSpawns implements ICommandHandler {
  @Override
  public void handle(CommandSender commandSender, String[] strings, int i) {
    String spawns = "";
    spawns += String.join(
      MessageManager.getMessage(MessageKey.SPAWN_LIST_JOIN),
      SassySpawnPlugin.getSpawnManager().getSpawnLocations().entrySet().stream().map((Map.Entry<String, Spawn> spawnEntry) -> {
        Spawn spawn = spawnEntry.getValue();
        MessageManager.setValue("spawn", spawnEntry.getKey());
        MessageKey spawnListEach = MessageKey.SPAWN_LIST_EACH;

        if (SassySpawnPlugin.getEconomy() != null && spawn.getCost() > 0) {
          spawnListEach = MessageKey.SPAWN_LIST_EACH_COST;
          MessageManager.setValue("cost", SassySpawnPlugin.getEconomy().format(spawn.getCost()));

          if (!spawn.isEnabled()) {
            spawnListEach = MessageKey.SPAWN_LIST_EACH_COST_DISABLED;
          }
        } else if (!spawn.isEnabled()) {
          spawnListEach = MessageKey.SPAWN_LIST_EACH_DISABLED;
        }
        return MessageManager.getMessage(spawnListEach);
      }).collect(Collectors.toSet())
    );

    MessageManager.setValue("count", SassySpawnPlugin.getSpawnManager().getSpawnLocations().size());
    MessageManager.setValue("spawns", spawns);
    MessageManager.sendMessage(commandSender, MessageKey.SPAWN_LIST);
  }
}
