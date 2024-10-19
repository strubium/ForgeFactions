package com.example.modid;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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

    public String getName() {
        return name;
    }

    public EntityPlayer getLeader() {
        return leader;
    }

    public Set<EntityPlayer> getMembers() {
        return members;
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

    public void claimChunk(ChunkPos chunkPos) {
        claimedChunks.add(chunkPos);
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

    /**
     * Write Faction data to NBT for saving.
     */
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("Name", this.name);

        // Save leader
        nbt.setString("Leader", leader.getUniqueID().toString());

        // Save members
        NBTTagList memberList = new NBTTagList();
        for (EntityPlayer member : members) {
            NBTTagCompound memberTag = new NBTTagCompound();
            memberTag.setString("Member", member.getUniqueID().toString());
            memberList.appendTag(memberTag);
        }
        nbt.setTag("Members", memberList);

        // Save claimed chunks
        NBTTagList chunkList = new NBTTagList();
        for (ChunkPos chunk : claimedChunks) {
            NBTTagCompound chunkTag = new NBTTagCompound();
            chunkTag.setInteger("ChunkX", chunk.x);
            chunkTag.setInteger("ChunkZ", chunk.z);
            chunkList.appendTag(chunkTag);
        }
        nbt.setTag("ClaimedChunks", chunkList);

        // Save enemies
        NBTTagList enemyList = new NBTTagList();
        for (Faction enemy : enemies) {
            NBTTagCompound enemyTag = new NBTTagCompound();
            enemyTag.setString("Enemy", enemy.getName());
            enemyList.appendTag(enemyTag);
        }
        nbt.setTag("Enemies", enemyList);
    }

    /**
     * Read Faction data from NBT for loading.
     */
    public void readFromNBT(NBTTagCompound nbt) {
        // Load faction name
        this.name = nbt.getString("Name");

        // Leader loading needs to be handled depending on how you're managing players
        // For example, you could later find the player by UUID
        String leaderUUID = nbt.getString("Leader");
        // Load members (you need a way to convert these back to EntityPlayer)
        NBTTagList memberList = nbt.getTagList("Members", 10); // 10 for NBTTagCompound
        for (int i = 0; i < memberList.tagCount(); i++) {
            NBTTagCompound memberTag = memberList.getCompoundTagAt(i);
            String memberUUID = memberTag.getString("Member");
            // You would need to resolve the player by UUID here
        }

        // Load claimed chunks
        NBTTagList chunkList = nbt.getTagList("ClaimedChunks", 10); // 10 for NBTTagCompound
        for (int i = 0; i < chunkList.tagCount(); i++) {
            NBTTagCompound chunkTag = chunkList.getCompoundTagAt(i);
            int chunkX = chunkTag.getInteger("ChunkX");
            int chunkZ = chunkTag.getInteger("ChunkZ");
            this.claimedChunks.add(new ChunkPos(chunkX, chunkZ));
        }

        // Load enemies (you may need to resolve these Faction objects from the manager)
        NBTTagList enemyList = nbt.getTagList("Enemies", 10);
        for (int i = 0; i < enemyList.tagCount(); i++) {
            NBTTagCompound enemyTag = enemyList.getCompoundTagAt(i);
            String enemyName = enemyTag.getString("Enemy");
            // You can look up the enemy faction from the FactionManager by name later
        }
    }
}
