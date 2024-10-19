package com.example.modid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FactionChunkHandler {

    // Optimized data structure to quickly look up which faction owns a chunk
    private final Map<ChunkPos, Faction> chunkOwnershipMap = new HashMap<>();

    // Register claimed chunks into the map when the server starts or the mod initializes
    public void registerClaimedChunks() {
        for (Faction faction : FactionManager.getInstance().getFactions()) {
            Set<ChunkPos> claimedChunks = faction.getClaimedChunks();
            for (ChunkPos chunkPos : claimedChunks) {
                chunkOwnershipMap.put(chunkPos, faction);
            }
        }
    }

    // Listen to when a player enters a new chunk
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;
            ChunkPos playerChunkPos = new ChunkPos(player.chunkCoordX, player.chunkCoordZ);

            if (chunkOwnershipMap.containsKey(playerChunkPos)) {
                Faction faction = chunkOwnershipMap.get(playerChunkPos);

                if (!faction.getMembers().contains(player)) {
                    player.sendMessage(new TextComponentString("Warning: You are in " + faction.getName() + "'s territory!"));
                }
            }
        }
    }

    // Prevent unauthorized block placement
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        ChunkPos chunkPos = new ChunkPos(event.getPos());
        EntityPlayer player = event.getPlayer();

        if (chunkOwnershipMap.containsKey(chunkPos)) {
            Faction faction = chunkOwnershipMap.get(chunkPos);
            Faction playerFaction = getPlayerFaction(player);  // Implement this helper function

            // Check if the player is a member of the faction or if their faction is at war
            if (playerFaction != null && (faction.getMembers().contains(player) || FactionManager.getInstance().areAtWar(faction, playerFaction))) {
                // Allow the block break
            } else {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString("You cannot break blocks in " + faction.getName() + "'s territory!"));
            }
        }
    }

    // Similarly for block placement...
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        ChunkPos chunkPos = new ChunkPos(event.getPos());
        EntityPlayer player = event.getPlayer();

        if (chunkOwnershipMap.containsKey(chunkPos)) {
            Faction faction = chunkOwnershipMap.get(chunkPos);
            Faction playerFaction = getPlayerFaction(player);

            if (playerFaction != null && (faction.getMembers().contains(player) || FactionManager.getInstance().areAtWar(faction, playerFaction))) {
                // Allow the block place
            } else {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString("You cannot place blocks in " + faction.getName() + "'s territory!"));
            }
        }
    }

    // Helper method to find which faction a player belongs to
    public static Faction getPlayerFaction(EntityPlayer player) {
        for (Faction faction : FactionManager.getInstance().getFactions()) {
            if (faction.getMembers().contains(player)) {
                return faction;
            }
        }
        return null;
    }

    // Load claimed chunks into the map when the chunk loads
    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        ChunkPos chunkPos = event.getChunk().getPos();

        for (Faction faction : FactionManager.getInstance().getFactions()) {
            if (faction.getClaimedChunks().contains(chunkPos)) {
                chunkOwnershipMap.put(chunkPos, faction);
            }
        }
    }

    // Clean up chunk from the map when the chunk unloads
    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        ChunkPos chunkPos = event.getChunk().getPos();
        chunkOwnershipMap.remove(chunkPos);
    }
}

