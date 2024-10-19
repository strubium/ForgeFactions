package com.example.modid;

import net.minecraft.entity.player.EntityPlayer;

import java.util.*;

public class FactionManager {
    private static FactionManager instance;
    private Map<String, Faction> factions;

    private FactionManager() {
        factions = new HashMap<>();
    }

    public static FactionManager getInstance() {
        if (instance == null) {
            instance = new FactionManager();
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
        return Collections.unmodifiableSet(new HashSet<>(factions.values()));  // Return an unmodifiable copy of the factions set
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
}

