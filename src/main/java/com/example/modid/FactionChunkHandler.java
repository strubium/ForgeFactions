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
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class FactionChunkHandler {

    private final Map<ChunkPos, Faction> chunkOwnershipMap = new HashMap<>();
    private static final Map<EntityPlayer, Faction> playerFactionMap = new HashMap<>();
    private FactionSavedData factionSavedData;

    public FactionChunkHandler(World world) {
        this.factionSavedData = new FactionSavedData(world);
        loadFactionData();
    }

    private void loadFactionData() {
        // Load factions from saved data into the chunkOwnershipMap
        for (Map.Entry<String, Faction> entry : factionSavedData.getFactions().entrySet()) {
            Faction faction = entry.getValue();
            for (ChunkPos chunkPos : faction.getClaimedChunks()) {
                chunkOwnershipMap.put(chunkPos, faction);
            }
        }
        System.out.println("Loaded factions into chunk ownership map.");
    }

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

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        handleBlockAction(event.getPos(), event.getPlayer(), event, false);
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        handleBlockAction(event.getPos(), event.getPlayer(), event, true);
    }

    private void handleBlockAction(BlockPos blockPos, EntityPlayer player, BlockEvent event, boolean isPlacing) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        Faction faction = chunkOwnershipMap.get(chunkPos);

        if (faction != null) {
            Faction playerFaction = FactionManager.getInstance(player.getEntityWorld()).getFactionByPlayer(player.getGameProfile()).orElse(null);

            if (playerFaction == null || (!faction.getMembers().contains(player)
                    && !FactionManager.getInstance(player.getEntityWorld()).areAtWar(faction, playerFaction))) {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString("You cannot " + (isPlacing ? "place" : "break") +
                        " blocks in " + faction.getName() + "'s territory!"));
            }
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        ChunkPos chunkPos = event.getChunk().getPos();
        for (Faction faction : factionSavedData.getFactions().values()) {
            if (faction.getClaimedChunks().contains(chunkPos)) {
                chunkOwnershipMap.put(chunkPos, faction);
            }
        }
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        ChunkPos chunkPos = event.getChunk().getPos();
        chunkOwnershipMap.remove(chunkPos);
    }
}
