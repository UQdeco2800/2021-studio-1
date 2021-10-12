package com.deco2800.game.entities.configs;

/**
 * Defines all NPC configs to be loaded by the NPC Factory.
 */
public class NPCConfigs {
  public BaseEntityConfig ghost = new BaseEntityConfig();
  public GhostKingConfig ghostKing = new GhostKingConfig();
  //creates a config class for the wall of death.
  public BaseEntityConfig wallOfDeath = new BaseEntityConfig();
  public BaseEntityConfig deathGiant = new BaseEntityConfig();
  public BaseEntityConfig bifrost = new BaseEntityConfig();

  public BaseEntityConfig skeleton = new BaseEntityConfig();
  public BaseEntityConfig wolf = new BaseEntityConfig();
  public BaseEntityConfig fireSpirit = new BaseEntityConfig();
  public BaseEntityConfig rock = new BaseEntityConfig();
  public BaseEntityConfig spikes = new BaseEntityConfig();
}
