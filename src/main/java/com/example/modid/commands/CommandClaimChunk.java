package com.example.modid.commands;

import com.example.modid.Faction;
import com.example.modid.FactionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;

public class CommandClaimChunk extends CommandBase {

    @Override
    public String getName() {
        return "claimchunk";  // The name of the command
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimchunk - Claims the chunk you are currently in for your faction.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) sender;

            FactionManager factionManager = FactionManager.getInstance();
            Faction playerFaction = getPlayerFaction(player);

            if (playerFaction != null) {
                ChunkPos chunkPos = new ChunkPos(player.chunkCoordX, player.chunkCoordZ);

                if (!playerFaction.getClaimedChunks().contains(chunkPos)) {
                    playerFaction.claimChunk(chunkPos);
                    player.sendMessage(new TextComponentString("You have claimed the chunk at " + chunkPos.toString() + " for your faction."));
                } else {
                    player.sendMessage(new TextComponentString("This chunk is already claimed by your faction."));
                }
            } else {
                player.sendMessage(new TextComponentString("You must be in a faction to claim chunks."));
            }
        } else {
            sender.sendMessage(new TextComponentString("This command can only be used by a player."));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;  // All players can use this command
    }

    private Faction getPlayerFaction(EntityPlayerMP player) {
        for (Faction faction : FactionManager.getInstance().getFactions()) {
            if (faction.getMembers().contains(player)) {
                return faction;
            }
        }
        return null;
    }
}
