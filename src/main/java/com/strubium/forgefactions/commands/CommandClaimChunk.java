package com.strubium.forgefactions.commands;

import com.strubium.forgefactions.faction.Faction;
import com.strubium.forgefactions.faction.FactionSavedData;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.UUID;

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
            World world = player.getEntityWorld();

            // Get the FactionSavedData instance to access and modify factions
            FactionSavedData factionData = new FactionSavedData(world);

            // Retrieve the player's faction
            Faction playerFaction = getPlayerFaction(player, factionData);

            if (playerFaction != null) {
                ChunkPos chunkPos = new ChunkPos(player.chunkCoordX, player.chunkCoordZ);

                // Check if the chunk is already claimed by the faction
                if (!playerFaction.getClaimedChunks().contains(chunkPos)) {
                    playerFaction.claimChunk(chunkPos);  // Claim the chunk for the faction
                    factionData.save();  // Save the updated faction data

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

    private Faction getPlayerFaction(EntityPlayerMP player, FactionSavedData factionData) {
        UUID playerUUID = player.getUniqueID();  // Get the player's UUID

        for (Faction faction : factionData.getFactions().values()) {
            // Loop through each member in the faction, which are EntityPlayer objects
            for (GameProfile member : faction.getMembers()) {
                if (member.getId().equals(playerUUID)) {
                    return faction;  // Return the faction if the player's UUID matches a member's UUID
                }
            }
        }
        return null;
    }
}
