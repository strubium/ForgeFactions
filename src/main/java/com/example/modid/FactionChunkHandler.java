package com.example.modid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
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
import java.util.Optional;
import java.util.Set;

public class FactionChunkHandler {

    // Optimized data structure to quickly look up which faction owns a chunk
    private final Map<ChunkPos, Faction> chunkOwnershipMap = new HashMap<>();
    // Optimized map for storing player faction for fast lookup
    private static final Map<EntityPlayer, Faction> playerFactionMap = new HashMap<>();


    // Listen to when a player enters a new chunk
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;
            ChunkPos playerChunkPos = new ChunkPos(player.chunkCoordX, player.chunkCoordZ);

            Faction faction = chunkOwnershipMap.get(playerChunkPos);
            if (faction != null && !faction.getMembers().contains(player)) {
                player.sendMessage(new TextComponentString("Warning: You are in " + faction.getName() + "'s territory!"));
            }
        }
    }

    // Prevent unauthorized block placement or breaking
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        handleBlockAction(event.getPos(), event.getPlayer(), event, false);
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        handleBlockAction(event.getPos(), event.getPlayer(), event, true);
    }

    // Helper to handle block breaking and placing logic
    private void handleBlockAction(BlockPos blockPos, EntityPlayer player, BlockEvent event, boolean isPlacing) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        Faction faction = chunkOwnershipMap.get(chunkPos);

        if (faction != null) {
            Faction playerFaction = getPlayerFaction(player).orElse(null);

            // Check if the player is in the faction or their faction is at war with the owner faction
            if (playerFaction == null || (!faction.getMembers().contains(player)
                    && !FactionManager.getInstance(player.getEntityWorld()).areAtWar(faction, playerFaction))) {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString("You cannot " + (isPlacing ? "place" : "break") +
                        " blocks in " + faction.getName() + "'s territory!"));
            }
        }
    }

    // Helper method to find which faction a player belongs to using an Optional
    public static Optional<Faction> getPlayerFaction(EntityPlayer player) {
        return Optional.ofNullable(playerFactionMap.computeIfAbsent(player, p -> {
            for (Faction faction : FactionManager.getInstance(player.getEntityWorld()).getFactions()) {
                if (faction.getMembers().contains(p)) {
                    return faction;
                }
            }
            return null;
        }));
    }

    // Load claimed chunks into the map when the chunk loads
    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        ChunkPos chunkPos = event.getChunk().getPos();
        for (Faction faction : FactionManager.getInstance(event.getWorld()).getFactions()) {
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
