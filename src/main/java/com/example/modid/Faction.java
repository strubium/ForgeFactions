package com.example.modid;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;

public class Faction {
    private String name;
    private EntityPlayer leader;
    private Set<EntityPlayer> members;
    private Set<ChunkPos> claimedChunks;
    private Set<Faction> enemies;  // Track factions at war

    public Faction(String name, EntityPlayer leader) {
        this.name = name;
        this.leader = leader;
        this.members = new HashSet<>();
        this.claimedChunks = new HashSet<>();
        this.enemies = new HashSet<>();  // Initialize enemies list
        members.add(leader);
    }

    public Faction() {
        this.name = null;
        this.leader = null;
        this.members = new HashSet<>();
        this.claimedChunks = new HashSet<>();
        this.enemies = new HashSet<>();  // Initialize enemies list
        members.add(leader);
    }

    public String getName() {
        return name;
    }

    public EntityPlayer getLeader() {
        return leader;
    }

    public Set<EntityPlayer> getMembers() {
        return members;
    }
    public void setMembers(Set<EntityPlayer> members) {
        this.members.clear();
        this.members.addAll(members); // Set new members
    }

    public Set<Faction> getEnemies() {
        return enemies;
    }

    public void setEnemies(Set<Faction> enemies) {
        this.enemies.clear();
        this.enemies.addAll(enemies); // Set new enemies
    }

    public void addMember(EntityPlayer player) {
        members.add(player);
    }

    public void removeMember(EntityPlayer player) {
        members.remove(player);
    }

    public Set<ChunkPos> getClaimedChunks() {
        return claimedChunks;
    }

    public void setClaimedChunks(Set<ChunkPos> claimedChunks) {
        this.claimedChunks.clear();
        this.claimedChunks.addAll(claimedChunks); // Set new claimed chunks
    }

    public void claimChunk(ChunkPos chunkPos) {
        claimedChunks.add(chunkPos);
        FactionManager.getInstance(this.leader.getEntityWorld()).saveFactions();
    }

    public void unclaimChunk(ChunkPos chunkPos) {
        claimedChunks.remove(chunkPos);
    }

    public void declareWar(Faction faction) {
        enemies.add(faction);
    }

    public void endWar(Faction faction) {
        enemies.remove(faction);
    }

    public boolean isAtWarWith(Faction faction) {
        return enemies.contains(faction);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLeader(EntityPlayer leader) {
        this.leader = leader;
    }
}
