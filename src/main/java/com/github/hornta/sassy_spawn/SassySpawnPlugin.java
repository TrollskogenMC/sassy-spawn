package com.github.hornta.sassy_spawn;

import com.github.hornta.commando.*;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.messenger.MessagesBuilder;
import com.github.hornta.messenger.Translation;
import com.github.hornta.messenger.Translations;
import com.github.hornta.sassy_spawn.commands.CommandDeleteSpawn;
import com.github.hornta.sassy_spawn.commands.CommandDisable;
import com.github.hornta.sassy_spawn.commands.CommandEnable;
import com.github.hornta.sassy_spawn.commands.CommandHelp;
import com.github.hornta.sassy_spawn.commands.CommandReload;
import com.github.hornta.sassy_spawn.commands.CommandRename;
import com.github.hornta.sassy_spawn.commands.CommandSetCost;
import com.github.hornta.sassy_spawn.commands.CommandSetSpawn;
import com.github.hornta.sassy_spawn.commands.CommandSpawn;
import com.github.hornta.sassy_spawn.commands.CommandSpawns;
import com.github.hornta.sassy_spawn.commands.argumentHandlers.SpawnArgumentHandler;
import com.github.hornta.sassy_spawn.config.InitialVersion;
import com.github.hornta.sassy_spawn.listeners.EntityListener;
import com.github.hornta.sassy_spawn.listeners.PlayerListener;
import com.github.hornta.versioned_config.Configuration;
import com.github.hornta.versioned_config.ConfigurationBuilder;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public final class SassySpawnPlugin extends JavaPlugin {
  private static SassySpawnPlugin instance;
  private static SpawnManager spawnManager;
  private static Commando commando;
  private static Economy economy;
  private static PlayerManager playerManager;
  private static TargetedPlayers targetedPlayers;
  private Configuration<ConfigKey> configuration;
  private Translations translations;

  @Override
  public void onEnable() {
    new Metrics(this, 4044);
    instance = this;
    spawnManager = new SpawnManager(this);
    playerManager = new PlayerManager();
    targetedPlayers = new TargetedPlayers();
    setupConfig();
    setupMessages();
    setupVaultIntegration();
    setupEventListeners();
    setupCommands();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    return commando.handleCommand(sender, command, args);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    return commando.handleAutoComplete(sender, command, args);
  }

  private void setupConfig() {
    File cfgFile = new File(getDataFolder(), "config.yml");
    ConfigurationBuilder<ConfigKey> cb = new ConfigurationBuilder<>(this, cfgFile);
    cb.addVersion(new InitialVersion());
    configuration = cb.run();
  }

  private void setupMessages() {
    MessageManager messageManager = new MessagesBuilder()
      .add(MessageKey.COMMAND_NOT_FOUND, "command_not_found")
      .add(MessageKey.MISSING_ARGUMENTS_COMMAND, "missing_arguments_command")
      .add(MessageKey.NO_PERMISSION_COMMAND, "no_permission_command")
      .add(MessageKey.SPAWN_NOT_FOUND, "spawn_not_found")
      .add(MessageKey.ARGUMENT_NOT_NUMERIC, "argument_not_numeric")
      .add(MessageKey.TRY_SPAWN_DISABLED, "try_spawn_disabled")
      .add(MessageKey.NOT_AFFORD_SPAWN, "not_afford_spawn")
      .add(MessageKey.FALLING_SPAWN_DENIED, "falling_spawn_denied")
      .add(MessageKey.DENY_SPAWN_TARGETED, "deny_spawn_targeted")
      .add(MessageKey.DENY_SPAWN_TARGETED_COOLDOWN, "deny_spawn_targeted_cooldown")
      .add(MessageKey.DENY_SPAWN_NO_WORLD, "deny_spawn_no_world")
      .add(MessageKey.DENY_SPAWN_DAMAGED, "deny_spawn_damaged")
      .add(MessageKey.RENAME_SPAWN_EXIST, "rename_spawn_exist")
      .add(MessageKey.SPAWN_LIST, "spawn_list")
      .add(MessageKey.SPAWN_LIST_EACH, "spawn_list_each")
      .add(MessageKey.SPAWN_LIST_EACH_COST, "spawn_list_each_cost")
      .add(MessageKey.SPAWN_LIST_EACH_COST_DISABLED, "spawn_list_each_cost_disabled")
      .add(MessageKey.SPAWN_LIST_EACH_DISABLED, "spawn_list_each_disabled")
      .add(MessageKey.SPAWN_LIST_JOIN, "spawn_list_join")
      .add(MessageKey.SETSPAWNCOST_OK, "setspawncost_ok")
      .add(MessageKey.SPAWN_ENABLED_OK, "spawn_enabled_ok")
      .add(MessageKey.SPAWN_DISABLED_OK, "spawn_disabled_ok")
      .add(MessageKey.CONFIG_RELOADED_OK, "config_reloaded_ok")
      .add(MessageKey.SPAWN_SET_OK, "spawn_set_ok")
      .add(MessageKey.SPAWN_DELETED_OK, "spawn_deleted_ok")
      .add(MessageKey.SPAWN_COST_OK, "spawn_cost_ok")
      .add(MessageKey.RENAME_SPAWN_OK, "rename_spawn_ok")
      .add(MessageKey.HELP_TITLE, "help_title")
      .build();

    translations = new Translations(this, messageManager);
    Translation translation = translations.createTranslation(configuration.get(ConfigKey.LANGUAGE));
    messageManager.setTranslation(translation);
  }

  public Configuration<ConfigKey> getConfiguration() {
    return configuration;
  }

  public Translations getTranslations() {
    return translations;
  }

  public static SpawnManager getSpawnManager() {
    return spawnManager;
  }

  public static PlayerManager getPlayerManager() {
    return playerManager;
  }

  public static Economy getEconomy() {
    return economy;
  }

  public static Commando getCommando() {
    return commando;
  }

  public static JavaPlugin getPlugin() {
    return instance;
  }

  public static TargetedPlayers getTargetedPlayers() {
    return targetedPlayers;
  }

  private void setupVaultIntegration() {
    if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
      RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
      if (rsp != null) {
        economy = rsp.getProvider();
      }
    }
  }

  private void setupEventListeners() {
    getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    getServer().getPluginManager().registerEvents(new EntityListener(targetedPlayers), this);
  }

  private void setupCommands() {
    commando = new Commando();
    commando.setNoPermissionHandler((CommandSender commandSender, CarbonCommand command) -> MessageManager.sendMessage(commandSender, MessageKey.NO_PERMISSION_COMMAND));

    commando.setMissingArgumentHandler((CommandSender commandSender, CarbonCommand command) -> {
      MessageManager.setValue("usage", command.getHelpText());
      MessageManager.sendMessage(commandSender, MessageKey.MISSING_ARGUMENTS_COMMAND);
    });

    commando.setMissingCommandHandler((CommandSender sender, List<CarbonCommand> suggestions) -> {
      MessageManager.setValue("suggestions", suggestions.stream()
        .map(CarbonCommand::getHelpText)
        .collect(Collectors.joining("\n")));
      MessageManager.sendMessage(sender, MessageKey.COMMAND_NOT_FOUND);
    });

    SpawnArgumentHandler handler = new SpawnArgumentHandler();
    ICarbonArgument defaultSpawnArgument = new CarbonArgument.Builder("spawn")
      .setHandler(handler)
      .setDefaultValue(CommandSender.class, SpawnManager.DEFAULT_SPAWN)
      .create();

    ICarbonArgument spawnArgument = new CarbonArgument.Builder("spawn")
      .setHandler(handler)
      .create();

    commando
      .addCommand("spawn")
      .withHandler(new CommandSpawn())
      .withArgument(defaultSpawnArgument)
      .preventConsoleCommandSender()
      .requiresPermission("sassyspawn.spawn");

    commando
      .addCommand("setspawn")
      .withHandler(new CommandSetSpawn())
      .withArgument(new CarbonArgument.Builder("name").setDefaultValue(CommandSender.class, SpawnManager.DEFAULT_SPAWN).create())
      .preventConsoleCommandSender()
      .requiresPermission("sassyspawn.setspawn");

    commando
      .addCommand("delspawn")
      .withHandler(new CommandDeleteSpawn())
      .withArgument(defaultSpawnArgument)
      .requiresPermission("sassyspawn.delspawn");

    commando
      .addCommand("spawns")
      .withHandler(new CommandSpawns())
      .requiresPermission("sassyspawn.spawns");

    // TODO: validate cost within bounds
    if(economy != null) {
      commando
        .addCommand("sassyspawn setspawncost")
        .withHandler(new CommandSetCost())
        .withArgument(spawnArgument)
        .withArgument(new CarbonArgument.Builder("cost").setType(CarbonArgumentType.NUMBER).create())
        .requiresPermission("sassyspawn.setspawncost");
    }

    commando
      .addCommand("sassyspawn reload")
      .withHandler(new CommandReload())
      .requiresPermission("sassyspawn.reload");

    commando
      .addCommand("sassyspawn enable")
      .withHandler(new CommandEnable())
      .withArgument(defaultSpawnArgument)
      .requiresPermission("sassyspawn.enable");

    commando
      .addCommand("sassyspawn disable")
      .withHandler(new CommandDisable())
      .withArgument(defaultSpawnArgument)
      .requiresPermission("sassyspawn.disable");

    commando
      .addCommand("sassyspawn help")
      .withHandler(new CommandHelp())
      .requiresPermission("sassyspawn.help");

    commando
      .addCommand("sassyspawn rename")
      .withHandler(new CommandRename())
      .withArgument(spawnArgument)
      .withArgument(new CarbonArgument.Builder("name").create())
      .requiresPermission("sassyspawn.rename");
  }

  public static SassySpawnPlugin getInstance() {
    return instance;
  }
}
