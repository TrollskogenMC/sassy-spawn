package com.github.hornta.sassy_spawn.config;

import com.github.hornta.sassy_spawn.ConfigKey;
import com.github.hornta.versioned_config.Configuration;
import com.github.hornta.versioned_config.IConfigVersion;
import com.github.hornta.versioned_config.Patch;
import com.github.hornta.versioned_config.Type;

public class InitialVersion implements IConfigVersion<ConfigKey> {
  @Override
  public int version() {
    return 1;
  }

  @Override
  public Patch<ConfigKey> migrate(Configuration<ConfigKey> configuration) {
    Patch<ConfigKey> patch = new Patch<>();
    patch.set(ConfigKey.LANGUAGE, "language", "english", Type.STRING);
    patch.set(ConfigKey.FIRST_JOIN_SPAWN, "first_join_spawn", "default", Type.STRING);
    patch.set(ConfigKey.ON_DEATH_SPAWN, "on_death_spawn", "default", Type.STRING);
    patch.set(ConfigKey.RESPAWN_AT_BED, "respawn_at_bed", true, Type.BOOLEAN);
    patch.set(ConfigKey.ALLOW_SPAWN_WHEN_FALLING, "allow_spawn_when_falling", true, Type.BOOLEAN);
    patch.set(ConfigKey.ALLOW_SPAWN_WHEN_TARGETED, "allow_spawn_when_targeted", true, Type.BOOLEAN);
    patch.set(ConfigKey.TARGETED_COOLDOWN, "targeted_cooldown", 5, Type.INTEGER);
    patch.set(ConfigKey.ALLOW_SPAWN_WHEN_DAMAGED, "allow_spawn_when_damaged", true, Type.BOOLEAN);
    patch.set(ConfigKey.DAMAGED_COOLDOWN, "damaged_cooldown", 5, Type.INTEGER);
    return patch;
  }
}