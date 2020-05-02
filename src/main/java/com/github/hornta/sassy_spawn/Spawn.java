package com.github.hornta.sassy_spawn;

import org.bukkit.Location;

public class Spawn {
  private final Location location;
  private Double cost = 0.0;
  private boolean enabled = true;
  private String name;

  Spawn(Location location, Double cost, boolean enabled, String name) {
    this.location = location;
    this.cost = cost;
    this.enabled = enabled;
    this.name = name;
  }

  Spawn(Location location, String name) {
    this.location = location;
    this.name = name;
  }

  public Location getLocation() {
    return location;
  }

  public Double getCost() {
    return cost;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getName() {
    return name;
  }

  void setCost(Double cost) {
    this.cost = cost;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setName(String name) {
    this.name = name;
  }
}
