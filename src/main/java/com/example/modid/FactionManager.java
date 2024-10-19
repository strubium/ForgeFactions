package com.example.modid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.*;

public class FactionManager {
    private static FactionManager instance;
    private Map<String, Faction> factions;
    private FactionSavedData savedData;


    private FactionManager(World world) {
        this.factions = new HashMap<>();
        this.savedData = new FactionSavedData(world);
    }

    public static FactionManager getInstance(World world) {
        if (instance == null) {
            instance = new FactionManager(world);
        }
        return instance;
    }

    public Faction createFaction(String name, EntityPlayer leader) {
        Faction faction = new Faction(name, leader);
        factions.put(name, faction);
        return faction;
    }

    public Faction getFaction(String name) {
        return factions.get(name);
    }

    public void disbandFaction(String name) {
        factions.remove(name);
    }

    public Set<Faction> getFactions() {
        // Get the map of factions
        Map<String, Faction> factionsMap = savedData.getFactions();

        // Return an unmodifiable set of the factions
        return Collections.unmodifiableSet(new HashSet<>(factionsMap.values()));
    }

    public void declareWar(Faction faction1, Faction faction2) {
        faction1.declareWar(faction2);
        faction2.declareWar(faction1);  // Mutual war declaration
    }

    public void endWar(Faction faction1, Faction faction2) {
        faction1.endWar(faction2);
        faction2.endWar(faction1);  // End war for both factions
    }

    public boolean areAtWar(Faction faction1, Faction faction2) {
        return faction1.isAtWarWith(faction2);
    }

    // New method to check if a faction with the given name exists
    public boolean factionExists(String factionName) {
        return factions.containsKey(factionName);  // Check if the faction name exists in the map
    }
}
