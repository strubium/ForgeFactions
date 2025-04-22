package com.strubium.forgefactions.faction;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.HashSet;
import java.util.Set;

public class Faction {
    private String name;
    private GameProfile leader;  // Changed to GameProfile
    private Set<GameProfile> members;  // Changed to Set of GameProfiles
    private Set<ChunkPos> claimedChunks;
    private Set<Faction> enemies;  // Track factions at war

    public Faction(String name, GameProfile leader) {
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
    }

    public String getName() {
        return name;
    }

    public GameProfile getLeader() {
        return leader;
    }

    public Set<GameProfile> getMembers() {
        return members;
    }

    public void setMembers(Set<GameProfile> members) {
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

    public void addMember(GameProfile player) {
        members.add(player);
    }

    public void removeMember(GameProfile player) {
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

        // Resolve the leader EntityPlayer using the GameProfile
        EntityPlayer player = resolvePlayerByGameProfile(leader);
        if (player != null) {
            // Use the resolved EntityPlayer to get the world
            FactionManager.getInstance(player.getEntityWorld()).saveFactions();
        }
    }

    private EntityPlayer resolvePlayerByGameProfile(GameProfile gameProfile) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) {
            return server.getPlayerList().getPlayerByUUID(gameProfile.getId());  // Resolve player from UUID
        }
        return null;  // Return null if player is not online or not found
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

    public void setLeader(GameProfile leader) {
        this.leader = leader;
    }
}
