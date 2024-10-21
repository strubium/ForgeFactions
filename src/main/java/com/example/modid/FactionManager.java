package com.example.modid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.*;

public class FactionManager {
    private static FactionManager instance;
    private static Map<String, Faction> factions;  // Now synchronized with FactionSavedData
    private FactionSavedData savedData;

    private FactionManager(World world) {
        // Load factions from saved data
        this.savedData = new FactionSavedData(world);
        this.factions = savedData.getFactions();  // Load factions from FactionSavedData
    }

    public static FactionManager getInstance(World world) {
        if (instance == null) {
            instance = new FactionManager(world);
        }
        return instance;
    }

    // Create a new faction and save the updated factions map
    public Faction createFaction(String name, EntityPlayer leader) {
        Faction faction = new Faction(name, leader);
        factions.put(name, faction);
        savedData.setFactions(factions);  // Save the updated factions map
        return faction;
    }

    // Get an existing faction by name
    public static Faction getFaction(String name) {
        return factions.get(name);
    }

    public void saveFactions() {
        savedData.setFactions(factions);
    }

    // Disband a faction and save the updated factions map
    public void disbandFaction(String name) {
        factions.remove(name);
        savedData.setFactions(factions);  // Save the updated factions map
    }

    // Return all factions as an unmodifiable set
    public Set<Faction> getFactions() {
        return Collections.unmodifiableSet(new HashSet<>(factions.values()));
    }

    // Declare war between two factions and save the updated factions map
    public void declareWar(Faction faction1, Faction faction2) {
        faction1.declareWar(faction2);
        faction2.declareWar(faction1);  // Mutual war declaration
        savedData.setFactions(factions);  // Save the updated factions map
    }

    // End war between two factions and save the updated factions map
    public void endWar(Faction faction1, Faction faction2) {
        faction1.endWar(faction2);
        faction2.endWar(faction1);  // End war for both factions
        savedData.setFactions(factions);  // Save the updated factions map
    }

    // Check if two factions are at war
    public boolean areAtWar(Faction faction1, Faction faction2) {
        return faction1.isAtWarWith(faction2);
    }

    // Check if a faction with the given name exists
    public boolean factionExists(String factionName) {
        return factions.containsKey(factionName);
    }
}
